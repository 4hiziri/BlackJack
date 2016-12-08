package acceptanceServer

import java.io.IOException
import java.net.Socket

import RoomServer

/*
 * クライアントとの通信し，入力によって部屋に振り分ける
 */ object AssignRoomThread {
  private val room_manager: RoomManager = new RoomManager(RoomServer.ROOM_LIMIT, RoomServer.ENTRY_LIMIT)
}

class AssignRoomThread(val socket: Socket) extends Thread {
  this.client = new Client(socket)
  private var client: Client = null

  // 入力されたルームナンバーを抽出し，Int型にパースする
  // 文字列から変換できない場合は-1を返す
  private def parseInpumRoomNumber(str_room_num: String): Int = {
    var input_num: Int = -1
    // Int型に変換出来なければ-1をinput_numに代入する
    try {
      input_num = str_room_num.toInt
    }
    catch {
      case e: NumberFormatException => {
        input_num = -1
      }
    }
    return input_num
  }

  /** ルームナンバーをクライアントに入力させ，その値を取り出す
    * IOExceptionが起きたら，再び入力を求める
    * guest(ランダムなルームで良い)なら-1が返る
    * 入力を許す値の範囲をmin, maxで指定する
    *
    * @param min 入力を許す値の下限，-1以下だとguestとの区別がつけられないため正常に動作しない，エラーを返すように
    * @param max 入力を許す値の上限
    */
  private def readRoomNumberFromClient(min: Int, max: Int): Int = {
    // クライアントからのメッセージ受信
    // クライアントにルームナンバーを入力してもらい，その値をroom_numに代入する
    var str_room_num: String = ""
    var room_id: Int = -1
    // 引数のエラーチェック
    if (min < 0) {
      throw new IllegalArgumentException
    }
    while (room_id < min) {
      {
        client.println("Input Room Number, " + min + "~" + max + " (or input 'guest')")
        // 入力をパースしてroom_idに代入する
        // 入力が正しくないなら，-1が入る
        str_room_num = client.read
        if (str_room_num.equalsIgnoreCase("guest")) {
          room_id = -1
          break //todo: break is not supported
        }
        // 入力を文字列から数値に変換する
        try {
          room_id = parseInpumRoomNumber(str_room_num)
        }
        catch {
          case e: NumberFormatException => {
            // 変換に失敗した場合，再度入力させる
            client.println("please input number: ")
            continue //todo: continue is not supported
          }
        }
        if (room_id < min || room_id > max) {
          // 範囲を超えた数値が入力された場合，room_id = -1として再度ループさせる
          client.println("please " + min + " < [input] < " + max)
          room_id = -1 // maxを超えた場合room_id > minとなってしまうため
          continue //todo: continue is not supported
        }
      }
    }
    return room_id
  }

  override def run() {
    var room_id: Int = -1
    // ストリームの確立
    try {
      client.openStream()
    }
    catch {
      case e: IOException => {
        e.printStackTrace()
      }
    }
    room_id = readRoomNumberFromClient(0, RoomServer.ROOM_LIMIT - 1)
    System.out.println("Access to Room" + room_id)
    AssignRoomThread.room_manager.moveToRoom(room_id, client)
    return
  }
}