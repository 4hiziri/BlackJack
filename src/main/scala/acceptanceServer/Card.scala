package acceptanceServer

import Suit

class Card(val num: Int, val suit: Suit) {
  this.number = num
  this.suit = suit
  private var number: Int = 0
  private var suit: Suit = null

  def getNumber: Int = {
    return number
  }

  def getSuit: Suit = {
    return suit
  }
}