package gameServer

import akka.actor.Actor

/**
  * Created by seiya on 2017/02/09.
  *
  * implement game logic
  * if client is sent, call join(client) and send Check to itself
  * if Play is sent, call play and send Check to itself
  * if Check is sent, call check() and if it return true, call notify.
  * if not, send Play() to itself
  *
  * check() - check() all whether game finished or not, implement how to check game state is end here
  * notify() - notify() players game result, implement how to notify players here
  * join(Client) - join(Client) adds player to game, implement how to add players to game here
  * play() - play() run game process, implement how to play game here and recommend processing concurrent
  * leave(Player) - leave(Player) have player leave game, implement how to leave from game
  */
abstract class Room extends Actor {
  override def receive: Receive = {
    case client: Player => join(client); self ! Check
    case Check => if (check()) result() else self ! Play
    case Play => play(); self ! Check
    case player: Player => leave(player)
    case Host => sender() ! host()
    case _ =>
  }

  def host() = null

  def check(): Boolean

  def result(): Unit

  def join(client: Player): Unit

  def play(): Unit

  def leave(player: Player): Unit
}