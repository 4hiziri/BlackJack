package gameServer

// import ConnectionSetting

object ServerCards {
  private val address: String = ConnectionSetting.SERVER_CARDS_ADDRESS
  private val port: Int = ConnectionSetting.SERVER_CARDS_PORT
  private val draw_command: String = "draw"
}

class ServerCards {
  /*while (true) {
    {
      try {
        this.conn = new acceptanceServer.Client(new Socket(ServerCards.address, ServerCards.port))
        conn.openStream()
        break //todo: break is not supported
      }
      catch {
        case e: ConnectException => {
          System.out.println("can not connect, try again...")
          e.printStackTrace()
          try {
            Thread.sleep(3000)
          }
          catch {
            case e1: InterruptedException => {
              e1.printStackTrace()
              return
            }
          }
          continue //todo: continue is not supported
        }
        case e: IOException => {
          e.printStackTrace()
        }
      }
    }
  }
  private var conn: acceptanceServer.Client = null

  def draw(draw_num: Int): util.List[Card] = {
    val cards: util.ArrayList[Card] = new util.ArrayList[Card]
    // カードを要求し、cardsに収める。
    // Suitは使わないため、適当にスペードとする。
    var i: Int = 0
    while (i < draw_num) {
      {
        conn.println(ServerCards.draw_command)
        var card_str: String = ""
        while (card_str == null || card_str == "") {
          {
            card_str = conn.read
          }
        }
        cards.add(new Card(card_str.toInt, Suit.SPADE))
      }
      {
        i += 1; i - 1
      }
    }
    return cards
  }

  def close() {
    conn.closeStream()
    return
  }*/
}