package oen.ncgrep

import oen.libs.form
import oen.libs.formh
import oen.libs.{ncurses => nc}
import oen.libs.{ncursesh => nch}
import scala.scalanative.unsafe._

class InputForm(parent: Ptr[nch.Window], y: Int, x: Int)(implicit z: Zone) {

  val myForm = {
    val field = form.newField(1, 10, y, x, 0, 0)
    form.setFieldBack(field, nc.A_UNDERLINE)
    form.fieldOptsOff(field, form.O_STATIC)

    val fields = alloc[Ptr[formh.Form]](2)
    fields(0) = field
    fields(1) = null
    form.newForm(fields)
  }
  form.setFormSub(myForm, parent)
  form.postForm(myForm)
  form.formDriver(myForm, form.REQ_FIRST_FIELD)

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

  def resize(newWidth: Int): Unit = {
    val currField = form.currentField(myForm)
    val buff      = fromCString(form.fieldBuffer(currField, 0))

    form.unpostForm(myForm)
    resizeField(newWidth)
    form.postForm(myForm)

    buff.trim.foreach(ch => form.formDriver(myForm, ch))
    form.formDriver(myForm, form.REQ_VALIDATION)
  }

  def resizeField(newWidth: Int): Ptr[formh.Field] = {
    val currField = form.currentField(myForm)
    form.freeField(currField) // TODO we need to free the memory

    val newField = form.newField(1, newWidth, y, x, 0, 0)
    form.setFieldBack(newField, nc.A_UNDERLINE)
    form.fieldOptsOff(newField, form.O_STATIC)

    val newFields = alloc[Ptr[formh.Form]](2)
    newFields(0) = newField
    newFields(1) = null

    form.setFormFields(myForm, newFields)

    newField
  }

  def focus(): Unit = {
    form.formDriver(myForm, form.REQ_VALIDATION)
    nc.wRefresh(parent)
  }

  def delete(): Unit = {
    val currField = form.currentField(myForm)
    form.unpostForm(myForm)
    form.freeForm(myForm)
    form.freeField(currField)
  }
}
