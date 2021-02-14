package oen.ncgrep

import oen.libs.form
import oen.libs.formh
import oen.libs.{ncurses => ns}
import oen.libs.{ncursesh => nsh}
import oen.ncgrep.InputWindow
import oen.ncgrep.ResultWindow
import scala.annotation.tailrec
import scala.scalanative.posix.unistd
import scala.scalanative.unsafe._
import scalanative.unsigned._

object Main {

  case class Colors(title: Short)
  case class NcState(inputWin: InputWindow, resultWin: ResultWindow, colors: Colors)
  case class AppState(inputState: InputWindow.State = InputWindow.State())

  def main(args: Array[String]): Unit = {
    val colors = Colors(title = 1.toShort)
    val win    = init(colors)

    mainLoops(colors)

    ns.endWin()
  }

  def init(colors: Colors): Ptr[nsh.Window] = {
    val win = ns.initscr()
    ns.noecho()
    ns.startColor()
    ns.useDefaultColors()
    ns.keypad(win, true)
    ns.refresh()

    ns.initPair(colors.title, nsh.Color.Black, nsh.Color.White)

    win
  }

  def redrawMain(win: Ptr[nsh.Window], colors: Colors): Unit = {
    ns.wErease(win)
    drawTitle(0, win, colors.title, "ncgrep")

    val size = nsh.winSize(win)
    drawTitle(size.height - 1, win, colors.title, "<tab> - switch\t? - help\t F9 to quit")
    ns.wRefresh(win)
  }

  def mainLoops(colors: Colors): Unit = {
    @tailrec
    def resizeLoop(pressedKey: Int = 0, appState: AppState = AppState()): Unit = pressedKey match {
      case c if c == ns.KEY_F(9) => ()
      case _ =>
        val (nextKey, nextAppState) = Zone { implicit z =>
          redrawMain(ns.stdscr, colors)
          val inputWin  = new InputWindow(appState.inputState)
          val resultWin = new ResultWindow()
          inputWin.focus()

          val pressedKey     = ns.getch()
          val ncState        = NcState(inputWin = inputWin, resultWin = resultWin, colors = colors)
          val nextKey        = mainLoop(pressedKey, appState, ncState)
          val nextInputState = inputWin.getState()

          inputWin.delete()
          resultWin.delete()

          val nextAppState = AppState(inputState = nextInputState)
          (nextKey, nextAppState)
        }
        resizeLoop(nextKey, nextAppState)
    }

    @tailrec
    def mainLoop(pressedKey: Int, appState: AppState, ncState: NcState): Int = pressedKey match {
      case c if c == ns.KEY_F(9) || c == ns.KEY_RESIZE => c
      case other =>
        ncState.inputWin.handleKey(other)
        val nextKey = ns.getch()
        mainLoop(nextKey, appState, ncState)
    }

    resizeLoop()
  }

  def drawTitle(line: Int, win: Ptr[nsh.Window], colorId: Short, msg: String): Unit = {
    ns.wAttrOn(win, ns.colorPair(colorId))

    val maxX = ns.getMaxX(win)
    ns.mvWhLine(win, line, 0, ' ', maxX)
    Zone { implicit z =>
      val cmsg = toCString(msg)
      ns.wMove(win, line, (maxX / 2) - (msg.size / 2))
      ns.wPrintw(win, cmsg)
    }
    ns.wAttrOff(win, ns.colorPair(colorId))
  }
}
