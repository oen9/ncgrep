package oen.ncgrep

import oen.libs.form
import oen.libs.formh
import oen.libs.{ncurses => nc}
import oen.libs.{ncursesh => nch}
import oen.ncgrep.InputWindow.State
import oen.ncgrep.Main.Colors
import scala.scalanative.unsafe._

class InputWindow(initState: State, colors: Colors)(implicit z: Zone) {
  val (lines, cols)  = getLinesAndCols()
  val win            = nc.newWin(lines, cols, 1, 0)
  val grepQueryForm  = new InputForm(win, 2, 1, cols - 2, initState.grepQuery, colors)
  val grepParamsForm = new InputForm(win, 4, 1, cols / 2 - 2, initState.grepQuery, colors)
  val findParamsForm = new InputForm(win, 4, cols / 2, cols / 2, initState.grepQuery, colors)

  grepQueryForm.initStyle()
  grepParamsForm.initStyle()
  findParamsForm.initStyle()

  nc.box(win, 0, 0)

  Zone { implicit z =>
    nc.wMove(win, 0, 2)
    val cmsg = toCString("input window")
    nc.wPrintw(win, cmsg)

    nc.wMove(win, 1, 1)
    val grepQueryMsg = toCString("grep query:")
    nc.wPrintw(win, grepQueryMsg)

    nc.wMove(win, 3, 1)
    val grepParamsMsg = toCString("grep params:")
    nc.wPrintw(win, grepParamsMsg)

    nc.wMove(win, 3, cols / 2)
    val findParamsMsg = toCString("find params (optional):")
    nc.wPrintw(win, findParamsMsg)
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
    val lines   = 6
    val cols    = stdSize.width
    (lines, cols)
  }
}

object InputWindow {
  case class State(grepQuery: String = "")
}
