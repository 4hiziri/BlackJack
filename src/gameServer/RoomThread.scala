package gameServer

import java.util.concurrent.ConcurrentLinkedQueue

import akka.actor.Actor

/**
  * 部屋を表すスレッド，その部屋の全プレイヤーの入出力を管理する
  * ルールに手札を処理させる
  */
class RoomThread(val id: Int, val max_entry: Int) extends Actor {
  this.host = new Host(ServerCom.getConnection)
  host_process = new BlackJack(host, this)
  private val room_id: Int = id
  private val player_waiting: ConcurrentLinkedQueue[Player] = new ConcurrentLinkedQueue[Player]
  // :TODO scala concurrent or Actor
  private val threads: util.ArrayList[BlackJack] = new util.ArrayList[BlackJack]
  // :TODO to Actor
  private var isStart: Boolean = false
  private[serverApp] var host: Host = null
  private var host_process: BlackJack = null

  def addPlayer_waiting(player: Player) {
    player_waiting.add(player)
  }

  def getRoomId: Int = {
    return room_id
  }

  def isStart: Boolean = {
    return isStart
  }

  override def run() {
    // initialize
    isStart = true
    var isFinishedAllPlayer: Boolean = true
    // main logic
    while (true) {
      {
        // player entry
        // check player num. if over, connection close
        checkEntry()
        // game start
        // hostの手が確定していないなら、プロセスをスタートさせる
        // 繰り返しゲームをするときのためここで処理する
        if (!host.getIsDecided) {
          host_process.hostPlay()
        }
        // 全てのプレイヤーが手を確定させたかをチェックする
        isFinishedAllPlayer = checkAllPlayerFinished
        // 手が決まったら、手の強さで勝敗を決定させる
        if (isFinishedAllPlayer && host.getIsDecided) {
          notifyResult()
          isFinishedAllPlayer = false
          clear()
        }
        // :TODO 賭け金システム実装
      }
    }
  }

  /**
    * 全てのプレイヤーの手が確定しているかどうかを判定する
    *
    * @return
    */
  private def checkAllPlayerFinished: Boolean = {
    val mem_num: Int = getMemberNum
    var i: Int = 0
    while (i < mem_num) {
      {
        //決めていないプレイヤーがいた時点でfalseを返す
        val player: Player = threads.get(i).player
        if (!player.getIsDecided) return false
      }
      {
        i += 1; i - 1
      }
    }
    // 親が手を確定させていないなら、falseを返す
    // ほぼ間違いなく終わっているはずなので、通常実行されない
    if (!host.getIsDecided) return false
    // 全てのプレイヤーが手を確定させていたら、trueを返す
    return true
  }

  /**
    * 参加待ちプレイヤーがいるかどうかチェックする
    * いた場合、参加可能なら参加させ、不可能なら接続を切断する
    * 状態を変更させる
    */
  private def checkEntry() {
    // 待ちプレイヤーがいた場合、entryProcedureを呼び、プレイヤーを参加させる
    while (!player_waiting.isEmpty) {
      {
        entryProcedure(player_waiting.poll)
      }
    }
    return
  }

  /**
    * playerが参加できるかどうかを判定し、可能ならPlayersに追加し，出来ないならplayerを切断する
    *
    * @return
    */
  private def entryProcedure(player: Player) {
    // ここで比較すると効率が悪い気がするが，そんなに多くのエントリーを想定していない
    // 良しとしておく
    // 再接続と部屋選択に困難はないはずだ
    val mem_num: Int = getMemberNum
    if (mem_num >= max_entry) {
      player.systemMessage("Room" + room_id + "は，満員です．")
      player.bye()
    }
    else {
      // 他のPlayerに参加したPlayerを通知
      systemNotifyAllPlayer(player + "が参加しました．")
      player.systemMessage("Room" + room_id + "にようこそ！")
      //players.add(player);
      val thread: BlackJack = new BlackJack(player, this)
      thread.start()
      threads.add(thread)
    }
    return
  }

  /**
    * 全てのプレイヤーにメッセージを表示する
    *
    * @param message
    */
  private[serverApp] def systemNotifyAllPlayer(message: String) {
    val player_num: Int = getMemberNum
    var i: Int = 0
    while (i < player_num) {
      {
        threads.get(i).player.systemMessage(message)
      }
      {
        i += 1;
        i - 1
      }
    }
    return
  }

  private def clear() {
    val len: Int = threads.size
    var i: Int = 0
    while (i < len) {
      {
        val thread: BlackJack = threads.get(i)
        renewThread(thread)
      }
      {
        i += 1;
        i - 1
      }
    }
    renewThread(host_process)
    return
  }

  /**
    * 次のゲームを開始できる状況にする
    *
    * @param thread
    */
  private def renewThread(thread: BlackJack) {
    thread synchronized {
      try {
        System.out.println("lock: " + room_id)
        thread.wait(3000)
      }
      catch {
        case e: InterruptedException => {
          e.printStackTrace()
        }
      }
      thread.resetPlayerStatus()
      thread.notify()
      System.out.println("release: " + room_id)
    }
  }

  /**
    * 勝ち負けを外部サーバで判定し、結果をプレイヤーに通知し、接続を切断する
    * :TODO 繰り返しゲームをできるようにし、賭け金システムを実装したい
    */
  private def notifyResult() {
    // 仕様により
    val WIN: Int = 1
    val LOSE: Int = -1
    val DRAW: Int = 0
    val judge: ServerJudge = new ServerJudge
    val players: util.ArrayList[Player] = new util.ArrayList[Player]
    val mem_num: Int = getMemberNum
    var i: Int = 0
    while (i < mem_num) {
      {
        players.add(threads.get(i).player)
      }
      {
        i += 1; i - 1
      }
    }
    val result_list: util.ArrayList[Integer] = judge.judge(host, players)
    var i: Int = 0
    while (i < result_list.size) {
      {
        val player: Player = players.get(i)
        val result: Int = result_list.get(i)
        if (result == WIN) {
          win(player)
        }
        else if (result == LOSE) {
          lose(player)
        }
        else if (result == DRAW) {
          draw(player)
        }
      }
      {
        i += 1; i - 1
      }
    }
  }

  /**
    * @param player
    */
  private def draw(player: Player) {
    player.systemMessage("YOU DRAW")
    return
  }

  def getMemberNum: Int = {
    return threads.size
  }

  /**
    * @param player
    */
  private def lose(player: Player) {
    player.systemMessage("YOU LOSE...")
    return
  }

  /**
    * @param player
    */
  private def win(player: Player) {
    player.systemMessage("YOU WIN!")
    player.systemMessage("Congratulation!")
    return
  }

  /**
    * 部屋を閉鎖する
    * 全てのプレイヤーがゲームを終了した段階でスレッドを終了させる
    * 全てのストリームの解放などを行う
    */
  private def closeRoom() {
    val mem_num: Int = getMemberNum
    var i: Int = 0
    while (i < mem_num) {
      {
        threads.get(i).player.bye
      }
      {
        i += 1; i - 1
      }
    }
    host.bye()
  }

  // 渡されたスレッドと同じIDのスレッドを削除し、threadsを先頭に詰める
  private[serverApp] def deleteThread(rm_thread: BlackJack) {
    threads.remove(rm_thread)
    return
  }

  private[serverApp] def playerNotifyAllPlayer(player: Player, message: String) {
    val player_num: Int = getMemberNum
    var i: Int = 0
    while (i < player_num) {
      {
        threads.get(i).player.message("Player" + player + ": " + message)
      }
      {
        i += 1; i - 1
      }
    }
    return
  }
}