package gameServer

import akka.actor.{Actor, Props}
import gameServer.GameProcess._

/**
  * 部屋を表すスレッド，その部屋の全プレイヤーの入出力を管理する
  * ルールに手札を処理させる
  */
class RoomThread(val roomId: Int, val max_entry: Int) extends Actor {
  // :TODO ストラテジを分離
  private val testId = -1
  private val _host: Host = null
  private var playerSet: Set[CardGamePlayer] = Set()
  private var isStart = false

  override def receive: Receive = {
    case client: Player => join(client); self ! Check
    case Check => if (check()) result() else self ! Play
    case Play => play(); self ! Check // useless
    case player: Player => leave(player)
    case Host => sender() ! host()
    case msg: String => for (p <- playerSet) p.receivesMessage(msg)
    case _ =>
  }

  private def host(): Player = _host.clone()

  private def check(): Boolean = playerSet.forall((p: CardGamePlayer) => p.isDecided)

  private def result(): Unit = {
    def judge(p: CardGamePlayer): Result = GameProcess.judge(_host)(p)

    for (p <- playerSet) {
      val result: Result = judge(p)

      if (result == Win) win(p)
      else if (result == Lose) lose(p)
      else if (result == Draw) draw(p)
      // else // Erorr
    }

    initialize()
  }

  def initialize(): Unit = {
    playerSet.foreach(_.flushHand())
    playerSet.foreach(processRun)
  }

  /**
    * @param player
    */
  private def draw(player: CardGamePlayer) = player.receivesMessage("YOU DRAW")

  /**
    * @param player
    */
  private def lose(player: CardGamePlayer) = player.receivesMessage("YOU LOSE...")

  /**
    * @param player
    */
  private def win(player: CardGamePlayer) = player.receivesMessage("YOU WIN!")

  /*/**
    * 勝ち負けを外部サーバで判定し、結果をプレイヤーに通知し、接続を切断する
    * :TODO 繰り返しゲームをできるようにし、賭け金システムを実装したい
    */
  private def notifyResult() {
    // 仕様により
    val WIN: Int = 1
    val LOSE: Int = -1
    val DRAW: Int = 0
    val judge: ServerJudge = new ServerJudge
    val players: util.ArrayList[CardGamePlayer] = new util.ArrayList[CardGamePlayer]
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
        val player: CardGamePlayer = players.get(i)
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
*/

  private def join(client: Player): Unit = {
    val player = client.asInstanceOf[CardGamePlayer]
    playerSet = playerSet + player
    processRun(player)
  }

  private def processRun(player: CardGamePlayer): Unit = {
    val gp = context.actorOf(Props(classOf[GameProcess], self, player)) // :TODO pool
    gp ! Run
  }

  /* def getMemberNum: Int = {
     return threads.size
   }*/

  private def play(): Unit = {}

  private def leave(player: Player): Unit = {}

  /* /**
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
   }*/

  /*// 渡されたスレッドと同じIDのスレッドを削除し、threadsを先頭に詰める
  private[serverApp] def deleteThread(rm_thread: BlackJack) {
    threads.remove(rm_thread)
    return
  }*/

  /*private[serverApp] def playerNotifyAllPlayer(player: CardGamePlayer, message: String) {
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
  }*/
}