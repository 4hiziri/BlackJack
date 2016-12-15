package acceptanceServer

import java.net.Socket

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/*
 * クライアントとの通信し，入力によって部屋に振り分ける
 */

class AssignRoomThread extends Actor {

  override def receive: Receive = {
    case x: Socket => {
      val props = Props(classOf[Client], x)
      val client = RoomServer.actor_system.actorOf(props, name = "Client")
      access(client)
    }
    case _ => {}
  }

  private def access(client: ActorRef): Unit ={
    val room_id = readRoomNumberFromClient(client, RoomServer.ROOM_LIMIT - 1)
    System.out.println("Access to Room" + room_id)
    RoomManager.moveToRoom(room_id, client)
  }

  /**
    * string to int convert function
    * if cannot convert string, return None
    * string can be converted to int, return Some(int)
    * @param str_num string to be going to be converted to int
    */
  private def toOptInt(str_num: String): Option[Int] = { // :TODO general, should extract?
    try {
      Option(str_num.toInt)
    } catch {
      case e: NumberFormatException => {
        None
      }
    }
  }

  /** ルームナンバーをクライアントに入力させ，その値を取り出す
    * IOExceptionが起きたら，再び入力を求める
    * guest(ランダムなルームで良い)なら-1が返る
    * 入力を許す値の範囲をmin, maxで指定する
    *
    * @param max 入力を許す値の上限
    */
  private def readRoomNumberFromClient(client: ActorRef, max: Int, min: Int = 0): Int = {
    /**
     * クライアントに入力を促し、入力がguestなら-1として数字に変換する。変換できなければNoneが返る
     */
    def readInputNum: Option[Int] = {
      val future_input: Future[Any] = client ? ("read", "Input Room Number, " + min + "~" + max + " (or input 'guest')")
      // 入力をパースしてroom_idに代入する
      // 入力が正しくないなら，-1が入る

      // get Future-result, and wait until get value
      // and cast to String
      val str_room_num: String = Await.result(future_input, Duration.Inf).asInstanceOf[String]
      if (str_room_num.equalsIgnoreCase("guest")) {
        return Option(-1)
      }
      // 入力を文字列から数値に変換する

      val room_id_opt: Option[Int] = toOptInt(str_room_num)

      room_id_opt
    }

    while (true) { // :TODO bad method
      val room_id_opt: Option[Int] = readInputNum

      room_id_opt match {
        case Some(x) if x == -1 => return x
        case Some(x) if min <= x && x <= max => return x
      }
    }

    0 // no return this
  }
}
