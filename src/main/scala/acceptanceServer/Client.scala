package acceptanceServer

import java.io.{IOException, BufferedReader => BR, InputStream => IS, InputStreamReader => ISR, OutputStream => OS, PrintWriter => PW}
import java.net.Socket

import akka.actor.Actor


/**
  * クライアントごとにストリームを管理するためのクラス
  */
class Client(var sock: Socket) extends Actor{
  private val in: BR = new BR(new ISR(sock.getInputStream()))
  private val out: PW = new PW(sock.getOutputStream(), true) // 文字列出力ストリーム

  // if receive Message "print", call this method and print message for client
  private def println(str: String) = this.out.println(str)

  // if receive Message "read", call this method and read message from client
  // :TODO return option
  private def read: String = {
    var line: String = ""

    try {
      line = in.readLine
    }
    catch {
      case e: IOException => {
        System.out.println("Client:read error")
        e.printStackTrace()
      }
    }

    line
  }

  def closeStream() {
    val fin = sock.getInputStream()
    val fout = sock.getOutputStream()

    try {
      // クローズ
      if (in != null) {
        in.close()
      }
      if (out != null) {
        out.close()
      }
      if (fin != null) {
        fin.close()
      }
      if (fout != null) {
        fout.close()
      }
      if (sock != null) {
        sock.close()
      }
    }
    catch {
      case e: IOException => {
        System.out.println("error" + e)
      }
    }
  }

  override def receive: Receive = {
    // :TODO extract tuple to case class
    case (cmd: String, args: String) if cmd == "print" => println(args)
    case (cmd: String, args: String) if cmd == "read" => {
      println(args)
      val input = read
      sender() ! input
    }
    case _ => {}
  }
}