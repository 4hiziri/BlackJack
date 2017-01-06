package gameServer



object ServerScore {
  private val address: String = ConnectionSetting.SERVER_SCORE_ADDRESS
  private val port: Int = ConnectionSetting.SERVER_SCORE_PORT
  private var conn: acceptanceServer.Client = null
}

class ServerScore() {
  /* // :TODO あとで纏める
   while (true) {
     {
       try {
         ServerScore.conn = new acceptanceServer.Client(new Socket(ServerScore.address, ServerScore.port))
       }
       catch {
         case e: IOException => {
           System.out.println("cannot connect ScoreServer, try again...")
           System.out.print(e)
           continue //todo: continue is not supported
         }
       }
       try {
         ServerScore.conn.openStream()
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

   def score(hand: util.ArrayList[Card]): Int = {
     var hand_str: String = ""
     var i: Int = 0
     while (i < hand.size) {
       {
         hand_str += hand.get(i).getNumber + ","
       }
       {
         i += 1; i - 1
       }
     }
     ServerScore.conn.println(hand_str.substring(0, hand_str.length - 1))
     var reply: String = ""
     while (reply == "") {
       {
         reply = ServerScore.conn.read
       }
     }
     return reply.toInt
   }

   def close() {
     ServerScore.conn.closeStream()
   }*/
}