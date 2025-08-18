package org.gcb.plugin.inspection.row

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElementVisitor
import com.intellij.ui.components.JBTextField
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel

class SensitiveLogInspection: AbstractBaseJavaLocalInspectionTool() {

    @JvmField
    var selectedClassName: String? = null

    override fun createOptionsPanel(): JComponent {
        val panel = JPanel(BorderLayout())

        val textField = JBTextField()
        if (selectedClassName != null) {
            textField.text = selectedClassName
        }
        panel.add(textField, BorderLayout.CENTER)

        val button = JButton("Choose Class...")
        button.addActionListener {
            val project = CommonDataKeys.PROJECT
                .getData(DataManager.getInstance().dataContext) ?: return@addActionListener

            // 打开 Class Chooser
            val chooser = TreeClassChooserFactory.getInstance(project)
                .createAllProjectScopeChooser("Select Class")
            chooser.showDialog()
            val psiClass = chooser.selected ?: return@addActionListener

            selectedClassName = ""
            textField.text = selectedClassName
        }
        panel.add(button, BorderLayout.EAST)
        return panel
    }


    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return SentitiveLogVisitor()
    }
}

class SentitiveLogVisitor(): PsiElementVisitor() {

}

internal class RemoveLogStatement: LocalQuickFix {
    override fun getFamilyName(): String {
        return "Sensitive log"
    }

    override fun getName(): String {
        return super.getName()
    }


    override fun applyFix(project: Project, p1: ProblemDescriptor) {
        TODO("Not yet implemented")
    }
}

internal class DowngradeLogLevel: LocalQuickFix {
    override fun getFamilyName(): String {
        TODO("Not yet implemented")
    }

    override fun applyFix(p0: Project, p1: ProblemDescriptor) {
        TODO("Not yet implemented")
    }
}

