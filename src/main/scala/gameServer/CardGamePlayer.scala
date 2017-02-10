package gameServer

import acceptanceServer.{Get, Question}
import akka.actor.ActorRef
import akka.io.Tcp.PeerClosed
import akka.pattern._

import scala.collection.immutable._
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class CardGamePlayer(id: Int, client: ActorRef) extends Player with Cloneable {
  var isDecided: Boolean = false
  var name: String = "Player" + id
  private var _hand: Seq[Card] = Seq[Card]()

  def hand: Seq[Card] = _hand

  def answers(question: String): String = {
    val answer: Future[Any] = this.client ? Question(question)
    Await.result(answer, Duration.Inf).asInstanceOf[String]
  }

  def listen(): Queue[String] = {
    val que: Future[Any] = client ? Get
    Await.result(que, Duration.Inf).asInstanceOf[Queue[String]]
  }

  def leaves(): Unit = {
    receivesMessage("Good bye!")
    client ! PeerClosed
  }

  def receivesMessage(str: String): Unit = this.client ! str

  override def clone(): CardGamePlayer = {
    val newPlayer = new CardGamePlayer(this.id, client) // delete client?
    for (card <- _hand) newPlayer.receivesCard(card)
    newPlayer.isDecided = this.isDecided
    newPlayer.name = this.name
    newPlayer
  }

  def receivesCard(card: Card) {
    _hand = _hand :+ card
    receivesMessage("カード " + card.number + "を引きました")
  }

  override def toString: String = name

  private[serverApp] def flushHand(): Unit = _hand = Seq[Card]()
}