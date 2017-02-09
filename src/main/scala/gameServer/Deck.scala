package gameServer

import gameServer.Card.Suit

import scala.util.Random

object Deck {
  def drawWithJoker(): Card = {
    val suitPool = Seq(Suit.Spade, Suit.Club, Suit.Heart, Suit.Diamond, Suit.Jorker)
    draw(suitPool, 54)
  }

  private def draw(suitPool: Seq[Suit], cardNum: Int): Card = {
    def rangeCheck(num: Int, suit: Suit): Option[Card] =
      if (0 <= num && num <= 12) Some(Card(num + 1, suit))
      else None

    var rand = Random.nextInt(cardNum)
    var card: Option[Card] = None

    for (suit <- suitPool) {
      if (card.isEmpty) card = rangeCheck(rand, suit)
      rand -= 13
    }

    card.get
  }

  def drawWithoutJoker(): Card = {
    val suitPool = Seq(Suit.Spade, Suit.Club, Suit.Heart, Suit.Diamond)
    draw(suitPool, 52)
  }
}