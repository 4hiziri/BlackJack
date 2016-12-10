package acceptanceServer

import java.net.ServerSocket

import akka.actor.{ActorSystem, Props}

object RoomServer {
  // :TODO extract this setting and include at runtime
  private[serverApp] val ROOM_LIMIT: Int = 10
  private[serverApp] val ENTRY_LIMIT: Int = 10
  private val PORT: Int = 59630
  val actor_system = ActorSystem("Room_Server") // :TODO should i extract?

  def main(args: Array[String]) {
    val sSock: ServerSocket = new ServerSocket(PORT) // 59630番ポートをサーバとして起動
    val props = Props(classOf[AssignRoomThread])

    while (true) {
      val client_sock = sSock.accept // クライアントからの接続待ち
      // スレッドの生成と起動
      actor_system.actorOf(props, name = "AssignRoomThread") ! client_sock
    }
  }
}