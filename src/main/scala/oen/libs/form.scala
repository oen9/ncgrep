package oen.libs

import scala.scalanative.unsafe._
import scalanative.unsigned._

@link("form")
@extern
object form {
  import formh._

  @name("scalanative_o_autoskip")
  def O_AUTOSKIP: UInt = extern

  @name("scalanative_o_bs_overload")
  def O_BS_OVERLOAD: UInt = extern

  @name("scalanative_o_static")
  def O_STATIC: UInt = extern

  @name("scalanative_o_active")
  def O_ACTIVE: UInt = extern

  @name("scalanative_req_next_field")
  def REQ_NEXT_FIELD: Int = extern

  @name("scalanative_req_prev_field")
  def REQ_PREV_FIELD: Int = extern

  @name("scalanative_req_end_line")
  def REQ_END_LINE: Int = extern

  @name("scalanative_req_validation")
  def REQ_VALIDATION: Int = extern

  @name("scalanative_req_del_char")
  def REQ_DEL_CHAR: Int = extern

  @name("scalanative_req_del_prev")
  def REQ_DEL_PREV: Int = extern

  @name("scalanative_req_first_field")
  def REQ_FIRST_FIELD: Int = extern

  @name("scalanative_req_prev_char")
  def REQ_PREV_CHAR: Int = extern

  @name("scalanative_req_next_char")
  def REQ_NEXT_CHAR: Int = extern

  @name("scalanative_req_clr_field")
  def REQ_CLR_FIELD: Int = extern

  @name("new_field")
  def newField(height: CInt, width: CInt, toprow: CInt, leftcol: CInt, offscreen: CInt, nbuffers: CInt): Ptr[Field] =
    extern

  @name("new_form")
  def newForm(field: Ptr[Ptr[Form]]): Ptr[Form] = extern

  @name("set_form_win")
  def setFormWin(field: Ptr[Form], window: Ptr[ncursesh.Window]): Int = extern

  @name("set_form_sub")
  def setFormSub(field: Ptr[Form], window: Ptr[ncursesh.Window]): Int = extern

  @name("post_form")
  def postForm(form: Ptr[Form]): CInt = extern

  @name("set_field_fore")
  def setFieldFore(field: Ptr[Field], attr: UInt): CInt = extern

  @name("set_field_back")
  def setFieldBack(field: Ptr[Field], attr: UInt): CInt = extern

  @name("field_opts_on")
  def fieldOptsOn(field: Ptr[Field], fieldOptions: UInt): CInt = extern

  @name("field_opts_off")
  def fieldOptsOff(field: Ptr[Field], fieldOptions: UInt): CInt = extern

  @name("unpost_form")
  def unpostForm(form: Ptr[Form]): CInt = extern

  @name("free_form")
  def freeForm(form: Ptr[Form]): CInt = extern

  @name("free_field")
  def freeField(field: Ptr[Field]): CInt = extern

  @name("form_driver")
  def formDriver(form: Ptr[Form], c: CInt): CInt = extern

  @name("field_buffer")
  def fieldBuffer(form: Ptr[Field], buffer: CInt): CString = extern

  @name("set_field_buffer")
  def setFieldBuffer(form: Ptr[Field], buffer: CInt, value: CString): CString = extern

  @name("set_max_field")
  def setMaxField(form: Ptr[Field], maxSize: CInt): CInt = extern

  @name("form_fields")
  def formFields(form: Ptr[Form]): Ptr[Ptr[Form]] = extern

  @name("current_field")
  def currentField(form: Ptr[Form]): Ptr[Field] = extern

  @name("set_form_fields")
  def setFormFields(form: Ptr[Form], fields: Ptr[Ptr[Field]]): CInt = extern
}

object formh {
  type Form  = CStruct0
  type Field = CStruct0
}
