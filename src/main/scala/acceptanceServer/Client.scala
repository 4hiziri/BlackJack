package acceptanceServer

import akka.actor.{Actor, ActorRef}
import akka.event.Logging
import akka.io.Tcp
import akka.util.ByteString
/**
  * クライアントごとにストリームを管理するためのクラス
  */
class Client(connection: ActorRef) extends Actor {
  import Tcp._

  val log = Logging(Main.system, this)
  // use log utility

  override def receive: Receive = {
    case Received(data) => parseCmd(data.utf8String)
    case data: String => connection ! makeWrite(data)
    case Question(data) => {
      val answer = sender()
      connection ! makeWrite(data)
      context.become({
        case Received(data) => answer ! data.decodeString("UTF-8").stripLineEnd; context.become(receive)
        case _ => println("Client Question Error")
      }, discardOld = false)
    }
    case PeerClosed => context stop self
    case _ => log.info("receive strange data")
  }

  private def parseCmd(cmd: String) = {
    cmd.dropRight(2) match {
      // last 2 char of message is useless, so drop them
      case ":quit" => makeWrite("connection close"); sender() ! Close
      case msg: String => println(msg)
      // case msg: String => println("length = " + msg.length() + ": " + msg) // do nothing now
      case _ => println("error " + cmd)
    }
  }

  private def makeWrite(data: String) = Write(ByteString(data + "\n"))
}