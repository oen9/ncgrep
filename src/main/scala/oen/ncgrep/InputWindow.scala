package oen.ncgrep

import oen.libs.form
import oen.libs.formh
import oen.libs.{ncurses => nc}
import oen.libs.{ncursesh => nch}
import oen.ncgrep.InputWindow.State
import oen.ncgrep.Main.AppState
import oen.ncgrep.Main.Colors
import scala.scalanative.unsafe._

class InputWindow(initState: State, colors: Colors)(implicit z: Zone) {
  val (lines, cols)  = getLinesAndCols()
  val win            = nc.newWin(lines, cols, 1, 0)
  val grepQueryForm  = new InputForm(win, 2, 1, cols - 2, initState.grepQuery, colors)
  val grepParamsForm = new InputForm(win, 4, 1, cols / 2 - 2, initState.grepParams, colors)
  val findParamsForm = new InputForm(win, 4, cols / 2, cols / 2, initState.findParams, colors)
  val forms          = List(grepQueryForm, grepParamsForm, findParamsForm)

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

  def handleKey(key: Int, state: State): State =
    key match {
      case ch if ch == '\t' =>
        val newFormId = (state.formId + 1) match {
          case 3     => 0
          case other => other
        }
        forms.lift(newFormId).foreach(_.focus())
        state.copy(formId = newFormId)

      case ch =>
        forms.lift(state.formId).foreach(_.handleKey(ch))
        getState(state)
    }

  def focus(state: State): Unit = forms.lift(state.formId).foreach(_.focus())

  private def getState(oldState: State): State = {
    val grepQuery  = grepQueryForm.getBuff()
    val grepParams = grepParamsForm.getBuff()
    val findParams = findParamsForm.getBuff()
    oldState.copy(
      grepQuery = grepQuery,
      grepParams = grepParams,
      findParams = findParams
    )
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
  case class State(grepQuery: String = "", grepParams: String = "* -d skip -iIHne", findParams: String = "", formId: Int = 0)
}
