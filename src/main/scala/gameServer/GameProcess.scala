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
      case ":bet" =>
      case ":hit" => if (!player.isDecided) hit() else player.receivesMessage("You can't!")
      case ":stay" => stand()
      case ":hand" => player.receivesMessage(handToString(player.hand))
      case ":hand dealer" => {
        val futureHost = manager ? Host
        val host = Await.result(futureHost, Duration.Inf).asInstanceOf[CardGamePlayer]
        player.receivesMessage(handToString(host.hand))
      }
      case ":double down" =>
      case ":split" =>
      case ":surrender" =>
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
  /**
    * return does p2 win against p1
    *
    * @param p1 Host
    * @param p2 CardGamePlayer
    * @return
    */
  def judge(p1: Host)(p2: CardGamePlayer): Result = {
    val scoreP1 = score(p1.hand)
    val scoreP2 = score(p2.hand)

    if (isWin(scoreP1, scoreP2)) Lose
    else if (isLose(scoreP1, scoreP2)) Win
    else if (isDraw(scoreP1, scoreP2)) Draw
    else Draw // :TODO error handling
  }

  /**
    * return score of hand
    *
    * @param rowHand
    * @return
    */
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

  // :TODO logic is shared and adopt more efficient way?
  private def isWin(s1: Int, s2: Int): Boolean = isBust(s2) ||
    !isBust(s1) && !isBust(s2) && s1 > s2

  def isBust(score: Int): Boolean = score > 21

  private def isLose(s1: Int, s2: Int): Boolean = !isBust(s2) && isBust(s1) ||
    !isBust(s1) && !isBust(s2) && s1 < s2

  private def isDraw(s1: Int, s2: Int): Boolean = !isBust(s1) && !isBust(s2) && s1 == s2

  sealed class Result

  final case object Win extends Result

  final case object Lose extends Result

  final case object Draw extends Result

  case object Run
}