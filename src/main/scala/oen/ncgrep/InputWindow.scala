package oen.ncgrep

import oen.libs.form
import oen.libs.formh
import oen.libs.{ncurses => nc}
import oen.libs.{ncursesh => nch}
import scala.scalanative.unsafe._

class InputWindow(implicit z: Zone) {
  val win = {
    val (lines, cols) = getLinesAndCols()
    nc.newWin(lines, cols, 1, 0)
  }

  val form1 = {
    val (lines, cols) = getLinesAndCols()
    new InputForm(win, 1, 1)
  }
  resize()

  def resize(): Unit = {
    val (lines, cols) = getLinesAndCols()
    nc.wResize(win, lines, cols)
    nc.wErease(win)
    form1.resize(cols / 2)
    redraw()
  }

  def redraw(): Unit = {
    nc.box(win, 0, 0)

    Zone { implicit z =>
      nc.wMove(win, 0, 2)
      val cmsg = toCString("input window")
      nc.wPrintw(win, cmsg)
    }

    nc.wRefresh(win)
  }

  def handleKey(key: Int): Unit =
    form1.handleKey(key)

  def focus(): Unit =
    form1.focus()

  def delete(): Unit = {
    form1.delete()
    nc.delWin(win)
  }

  private def getLinesAndCols(): (Int, Int) = {
    val stdSize = nch.getStdscrSize()
    val lines   = stdSize.height / 2 - 1
    val cols    = stdSize.width
    (lines, cols)
  }
}
