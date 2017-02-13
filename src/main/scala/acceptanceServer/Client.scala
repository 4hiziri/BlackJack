package acceptanceServer

import akka.actor.{Actor, ActorRef}
import akka.event.Logging
import akka.io.Tcp
import akka.util.ByteString

import scala.collection.immutable.Queue

/**
  * クライアントごとにストリームを管理するためのクラス
  */
class Client(connection: ActorRef) extends Actor {
  import Tcp._

  val log = Logging(Main.system, this)
  private var messageQue: Queue[String] = Queue()

  // use log utility

  override def receive: Receive = {
    case Received(data) => messageQue = messageQue.enqueue(data.utf8String.dropRight(2))
    case data: String => connection ! makeWrite(data)
    case Question(data) => {
      val answer = sender()
      connection ! makeWrite(data)
      context.become({
        case Received(data: ByteString) => answer ! data.decodeString("UTF-8").stripLineEnd; context.become(receive)
        case _ => println("Client Question Error")
      }, discardOld = false)
    }
    case Get => {
      sender() ! messageQue
      messageQue = Queue()
    }
    case PeerClosed => context stop self
    case strange => log.info("receive strange data: " + strange)
  }

  private def makeWrite(data: String) = Write(ByteString(data + "\n"))
}