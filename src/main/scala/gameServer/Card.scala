package gameServer

import gameServer.Card.Suit

// :TODO singleton and make playing card set
case class Card(number: Int, suit: Suit)

object Card {

  sealed abstract class Suit

  object Suit {

    case object Spade extends Suit

    case object Club extends Suit

    case object Heart extends Suit

    case object Diamond extends Suit

    case object Jorker extends Suit

  }

}