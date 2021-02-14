package oen.ncgrep

import oen.libs.form
import oen.libs.formh
import oen.libs.{ncurses => nc}
import oen.libs.{ncursesh => nch}
import oen.ncgrep.Main.Colors
import scala.scalanative.unsafe._
import scalanative.unsigned._

class InputForm(parent: Ptr[nch.Window], y: Int, x: Int, width: Int, initValue: String, colors: Colors)(
  implicit z: Zone
) {

  val field = form.newField(1, width, y, x, 0, 0)
  form.fieldOptsOff(field, form.O_STATIC)

  val fields = alloc[Ptr[formh.Form]](2)
  fields(0) = field
  fields(1) = null

  val myForm = form.newForm(fields)
  form.setFormSub(myForm, parent)
  form.postForm(myForm)
  form.formDriver(myForm, form.REQ_FIRST_FIELD)

  initValue.trim.foreach(ch => form.formDriver(myForm, ch))
  form.formDriver(myForm, form.REQ_VALIDATION)

  def initStyle(): Unit = {
    form.setFieldBack(field, 0.toUInt)
    form.setFieldFore(field, nc.colorPair(colors.form).toUInt)
    form.setFieldBack(field, nc.colorPair(colors.form).toUInt | nc.A_UNDERLINE)
  }

  def handleKey(key: Int): Unit = {
    key match {
      case ch if ch == nc.KEY_LEFT =>
        form.formDriver(myForm, form.REQ_PREV_CHAR)
      case ch if ch == nc.KEY_RIGHT =>
        form.formDriver(myForm, form.REQ_NEXT_CHAR)
      case ch if ch == nc.KEY_BACKSPACE || ch == 127 =>
        form.formDriver(myForm, form.REQ_DEL_PREV)
      case ch if ch == nc.KEY_DC =>
        form.formDriver(myForm, form.REQ_DEL_CHAR)
      case ch =>
        form.formDriver(myForm, ch)
    }
    form.formDriver(myForm, form.REQ_VALIDATION)
    nc.wRefresh(parent)
  }

  def getBuff(): String = fromCString(form.fieldBuffer(field, 0)).trim()

  def focus(): Unit = {
    form.formDriver(myForm, form.REQ_VALIDATION)
    nc.wRefresh(parent)
  }

  def delete(): Unit = {
    form.unpostForm(myForm)
    form.freeForm(myForm)
    form.freeField(field)
  }
}
