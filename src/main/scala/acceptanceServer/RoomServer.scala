package acceptanceServer

import java.net.ServerSocket

import akka.actor.{ActorSystem, Props}

/**
  * this object accepts connection
  * socket of connection is thrown to AssignRoomThread Actor from here
  * this object only processes socket throwing
  */
object RoomServer {
  // :TODO extract this setting and include at runtime
  private[serverApp] val ROOM_LIMIT: Int = 10 // Number of Room, Limitation
  private[serverApp] val ENTRY_LIMIT: Int = 10 // Number of Player in one Room, Limitation
  private val PORT: Int = 59630 // Port for this program
  val actor_system = ActorSystem("Room_Server") // :TODO should i extract?

  /**
    * main function
    * start waiting connection and process it if someone connects
    * @param args nothing :TODO receive port num here?
    */
  def main(args: Array[String]) {
    val sSock: ServerSocket = new ServerSocket(PORT) // wait on 59630-port
    // number of AssignRoomThread is the same of simultaneous connection's one
    // so generate Actor when this accepts connection
    val props = Props(classOf[AssignRoomThread])

    while (true) {
      val client_sock = sSock.accept // wait being connected
      // generate Actor and tell the socket as Message
      actor_system.actorOf(props, name = "AssignRoomThread") ! client_sock
    }
  }
}