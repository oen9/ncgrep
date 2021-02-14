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
  def main(args: Array[String]): Unit = {
    val colors = Colors(title = 1.toShort)
    val win    = init(colors)

    redrawMain(ns.stdscr, colors)
    Zone { implicit z =>
      val inputWin  = new InputWindow()
      val resultWin = new ResultWindow()
      inputWin.focus()

      mainLoop(inputWin, resultWin, colors)

      inputWin.delete()
      resultWin.delete()
    }
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

  def mainLoop(inputWindow: InputWindow, resultWin: ResultWindow, colors: Colors): Unit = {
    @tailrec
    def loop(pressedKey: Int): Unit = pressedKey match {
      case c if c == ns.KEY_F(9) => ()
      case _ =>
        pressedKey match {
          case c if c == ns.KEY_RESIZE =>
            redrawMain(ns.stdscr, colors)
            inputWindow.resize()
            resultWin.resize()
            inputWindow.focus()
          case '\t'  =>
          case other => inputWindow.handleKey(other)
        }
        val nextKey = ns.getch()
        loop(nextKey)
    }

    val firstHit = ns.getch()
    loop(firstHit)
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
