package com.inspection.java.rf;

import com.inspection.java.utils.CrapTemplate;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiIfStatement;
import com.intellij.psi.PsiWhiteSpace;

public class IfStmtVisitor extends JavaElementVisitor {
    private final ProblemsHolder problemsHolder;
    private final String DESCRIPTION_TEMPLATE = CrapTemplate.getCrapStmt("if-statement should start with a new line");

    public IfStmtVisitor(ProblemsHolder problemsHolder) {
        this.problemsHolder = problemsHolder;
    }


    @Override
    public void visitIfStatement(PsiIfStatement statement) {
        // 如果是一个if分支，不处理
        if (statement.getParent() instanceof PsiIfStatement) {
            return;
        }
        if (!(statement.getPrevSibling() instanceof PsiWhiteSpace)) {
            problemsHolder.registerProblem(statement, DESCRIPTION_TEMPLATE, new ReformatIfStmtFixer());
            return;
        }
        PsiWhiteSpace whiteSpace = (PsiWhiteSpace) statement.getPrevSibling();
        if (!whiteSpace.textContains('\n')) {
            problemsHolder.registerProblem(statement, DESCRIPTION_TEMPLATE, new ReformatIfStmtFixer());
        }
    }

}
