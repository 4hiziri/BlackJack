package acceptanceServer

import akka.actor.Actor
import akka.event.Logging
import akka.io.Tcp
/**
  * クライアントごとにストリームを管理するためのクラス
  */
class Client extends Actor {
  // :TODO use Actor.IO.Tcp
  import Tcp._

  val log = Logging(RoomServer.system, this) // use log utility

  override def receive: Receive = {
    // :TODO extract tuple to case class
    case Received(data) => print(data); sender() ! Write(data)
    //case Received(data: ByteString) if data == ByteString() =>
    case _ => log.info("receive strange data")
  }
}