package acceptanceServer

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef}
import akka.io.Tcp.PeerClosed
import akka.pattern._
import akka.util.Timeout

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by seiya on 2016/12/22.
  */
class Receptionist extends Actor {
  // :TODO Logger

  override def receive: Receive = {
    case client: ActorRef => {
      val room_id = readValidRoomNumberFromClient(client, Main.ROOM_LIMIT - 1)
      System.out.println("Access to Room" + room_id) // :TODO logging
      // RoomManager.moveToRoom(room_id, client)
      client ! s"test ends here, RoomID = ${room_id}\n"
      client ! PeerClosed
      context stop self
    }
    case _ =>
  }

  /** have client input room number and get that number
    * if happen IOException, try again
    * if "guest" is inputted, return None
    * x: input Int number. if min <= x <= max, accept x and return Some(x)
    * :TODO how handling input -1? more flexible way
    *
    * @param client is actor which has connection
    * @param max    is max of inputted num
    */
  private def readValidRoomNumberFromClient(client: ActorRef, max: Int): Option[Int] = {
    val min = 0

    /**
      * if guest, return Some(-1)
      * if number, return Some(number)
      * if not number, return None
      */
    def readInputNum: Option[Int] = {
      // for akka ask method(?)
      implicit val timeout = Timeout(1, TimeUnit.MINUTES)
      val message_to_client = "Input Room Number, " + min + "~" + max + " (or input 'guest')"
      val future_input: Future[Any] = client ? Question(message_to_client)
      // get Future-result, and wait until get value
      // and cast to String
      val str_room_num: String = Await.result(future_input, Duration.Inf).asInstanceOf[String]

      // if inputted 'guest', return Some(-1)
      if (str_room_num.equalsIgnoreCase("guest")) {
        return Some(-1)
      }

      toOptInt(str_room_num)
    }

    // if inputted string cannot be converted to Int, input again
    // extract as function because duplicate. but i wonder if it is complicated
    while (true) {
      // :TODO limit loop
      readInputNum match {
        case Some(x) if x == -1 => return None
        case Some(x) if min <= x && x <= max => return Some(x)
        case _ => client ! "invalid input, again"
      }
    }

    Some(-1) // here cannot reached
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
