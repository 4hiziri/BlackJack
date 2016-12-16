package acceptanceServer

import java.net.Socket

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * this Actor communicate client's socket and assign witch Room enter
  * receive socket as Message from RoomServer, throw socket to chosen Room
  */
class AssignRoomThread extends Actor {

  /**
    * receive socket and process Room Definition
    * Socket instance as Message is converted to Client instance here
    * so only accept socket as message.
    * if message isn't socket, it is ignored
    *
    * @return
    */
  override def receive: Receive = {
    case x: Socket => {
      val props = Props(classOf[Client], x) // :TODO Error handling, x is not Client
      val client = RoomServer.actor_system.actorOf(props, name = "Client")
      access(client)
    }
    case _ => // nothing to do
  }

  /**
    * client defines which room enter and access to that room
    *
    * @param client
    */
  private def access(client: ActorRef) = {
    val room_id = readRoomNumberFromClient(client, RoomServer.ROOM_LIMIT - 1)
    System.out.println("Access to Room" + room_id)
    RoomManager.moveToRoom(room_id, client)
  }

  /** have client input room number and get that number
    * if happen IOException, try again
    * if "guest" is inputted, convert to -1
    * x: input Int number. if min <= x <= max, accept x
    *
    * @param client is actor which has connection
    * @param max    is max of inputted num
    * @param min    is min of inputted num
    */
  private def readRoomNumberFromClient(client: ActorRef, max: Int, min: Int = 0): Int = {
    /**
      * if guest, return Some(-1)
      * if number, return Some(number)
      * if not number, return None
      * :TODO extract judgement whether guest or not and return at once if inputted guest
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

    // if inputted string cannot be converted to Int, input again
    var loop_flag: Boolean = true
    var inputted_num = 0
    // extract as function because duplicate. but i wonder if it is complicated
    while (loop_flag) {
      (loop_flag, inputted_num) = readInputNum match {
        case Some(x) if (x == -1) || (min <= x && x <= max) => (false, x)
        case _ => (true, 0)
      }
    }

    inputted_num
  }

  /**
    * string to int convert function
    * if cannot convert string, return None
    * string can be converted to int, return Some(int)
    *
    * @param str_num string to be going to be converted to int
    */
  private def toOptInt(str_num: String): Option[Int] = {
    // :TODO general, should extract?
    try {
      Option(str_num.toInt)
    } catch {
      case e: NumberFormatException => {
        None
      }
    }
  }
}
