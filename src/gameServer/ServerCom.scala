package gameServer



object ServerCom {
  private val address: String = ConnectionSetting.SERVER_HOST_ADDRESS
  private val port: Int = ConnectionSetting.SERVER_HOST_PORT

  /*def getConnection: acceptanceServer.Client = {
    var client: acceptanceServer.Client = null
    while (true) {
      {
        try {
          client = new acceptanceServer.Client(new Socket(address, port))
        }
        catch {
          case e: IOException => {
            System.out.println("cannot connect, try again...")
            e.printStackTrace()
            continue //todo: continue is not supported
          }
        }
        try {
          client.openStream()
          break //todo: break is not supported
        }
        catch {
          case e: IOException => {
            e.printStackTrace()
            continue //todo: continue is not supported
          }
        }
      }
    }
    return client
  }*/
}