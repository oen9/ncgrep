package oen.ncgrep

import oen.libs.{stdio => pstdio}
import scala.annotation.tailrec
import scala.scalanative.libc.stdio
import scala.scalanative.unsafe._
import scalanative.unsigned._

object GrepExecutor {
  def execute(grepQuery: String, grepParams: String, findParams: String): List[String] =
    getErrors(grepQuery, grepParams, findParams) match {
      case None =>
        val cmd = s"""grep $grepParams "$grepQuery""""
        readGrepResults(cmd)
      case Some(errors) =>
        errors
    }

  def readGrepResults(cmd: String): List[String] = Zone { implicit z =>
    val cmdWithSuppressErrors = s"$cmd 2>/dev/null"
    val grepStream            = pstdio.popen(toCString(cmdWithSuppressErrors), toCString("r"))
    val buff                  = alloc[CChar](1024)
    val buffSize              = (sizeof[CChar] * 1024.toULong).toInt

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

  private def getErrors(grepQuery: String, grepParams: String, findParams: String): Option[List[String]] =
    (queryErrors(grepQuery) ++ grepParamsErrors(grepParams) ++ findParamsErrors(findParams)) match {
      case Nil    => None
      case errors => Some(errors)
    }

  private def normalize(s: String): String =
    s.trim().toLowerCase.replaceAll("\\s+", " ")

  private def queryErrors(query: String): List[String] = {
    val normalized = normalize(query)
    if (normalized.size < 3) List(s"Query '$query' is too short. We need at least 3 characters")
    else Nil
  }

  private def grepParamsErrors(grepParams: String): List[String] = {
    val normalized           = normalize(grepParams)
    val bannedLastCharacters = "-."
    if (normalized.lastOption.exists(bannedLastCharacters.contains(_)))
      List(s"grep params can't end with: $bannedLastCharacters")
    else Nil
  }

  private def findParamsErrors(findParams: String): List[String] = Nil
}
