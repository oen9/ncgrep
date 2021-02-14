package oen.ncgrep

import oen.libs.{stdio => pstdio}
import scala.annotation.tailrec
import scala.scalanative.libc.stdio
import scala.scalanative.unsafe._
import scalanative.unsigned._

object GrepExecutor {
  def execute(grepQuery: String, grepParams: String, findParams: String): List[String] = grepQuery match {
    case query if query.size >= 3 =>
      val cmd = s"""grep $grepParams "$grepQuery""""
      readGrepResults(cmd)
    case query =>
      List(s"Query '$query' is too short. We need at least 3 characters")
  }

  def readGrepResults(cmd: String): List[String] = Zone { implicit z =>
    val grepStream = pstdio.popen(toCString(cmd), toCString("r"))
    val buff       = alloc[CChar](1024)
    val buffSize   = (sizeof[CChar] * 1024.toULong).toInt

    @tailrec
    def readLines(prevLine: String, acc: List[String] = List()): List[String] = prevLine match {
      case null => acc
      case someLine =>
        val result = stdio.fgets(buff, buffSize, grepStream)
        readLines(fromCString(result), acc :+ prevLine)
    }

    val firstResult = stdio.fgets(buff, (sizeof[CChar] * 1024.toULong).toInt, grepStream)
    val results     = readLines(fromCString(firstResult))
    pstdio.pclose(grepStream)
    results
  }

}
