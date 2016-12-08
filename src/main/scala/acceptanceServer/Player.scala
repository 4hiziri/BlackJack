package acceptanceServer

class Player(val client: acceptanceServer.Client) {
  this.client = client
  private var isDecided: Boolean = false
  private[serverApp] var client: acceptanceServer.Client = null
  protected var hand: util.ArrayList[acceptanceServer.Card] = new util.ArrayList[acceptanceServer.Card]
  // :TODO 名前の実装
  private var id: Int = -1

  def setId(id: Int) {
    this.id = id
    return
  }

  def receiveCard(card: acceptanceServer.Card) {
    this.hand.add(card)
    systemMessage("カード " + card.getNumber + "を引きました")
    return
  }

  @SuppressWarnings(Array("unchecked")) def getCard: util.ArrayList[acceptanceServer.Card] = {
    return hand.clone.asInstanceOf[util.ArrayList[acceptanceServer.Card]]
  }

  def message(str: String) {
    client.println(str)
    return
  }

  def systemMessage(msg: String) {
    client.println("System: " + msg)
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

  private[serverApp] def setIsDecided(bool: Boolean) {
    isDecided = bool
    return
  }

  private[serverApp] def getIsDecided: Boolean = {
    return isDecided
  }
}