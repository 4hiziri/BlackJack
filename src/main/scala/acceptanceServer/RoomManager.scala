package acceptanceServer

import akka.actor.{ActorRef, Props}
import gameServer.{CardGamePlayer, RoomThread}

/**
  * 全ての部屋を管理する，部屋の数や部屋に入る数も管理する 各部屋へPlayerを送る どの部屋へ送るかはIDで管理する
  * :TODO あらかじめ実体化し、メッセージで処理をすすめるようにする。要調査
  */
object RoomManager {
  val maxEntry = Main.ENTRY_LIMIT
  val maxRoom = Main.ROOM_LIMIT
  var roomThreads: Seq[ActorRef] = Seq()

  for (i <- 1 until maxEntry) {
    val actor = Main.system.actorOf(Props(classOf[RoomThread], i, maxEntry))
    roomThreads :+= actor
  }

  def moveToRoom(id: Int, client: ActorRef): Unit = {
    // temporary ignore id
    val player = new CardGamePlayer(-1, client)
    roomThreads.head ! player
  }


  /* // clientを引数にPlayerを作成して，IDがroom_idのスレッドに送る
   // スレッドがスタートしていないなら、スタートさせる
   def moveToRoom(room_id: Int, client: ActorRef) {
     // guestならidは-1なので，空いているところに放り込む
     if (room_id == -1) {
       try {
         room_id = getVacantRoom
       }
       catch {
         case e: acceptanceServer.NoVacantRoomException => {
           client.println("空いている部屋がありません、時間を置いてアクセスしてください")
           client.closeStream()
         }
       }
     }
     val room: RoomThread = room_threads[room_id]

   }

   /**
     * 空いているroom_threadを返す
     * 先頭から探すようになっているが、先頭からでなくともよい
     * 楽なので先頭から探している
     * もし空いている部屋が無いならエラーを返す
     *
     * @return
     * @throws acceptanceServer.NoVacantRoomException
     */
   @throws[acceptanceServer.NoVacantRoomException]
   private def getVacantRoom: Int = { // :TODO send message that ask vacant
     val room_num: Int = room_threads.size
     var i: Int = 0
     while (i < room_num) {
       {
         val room: RoomThread = room_threads.get(i)
         if (room.getMemberNum >= max_entry) continue //todo: continue is not supported
         else return room.getRoomId
       }
       {
         i += 1; i - 1
       }
     }
     throw new acceptanceServer.NoVacantRoomException
   }*/
}