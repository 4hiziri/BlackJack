package gameServer

import java.util.concurrent.TimeUnit

import acceptanceServer.{Get, Question}
import akka.actor.ActorRef
import akka.io.Tcp.PeerClosed
import akka.pattern._
import akka.util.Timeout

import scala.collection.immutable._
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class CardGamePlayer(id: Int, client: ActorRef) extends Player with Cloneable {
  var isDecided: Boolean = false
  var name: String = "Player" + id
  protected var _hand: Seq[Card] = Seq[Card]()

  def hand: Seq[Card] = _hand

  def answers(question: String): String = {
    implicit val timeout = Timeout(10, TimeUnit.MINUTES)
    // ? 実行時の暗黙タイムアウト設定
    val answer: Future[Any] = this.client ? Question(question)
    Await.result(answer, Duration.Inf).asInstanceOf[String]
  }

  def listen(): Queue[String] = {
    implicit val timeout = Timeout(5000, TimeUnit.MILLISECONDS)
    // ? 実行時の暗黙タイムアウト設定
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
  }

  override def toString: String = name

  def flushHand(): Unit = _hand = Seq[Card]()
}