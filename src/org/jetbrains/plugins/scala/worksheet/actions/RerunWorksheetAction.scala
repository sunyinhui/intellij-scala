package org.jetbrains.plugins.scala.worksheet.actions
import javax.swing.Icon

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent

/**
  * User: Dmitry.Naydanov
  * Date: 27.04.17.
  */
class RerunWorksheetAction extends RunWorksheetAction {
  override def actionPerformed(e: AnActionEvent): Unit = {
    RunWorksheetAction.runCompiler(e.getProject, auto = false, fromScratch = true)
  }

  override def actionIcon: Icon = AllIcons.Actions.Rerun

  override def bundleKey: String = "worksheet.rerun.button"

  override def shortcutId: Option[String] = None
}
