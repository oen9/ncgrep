package oen.libs

import scala.scalanative.libc.stdio.FILE
import scalanative.unsafe._

@extern
object stdio {
  def popen(command: CString, tpe: CString): Ptr[FILE] = extern
  def pclose(file: Ptr[FILE]): CInt                    = extern
}
