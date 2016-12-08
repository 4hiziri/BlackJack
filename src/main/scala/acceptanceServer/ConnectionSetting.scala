package acceptanceServer

/**
  * for convenient
  *
  * @author seiya
  *
  */
object ConnectionSetting {
  private[serverApp] val SERVER_CARDS_PORT: Int = 50000
  private[serverApp] val SERVER_JUDGE_PORT: Int = 50000
  private[serverApp] val SERVER_SCORE_PORT: Int = 50001
  private[serverApp] val SERVER_HOST_PORT: Int = 50000
  private[serverApp] val SERVER_CARDS_ADDRESS: String = "10.10.3.51"
  private[serverApp] val SERVER_JUDGE_ADDRESS: String = "10.10.3.50"
  private[serverApp] val SERVER_SCORE_ADDRESS: String = "10.10.3.50"
  private[serverApp] val SERVER_HOST_ADDRESS: String = "10.10.3.49"
}