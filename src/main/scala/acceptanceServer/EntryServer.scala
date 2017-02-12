package acceptanceServer

import java.net.InetSocketAddress

import akka.actor.{Actor, Props}
import akka.event.Logging
import akka.io.{IO, Tcp}

/**
  * Created by seiya on 2016/12/20.
  */
class EntryServer(address: String, port: Int) extends Actor {
  import Tcp._
  import context.system

  val log = Logging(Main.system, this) // :TODO make logger, as interface

  IO(Tcp) ! Bind(self, new InetSocketAddress(address, port))

  override def receive: Receive = {
    case b@Bound(localAddress) => {
      logMessage("Info: Open Port" + localAddress + " to accept connection")
    }
    case CommandFailed(_: Bind) => context stop self
    case Connected(remote, local) => throwToReceptionist()
    case _ => logMessage("Info: sent strange message")
  }

  private def throwToReceptionist(): Unit = {
    val connection = sender()
    val handler = context.actorOf(Props(classOf[Client], connection))
    val receipt = context.actorOf(Props(classOf[Receptionist]))

    connection ! Register(handler)
    receipt ! handler
  }

  private def logMessage(msg: String) {
    log.info("[" + this + "]" + msg + "\n")
  } // extract utility

  override def toString: String = "EntryServer"
}
