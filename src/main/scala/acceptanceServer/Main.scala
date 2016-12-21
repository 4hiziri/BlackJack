package acceptanceServer

import akka.actor.{ActorSystem, Props}

/**
  * this object accepts connection
  * socket of connection is thrown to AssignRoomThread Actor from here
  * this object only processes socket throwing
  */
object Main {
  // :TODO extract this setting and include at runtime
  val ROOM_LIMIT: Int = 10
  // Number of Room, Limitation
  val ENTRY_LIMIT: Int = 10
  // Number of Player in one Room, Limitation
  val system = ActorSystem("Entry_Server")
  // :TODO should i extract?
  private val PORT: Int = 59630 // Port for this program

  /**
    * main function
    * start waiting connection and process it if someone connects
    * @param args nothing :TODO receive port num here?
    */
  def main(args: Array[String]) {
    system.actorOf(Props(classOf[EntryServer], "localhost", PORT, Props(classOf[Client]))) // run Server Actor
  }
}