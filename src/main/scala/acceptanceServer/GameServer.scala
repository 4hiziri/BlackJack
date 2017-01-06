package acceptanceServer

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef}
import akka.event.Logging
import akka.io.{IO, Tcp}

/**
  * Created by seiya on 2016/12/18.
  */
class GameServer(listener: ActorRef) extends Actor {

  import Tcp._
  import context.system

  val log = Logging(Main.system, this)
  // :TODO extract to config_file
  private val GAME_SERVER_ADDRESS = "localhost"
  private val GAME_SERVER_PORT = 59630

  IO(Tcp) ! Connect(new InetSocketAddress(GAME_SERVER_ADDRESS, GAME_SERVER_PORT))

  override def receive = {
    case CommandFailed(_: Connect) =>
      log.info(s"Failed: cannot connect to $GAME_SERVER_ADDRESS")
      context stop self

    // :TODO implement method
    case c@Connected(remote, local) => {
      listener ! c

      val connection = sender()
      connection ! Register(self)
      context become {
        case "close" => {
          connection ! Close
        }
        case _: ConnectionClosed => {
          listener ! "connection closed"
          context stop self
        }
        case Received(data) => {
          log.info(s"$listener Received $data")
          listener ! ("print", data)
        }
        case data => {
          // :TODO redesign it
          connection ! data
        }
      }
    }
  }
}
