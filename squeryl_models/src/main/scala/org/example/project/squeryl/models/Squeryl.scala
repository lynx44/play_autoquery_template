
package org.example.project.squeryl.models

import org.squeryl.PrimitiveTypeMode
import org.squeryl.dsl.ast.UpdateAssignment

object Squeryl extends PrimitiveTypeMode {
  case class DynamicUpdateAssignment(updateAssignment: UpdateAssignment) {
    def inhibitWhen(cond: Boolean): DynamicUpdateAssignment = {
      _inhibited = cond
      this
    }

    private var _inhibited = false
    def inhibited: Boolean = _inhibited
  }

  object DynamicUpdateStatement {
    def apply(updateAssignments: DynamicUpdateAssignment*): Seq[UpdateAssignment] = {
      updateAssignments.filterNot(_.inhibited).map(_.updateAssignment)
    }
  }

  implicit def dynamic(updateAssignments: DynamicUpdateAssignment*): Seq[UpdateAssignment] = DynamicUpdateStatement.apply(updateAssignments:_*)

  implicit def toDynamicUpdateAssignment(u: UpdateAssignment): DynamicUpdateAssignment = {
    DynamicUpdateAssignment(u)
  }
}
