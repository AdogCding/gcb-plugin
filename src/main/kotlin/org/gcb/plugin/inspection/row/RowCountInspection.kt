package org.gcb.plugin.inspection.row

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiCodeBlock
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiWhiteSpace
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.components.JBTextField
import org.gcb.plugin.MyBundle
import java.awt.FlowLayout
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.event.DocumentEvent

/**
 *
 */
class RowCountInspection : AbstractBaseJavaLocalInspectionTool() {
    object InspectionConfig {
        var rowCountLimit = 100
        val commentStr = "//${rowCountLimit}Ignored"
    }

    override fun createOptionsPanel(): JComponent {
        val comment = JBTextField("${InspectionConfig.rowCountLimit}")
        val panel = JPanel(FlowLayout(FlowLayout.CENTER))
        comment.document.addDocumentListener(object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) {
                val text = comment.getText()
                if (text == null || text.isBlank() || text.any { el -> !el.isDigit() }) {
                    thisLogger().warn("No text")
                    return
                }
                InspectionConfig.rowCountLimit = Integer.parseInt(text)
            }
        })
        panel.add(comment)
        return panel
    }

    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
        session: LocalInspectionToolSession
    ): PsiElementVisitor {
        return RowCountVisitor(holder)
    }


}

internal class RowCountFixer:LocalQuickFix {
    override fun getFamilyName(): String {
        return "Append comment"
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        if (descriptor.psiElement !is PsiMethod) {
            return
        }
        val codeBlock = (descriptor.psiElement as PsiMethod).body ?: return
        val factory = JavaPsiFacade
            .getInstance(project)
            .elementFactory
        val substituteCodeBlock = factory
            .createCodeBlockFromText(codeBlock.text, codeBlock.context)
        val comment = factory.createCommentFromText(RowCountInspection.InspectionConfig.commentStr, null)
        substituteCodeBlock.addAfter(comment, substituteCodeBlock.lBrace)
        codeBlock.replace(substituteCodeBlock)    }
}

internal class RowCountVisitor(private val problemsHolder: ProblemsHolder) : JavaElementVisitor() {
    private fun PsiCodeBlock.countPsiStatement(): Int {
        return this.statements.size
    }

    private fun PsiMethod.isFixed():Boolean {
        var start = this.body?.lBrace?.nextSibling
        while (start != null && start is PsiWhiteSpace) {
            start = start.nextSibling
        }
        if (start == null) {
            return false
        }
        return start is PsiComment && start.textMatches(RowCountInspection.InspectionConfig.commentStr)
    }

    override fun visitMethod(method: PsiMethod) {
        super.visitMethod(method)
        val rowCount = method.body?.countPsiStatement() ?: -1
        if (rowCount < 0) {
            thisLogger().warn("Counting row of ${method.name} fail")
        }
        if (!method.isFixed() && rowCount > RowCountInspection.InspectionConfig.rowCountLimit) {
            problemsHolder.registerProblem(method, MyBundle.message("inspection.row.count.statement.description",
                RowCountInspection.InspectionConfig.rowCountLimit), RowCountFixer())
        }
    }
}