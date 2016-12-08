package acceptanceServer

import java.io.IOException
import java.net.Socket

object ServerJudge {
  private val address: String = ConnectionSetting.SERVER_JUDGE_ADDRESS
  private val port: Int = ConnectionSetting.SERVER_JUDGE_PORT
}

class ServerJudge() {
  while (true) {
    {
      try {
        conn = new acceptanceServer.Client(new Socket(ServerJudge.address, ServerJudge.port))
        server_score = new ServerScore
      }
      catch {
        case e: IOException => {
          System.out.print(e)
          continue //todo: continue is not supported
        }
      }
      try {
        conn.openStream()
        break //todo: break is not supported
      }
      catch {
        case e: IOException => {
          e.printStackTrace()
          continue //todo: continue is not supported
        }
      }
    }
  }
  private var conn: acceptanceServer.Client = null
  private var server_score: ServerScore = null

  def judge(host: acceptanceServer.Player, players: util.ArrayList[acceptanceServer.Player]): util.ArrayList[Integer] = {
    val host_score: Int = server_score.score(host.getCard)
    val list: util.ArrayList[Integer] = new util.ArrayList[Integer]
    var i: Int = 0
    while (i < players.size) {
      {
        val hand: util.ArrayList[Card] = players.get(i).getCard
        val player_score: Int = server_score.score(hand)
        conn.println(host_score + " " + player_score)
        var reply: String = ""
        while (reply == "") {
          {
            reply = conn.read
          }
        }
        list.add(reply.toInt)
      }
      {
        i += 1; i - 1
      }
    }
    return list
  }
}