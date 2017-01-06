package gameServer

import acceptanceServer.Client

class Host(val client: Client) extends Player(client) {
  override def receiveCard(card: Card) {
    this.hand.add(card)
    return
  }

  override def systemMessage(msg: String) {
    return
  }
}