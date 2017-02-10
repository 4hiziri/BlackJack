package gameServer

import akka.actor.{Actor, ActorRef}
import akka.pattern._
import gameServer.GameProcess.Run

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by seiya on 2017/02/09.
  */
class GameProcess(manager: ActorRef, player: CardGamePlayer) extends Actor {
  override def receive: Receive = {
    case Run => run()
    case _ =>
  }

  private def run(): Unit = {
    for (_ <- 1 to 2) player.receivesCard(Deck.drawWithoutJoker())
    while (!player.isDecided) {
      val msgQue = player.listen()
      for (msg <- msgQue) parseMsg(msg)
    }
  }

  private def parseMsg(msg: String): Unit = {
    msg match {
      case ":hit" => if (!player.isDecided) hit() else player.receivesMessage("You can't!")
      case ":stay" => stand()
      case ":hand" => player.receivesMessage(handToString(player.hand))
      case ":hand dealer" => {
        val futureHost = manager ? Host
        val host = Await.result(futureHost, Duration.Inf).asInstanceOf[CardGamePlayer]
        player.receivesMessage(handToString(host.hand))
      }
      case ":quit" => player.leaves()
      case _ => manager ! msg
    }
  }

  private def hit() = {
    player.receivesCard(Deck.drawWithoutJoker())
    player.receivesMessage("Draw Card!")
  }

  private def stand() = {
    player.isDecided = true
    player.receivesMessage("Stand!")
  }

  private def handToString(hand: Seq[Card]): String = hand.reduce((c1: Card, c2: Card) => c1 + ", " + c2)
}

object GameProcess {

  def score(rowHand: Seq[Card]): Int = {
    def toPoint(num: Int): Int =
      if (num == 11 || num == 12 || num == 13) 10
      else num

    var point = 0
    val hand = rowHand.map((card: Card) => toPoint(card.number))

    point = hand.map((num: Int) => if (num == 1) 11 else num).sum
    if (point > 21) point = hand.sum
    point
  }

  case object Run
}