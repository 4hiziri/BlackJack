package acceptanceServer

/**
  * 全ての部屋を管理する，部屋の数や部屋に入る数も管理する 各部屋へPlayerを送る どの部屋へ送るかはIDで管理する
  */
class RoomManager(val max_room: Int, val max_entry: Int) {
  this.max_room = max_room
  this.max_entry = max_entry
  var i: Int = 0
  while (i < this.max_room) {
    {
      room_threads.add(new RoomThread(i, this.max_entry))
    }
    {
      i += 1; i - 1
    }
  }
  private[serverApp] val room_threads: util.List[RoomThread] = new util.ArrayList[RoomThread]
  private var max_room: Int = 0
  private var max_entry: Int = 0

  // clientを引数にPlayerを作成して，IDがroom_idのスレッドに送る
  // スレッドがスタートしていないなら、スタートさせる
  def moveToRoom(room_id: Int, client: Client) {
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
    val room: RoomThread = room_threads.get(room_id)
    val player: acceptanceServer.Player = new acceptanceServer.Player(client)
    player.setId(room.getMemberNum)
    room.addPlayer_waiting(player)
    if (!room.isStart) {
      room.start()
    }
    return
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
  private def getVacantRoom: Int = {
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
  }
}