package acceptanceServer

import java.io._
import java.net.Socket

/**
  * クライアントごとにストリームを管理するためのクラス
  */
class Client(var sock: Socket) {
  private var fin: InputStream = null // 入力ストリーム
  private var fout: OutputStream = null // 出力ストリーム
  private var in: BufferedReader = null // 文字列入力ストリーム
  private var out: PrintWriter = null // 文字列出力ストリーム
  @throws[IOException]
  def openStream() {
    // ストリームの確立
    fin = sock.getInputStream
    in = new BufferedReader(new InputStreamReader(fin))
    fout = sock.getOutputStream
    out = new PrintWriter(fout, true)
    return
  }

  def println(str: String) {
    this.out.println(str)
    return
  }

  def read: String = {
    var line: String = ""
    try {
      line = in.readLine
    }
    catch {
      case e: IOException => {
        e.printStackTrace()
      }
    }
    return line
  }

  def closeStream() {
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
    return
  }
}