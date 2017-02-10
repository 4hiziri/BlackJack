package gameServer

import akka.actor.ActorRef

import scala.collection.immutable.Queue

class Host(val client: ActorRef) extends CardGamePlayer(-1, client) {
  override def receivesCard(card: Card) {
    _hand = _hand :+ card
  }

  override def listen(): Queue[String] = nextAct()

  private def nextAct(): Queue[String] = {
    val score = GameProcess.score(_hand)
    val act = Queue[String]()

    if (score < 17) act.enqueue(":hit")
    else if (17 <= score && score <= 21) act.enqueue(":stay")
    else act
  }
}

object Host