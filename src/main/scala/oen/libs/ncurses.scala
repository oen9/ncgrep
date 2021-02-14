package oen.libs

import scala.scalanative.unsafe._
import scalanative.unsigned._

// https://github.com/MasseGuillaume/hands-on-scala-native/blob/master/src/main/scala/ncurses.scala
// http://invisible-island.net/ncurses/man/ncurses.3x.html

@link("ncurses")
@extern
object ncurses {
  import ncursesh._

  def initscr(): Ptr[Window]                                  = extern
  def printw(fmt: CString, args: CVarArg*): CInt              = extern
  def refresh(): CInt                                         = extern
  def endwin(): Unit                                          = extern
  def getch(): CInt                                           = extern
  def timeout(delay: CInt): Unit                              = extern
  def noecho(): Unit                                          = extern
  def attron(attribute: CInt): CInt                           = extern
  def attroff(attribute: CInt): CInt                          = extern
  val stdscr: Ptr[Window]                                     = extern
  def box(win: Ptr[Window], verch: CChar, horch: CChar): CInt = extern
  def keypad(win: Ptr[Window], bf: CBool): CInt               = extern

  @name("scalanative_a_underline")
  def A_UNDERLINE: UInt = extern

  @name("scalanative_key_up")
  def KEY_UP: CInt = extern

  @name("scalanative_key_down")
  def KEY_DOWN: CInt = extern

  @name("scalanative_key_left")
  def KEY_LEFT: CInt = extern

  @name("scalanative_key_right")
  def KEY_RIGHT: CInt = extern

  @name("scalanative_key_enter")
  def KEY_ENTER: CInt = extern

  @name("scalanative_key_dc")
  def KEY_DC: CInt = extern

  @name("scalanative_key_backspace")
  def KEY_BACKSPACE: CInt = extern

  @name("scalanative_key_resize")
  def KEY_RESIZE: CInt = extern

  @name("scalanative_key_f")
  def KEY_F(n: CInt): CInt = extern

  @name("wborder")
  def wBorder(
    window: Ptr[Window],
    ls: CChar,
    rs: CChar,
    ts: CChar,
    bs: CChar,
    tl: CChar,
    tr: CChar,
    bl: CChar,
    br: CChar
  ): CInt = extern

  @name("init_pair")
  def initPair(pair: CShort, foreground: Color, background: Color): CInt = extern

  @name("start_color")
  def startColor(): Unit = extern

  @name("has_colors")
  def hasColors(): Boolean = extern

  @name("use_default_colors")
  def useDefaultColors(): Unit = extern

  @name("COLOR_PAIR")
  def colorPair(pair: CShort): CInt = extern

  @name("newwin")
  def newWin(nlines: Int, ncols: Int, begin_y: Int, begin_x: Int): Ptr[Window] = extern

  @name("delwin")
  def delWin(win: Ptr[Window]): Unit = extern

  @name("endwin")
  def endWin(): Unit = extern

  @name("werase")
  def wErease(win: Ptr[Window]): CInt = extern

  @name("wattron")
  def wAttrOn(win: Ptr[Window], attribute: CInt): CInt = extern

  @name("wattroff")
  def wAttrOff(win: Ptr[Window], attribute: CInt): CInt = extern

  @name("wprintw")
  def wPrintw(win: Ptr[Window], fmt: CString, args: CVarArg*): CInt = extern

  @name("wrefresh")
  def wRefresh(win: Ptr[Window]): CInt = extern

  @name("mvwprintw")
  def mvPrintW(win: Ptr[Window], y: CInt, x: CInt, fmt: CString, args: CVarArg*): CInt = extern

  @name("mvwhline")
  def mvWhLine(win: Ptr[Window], y: CInt, x: CInt, ch: CChar, n: CInt): CInt = extern

  @name("mvwvline")
  def mvWvLine(win: Ptr[Window], y: CInt, x: CInt, ch: CChar, n: CInt): CInt = extern

  @name("wmove")
  def wMove(win: Ptr[Window], y: CInt, x: CInt): CInt = extern

  @name("wresize")
  def wResize(win: Ptr[Window], lines: CInt, columns: CInt): CInt = extern

  @name("mvwin")
  def mvWin(win: Ptr[Window], y: CInt, x: CInt): CInt = extern

  @name("getmaxx")
  def getMaxX(window: Ptr[Window]): Int = extern

  @name("getmaxy")
  def getMaxY(window: Ptr[Window]): Int = extern
}

object ncursesh {
  type Window = CStruct0
  case class Size(width: Int, height: Int)

  def winSize(win: Ptr[Window]): Size = {
    val width = ncurses.getMaxX(win)
    val height = ncurses.getMaxY(win)
    Size(width, height)
  }

  def getStdscrSize(): Size = winSize(ncurses.stdscr)

  class Color(val value: CInt) extends AnyVal

  object Color {
    final val Transparent = new Color(-1)
    final val Black       = new Color(0)
    final val Red         = new Color(1)
    final val Green       = new Color(2)
    final val Yellow      = new Color(3)
    final val Blue        = new Color(4)
    final val Magenta     = new Color(5)
    final val Cyan        = new Color(6)
    final val White       = new Color(7)
  }
  class Attribute(val value: CInt) extends AnyVal
}
