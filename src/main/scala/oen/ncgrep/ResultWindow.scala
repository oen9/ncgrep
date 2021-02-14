package oen.ncgrep

import oen.libs.form
import oen.libs.formh
import oen.libs.{ncurses => nc}
import oen.libs.{ncursesh => nch}
import scala.scalanative.unsafe._

class ResultWindow {
  val (lines, cols) = getLinesAndCols()
  val win           = nc.newWin(lines, cols, 7, 0)

  nc.box(win, 0, 0)
  Zone { implicit z =>
    nc.wMove(win, 0, 2)
    val cmsg = toCString("result window")
    nc.wPrintw(win, cmsg)
  }

  nc.wRefresh(win)

  def delete(): Unit = nc.delWin(win)

  private def getLinesAndCols(): (Int, Int) = {
    val stdSize = nch.getStdscrSize()
    val lines   = stdSize.height - 8
    val cols    = stdSize.width
    (lines, cols)
  }
}
