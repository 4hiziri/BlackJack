package acceptanceServer

import java.net.InetSocketAddress

import akka.actor.{Actor, Props}
import akka.event.Logging
import akka.io.{IO, Tcp}

/**
  * Created by seiya on 2016/12/20.
  */
class EntryServer(address: String, port: Int, props: Props) extends Actor {

  import Tcp._
  import context.system

  val log = Logging(RoomServer.system, this)

  IO(Tcp) ! Bind(self, new InetSocketAddress(address, port))

  override def receive: Receive = {
    case b@Bound(localAddress) => {
      logMessage("Info: Open Port" + localAddress + " to accept connection")
    }
    case CommandFailed(_: Bind) => context stop self
    case Connected(remote, local) => {
      val handler = context.actorOf(Props[Client])
      val connection = sender()
      connection ! Register(handler)
    }
    case _ => logMessage("Info: Be sent strange message")
  }

  private def logMessage(msg: String) {
    log.info("[" + this + "]" + msg)
  } // extract utility


}
