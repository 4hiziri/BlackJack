package acceptanceServer

import java.io.IOException
import java.net.{ServerSocket, Socket}

object RoomServer {
  private[serverApp] val ROOM_LIMIT: Int = 10
  private[serverApp] val ENTRY_LIMIT: Int = 10
  private val PORT: Int = 59630

  @SuppressWarnings(Array("resource"))
  @throws[IOException]
  def main(args: Array[String]) {
    var sSock: ServerSocket = null // サーバ側のソケット
    var client_sock: Socket = null // クライアント側のソケット
    // サーバスタート
    sSock = new ServerSocket(PORT) // 59630番ポートをサーバとして起動
    System.out.println("Server Start")
    while (true) {
      {
        client_sock = sSock.accept // クライアントからの接続待ち
        // スレッドの生成と起動
        val t: AssignRoomThread = new AssignRoomThread(client_sock)
        t.start()
      }
    }
  }
}