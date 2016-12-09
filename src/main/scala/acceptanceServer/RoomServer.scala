package acceptanceServer

import java.net.ServerSocket

import akka.actor.{ActorSystem, Props}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}

object RoomServer {
  // :TODO extract this setting and include at runtime
  private[serverApp] val ROOM_LIMIT: Int = 10
  private[serverApp] val ENTRY_LIMIT: Int = 10
  private val PORT: Int = 59630
  private val actor_system = ActorSystem("Room_Server") // :TODO should i extract?

  def main(args: Array[String]) {
    val sSock: ServerSocket = new ServerSocket(PORT) // 59630番ポートをサーバとして起動
    val router = {
      val routees = Vector.fill(5) {
        val r = context.actorOf(Props[AssignRoomThread])
        context watch r
        ActorRefRoutee(r)
      }
      Router(RoundRobinRoutingLogic(), routees)
    }

    while (true) {
      val client_sock = sSock.accept // クライアントからの接続待ち
      // スレッドの生成と起動
      // :TODO convert to scala like thread
      router.route(client_sock, sender())
    }
  }
}