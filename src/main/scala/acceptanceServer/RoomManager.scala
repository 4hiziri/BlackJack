package acceptanceServer

// import gameServer.RoomThread
class RoomThread(testi: Int, testj: Int) {}

// dummy
/**
  * 全ての部屋を管理する，部屋の数や部屋に入る数も管理する 各部屋へPlayerを送る どの部屋へ送るかはIDで管理する
  * :TODO 1サーバ毎にルームとする方式に変更する
  */
object RoomManager {
  val max_entry = Main.ENTRY_LIMIT
  val max_room = Main.ROOM_LIMIT
  var room_threads: IndexedSeq[RoomThread] = Vector()

  for (i <- 1 until max_entry) {
    room_threads :+= new RoomThread(i, max_entry) // :TODO make some Actor
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