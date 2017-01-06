package acceptanceServer

import java.net.Socket

import akka.actor.{Actor, Props}

/**
  * OBSOLETE
  * this Actor communicate client's socket and assign witch Room enter
  * receive socket as Message from Main, throw socket to chosen Room
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
      val client = Main.system.actorOf(props, name = "Client")
    }
    case _ => // nothing to do
  }
}
