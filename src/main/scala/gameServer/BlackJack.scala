package gameServer

object BlackJack {
  private val server_score: gameServer.ServerScore = new gameServer.ServerScore
}

class BlackJack private[serverApp](val player: gameServer.Player, val parent: RoomThread) extends Thread {
  this.parent = parent
  this.player = player
  this.dealer = new gameServer.ServerCards
  this.isFinished = false
  initializeHand()
  private var isFinished: Boolean = false // threadが終了しているかどうか
  private[serverApp] var player: gameServer.Player = null
  private var dealer: gameServer.ServerCards = null
  private var parent: RoomThread = null

  /**
    * run thread, process game routine
    */
  override def run() {
    while (!isFinished) {
      {
        val command: String = player.listen
        parseCommand(command)
      }
    }
    dealer.close()
    parent.deleteThread(this)
    return
  }

  /**
    * process player as host
    */
  def hostPlay() {
    while (!player.getIsDecided) {
      {
        var str_hand: String = ""
        val player_hand: util.ArrayList[gameServer.Card] = player.getCard
        // サーバへ送る文字列を、適切にフォーマットする
        // 今は、1文字の区切りを入れる形式になっているため、","を間に挟んでいる
        var i: Int = 0
        while (i < player_hand.size) {
          {
            str_hand += player_hand.get(i).getNumber + ","
          }
          {
            i += 1; i - 1
          }
        }
        // 最後に無駄な","がついてしまうので、取り除く
        str_hand = str_hand.substring(0, str_hand.length - 1)
        // 送信する
        player.message(str_hand)
        // 送信した手によってサーバが行動するので、手が確定するまで続ける
        parseCommand(player.client.read)
      }
    }
    return
  }

  /**
    * 命令文字列から関数を実行し、継続するかどうかの真偽値を返す
    *
    * @param command
    * @return
    */
  // :TODO to void, use isDecided
  private def parseCommand(command: String) {
    if (command == null) return
    command match {
      case "hit" =>
        if (player.getIsDecided) break //todo: break is not supported
        hit()
        break //todo: break is not supported
      case "stand" =>
        stand()
        break //todo: break is not supported
      case "hand" =>
        printPlayerHand()
        break //todo: break is not supported
      case "hand dealer" =>
        printHostHand()
        break //todo: break is not supported
      case "quit" =>
        quitGame()
        break //todo: break is not supported
      case _ =>
        System.out.println("chat log:[" + player + "]: " + command)
        printPlayerMessage(command)
        break //todo: break is not supported
    }
    return
  }

  /**
    * disconnect player's connection
    */
  private def quitGame() {
    // :TODO 退出処理
    player.systemMessage("ゲームを終了します")
    parent.systemNotifyAllPlayer(player + "が退出しました")
    player.bye()
    isFinished = true
  }

  /**
    * print one player message to all player, which is added prefix "Player[ID]: [msg]"
    *
    * @param command
    */
  private def printPlayerMessage(command: String) {
    parent.playerNotifyAllPlayer(player, command)
    return
  }

  /**
    * print host's hand to only player
    */
  private def printHostHand() {
    player.systemMessage("親の手は、")
    player.systemMessage(handToStrHostFormat(parent.host.getCard))
    return
  }

  /**
    * @param hand
    * @return
    */
  private def handToStrHostFormat(hand: util.ArrayList[gameServer.Card]): String = {
    var hand_str: String = ""
    hand_str += hand.get(0).getNumber + ", "
    var i: Int = 1
    while (i < hand.size) {
      {
        hand_str += "*" + ", "
      }
      {
        i += 1;
        i - 1
      }
    }
    return hand_str.substring(0, hand_str.length - 2)
  }

  /**
    * print player's hand to only player
    */
  private def printPlayerHand() {
    player.systemMessage("あなたの手は、")
    player.systemMessage(handToStr(player.getCard))
    return
  }

  /**
    * @param hand
    * @return
    */
  private def handToStr(hand: util.ArrayList[gameServer.Card]): String = {
    var hand_str: String = ""
    var i: Int = 0
    while (i < hand.size) {
      {
        hand_str += hand.get(i).getNumber + ", "
      }
      {
        i += 1;
        i - 1
      }
    }
    return hand_str.substring(0, hand_str.length - 2)
  }

  /**
    * draw card and added to player's hand
    */
  private def hit() {
    player.systemMessage("ヒットします")
    player.receiveCard(dealer.draw(1).get(0))
    val isBust: Boolean = isBust // バストしたかどうか調べ、出力を行う
    if (isBust) {
      player.systemMessage("バストしてしまった!")
    }
    player.setIsDecided(isBust)
    return
  }

  /**
    * player's member var, isDecided, false -> true
    */
  private def stand() {
    player.setIsDecided(true)
    player.systemMessage("スタンドしました")
    return
  }

  /**
    * check player's hand whether bust or not
    *
    * @return
    */
  private def isBust: Boolean = {
    val score: Int = BlackJack.server_score.score(player.getCard)
    if (score > 21) return true
    else if (score > 0) return false
    else return false
  }

  /**
    * reset player's hand and isDecided true -> false
    *
    * @param player
    */
  private[serverApp] def resetPlayerStatus() {
    player.flushHand()
    player.setIsDecided(false)
    initializeHand()
  }

  /**
    * initialize player's hand, draw two cards
    */
  private def initializeHand() {
    val cards: util.ArrayList[gameServer.Card] = dealer.draw(2).asInstanceOf[util.ArrayList[gameServer.Card]]
    val length: Int = cards.size
    // :TODO receiveCardsに
    var i: Int = 0
    while (i < length) {
      {
        player.receiveCard(cards.get(i))
      }
      {
        i += 1;
        i - 1
      }
    }
    return
  }
}