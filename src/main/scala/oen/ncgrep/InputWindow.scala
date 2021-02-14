package oen.ncgrep

import oen.libs.form
import oen.libs.formh
import oen.libs.{ncurses => nc}
import oen.libs.{ncursesh => nch}
import oen.ncgrep.InputWindow.State
import scala.scalanative.unsafe._

class InputWindow(initState: State)(implicit z: Zone) {
  val (lines, cols) = getLinesAndCols()
  val win           = nc.newWin(lines, cols, 1, 0)
  val grepQueryForm = new InputForm(win, 1, 1, cols / 2, initState.grepQuery)

  nc.box(win, 0, 0)

  Zone { implicit z =>
    nc.wMove(win, 0, 2)
    val cmsg = toCString("input window")
    nc.wPrintw(win, cmsg)
  }

  nc.wRefresh(win)

  def handleKey(key: Int): Unit =
    grepQueryForm.handleKey(key)

  def focus(): Unit =
    grepQueryForm.focus()

  def getState(): State = {
    val grepQuery = grepQueryForm.getBuff()
    State(grepQuery)
  }

  def delete(): Unit = {
    grepQueryForm.delete()
    nc.delWin(win)
  }

  private def getLinesAndCols(): (Int, Int) = {
    val stdSize = nch.getStdscrSize()
    val lines   = stdSize.height / 2 - 1
    val cols    = stdSize.width
    (lines, cols)
  }
}

object InputWindow {
  case class State(grepQuery: String = "")
}
