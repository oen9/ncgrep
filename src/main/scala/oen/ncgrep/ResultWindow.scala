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

  def drawResult(s: String): Unit = {
    nc.wErease(win)
    drawBox()
    s match {
      case s if s.length() >= 3 =>
        Zone { implicit z =>
          nc.wMove(win, 1, 1)
          val cmsg = toCString(s"here will be grep result for: $s")
          nc.wPrintw(win, cmsg)
        }
      case s =>
        Zone { implicit z =>
          nc.wMove(win, 1, 1)
          val cmsg = toCString(s"'$s' is too short (we need at least 3 characters")
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
