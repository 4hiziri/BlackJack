package gameServer

class Player(val client: acceptanceServer.Client) {
  this.client = client
  protected var hand: util.ArrayList[Card] = new util.ArrayList[Card]
  private var isDecided: Boolean = false
  private[serverApp] var client: acceptanceServer.Client = null
  // :TODO 名前の実装
  private var id: Int = -1

  def setId(id: Int) {
    this.id = id
    return
  }

  def receiveCard(card: Card) {
    this.hand.add(card)
    systemMessage("カード " + card.getNumber + "を引きました")
    return
  }

  def systemMessage(msg: String) {
    client.println("System: " + msg)
    return
  }

  @SuppressWarnings(Array("unchecked")) def getCard: util.ArrayList[Card] = {
    return hand.clone.asInstanceOf[util.ArrayList[Card]]
  }

  def message(str: String) {
    client.println(str)
    return
  }

  def listen: String = {
    return client.read
  }

  def bye() {
    systemMessage("さよなら!")
    client.closeStream()
    return
  }

  override def toString: String = {
    return "Player" + id
  }

  private[serverApp] def flushHand() {
    hand.clear()
    return
  }

  private[serverApp] def getIsDecided: Boolean = {
    return isDecided
  }

  private[serverApp] def setIsDecided(bool: Boolean) {
    isDecided = bool
    return
  }
}