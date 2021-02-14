package oen.ncgrep

import oen.libs.form
import oen.libs.formh
import oen.libs.{ncurses => nc}
import oen.libs.{ncursesh => nch}
import scala.scalanative.unsafe._

class ResultWindow {
  val win = {
    val (lines, cols) = getLinesAndCols()
    nc.newWin(lines, cols, 1, 0)
  }

  resize()

  def resize(): Unit = {
    val (lines, cols) = getLinesAndCols()
    nc.wResize(win, lines, cols)

    val newY = nc.getMaxY(nc.stdscr) / 2
    nc.mvWin(win, newY, 0)

    redraw()
  }

  def redraw(): Unit = {
    nc.wErease(win)
    nc.box(win, 0, 0)

    Zone { implicit z =>
      nc.wMove(win, 0, 2)
      val cmsg = toCString("result window")
      nc.wPrintw(win, cmsg)
    }

    nc.wRefresh(win)
  }

  def delete(): Unit = nc.delWin(win)

  private def getLinesAndCols(): (Int, Int) = {
    val stdSize = nch.getStdscrSize()
    val lines   = stdSize.height / 2 - 1
    val cols    = stdSize.width
    (lines, cols)
  }
}
