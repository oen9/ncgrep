package oen.ncgrep

import oen.libs.form
import oen.libs.formh
import oen.libs.{ncurses => nc}
import oen.libs.{ncursesh => nch}
import scala.scalanative.unsafe._

class ResultWindow {
  val (lines, cols) = getLinesAndCols()
  val win           = nc.newWin(lines, cols, 7, 0)

  drawBox()
  nc.wRefresh(win)

  def drawResult(results: List[String]): Unit = {
    nc.wErease(win)
    drawBox()
    for ((line, idx) <- results.zipWithIndex if idx < lines - 2) {
      Zone { implicit z =>
        nc.wMove(win, 1 + idx, 1)
        val fixedLine =
          if (line.size > cols - 2) s"${line.trim.substring(0, cols - 6)}..."
          else line.trim
        val cmsg = toCString(fixedLine)
        nc.wPrintw(win, cmsg)
      }
    }
    nc.wRefresh(win)
  }

  def delete(): Unit = nc.delWin(win)

  private def drawBox(): Unit = {
    nc.box(win, 0, 0)
    Zone { implicit z =>
      nc.wMove(win, 0, 2)
      val cmsg = toCString("result window")
      nc.wPrintw(win, cmsg)
    }
  }

  private def getLinesAndCols(): (Int, Int) = {
    val stdSize = nch.getStdscrSize()
    val lines   = stdSize.height - 8
    val cols    = stdSize.width
    (lines, cols)
  }
}
