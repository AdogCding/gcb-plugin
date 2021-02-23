package com.inspection.java.db.method;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiMethod;

/**
 */
public class SessionRelatedMethodVisitor extends JavaElementVisitor {
    ProblemsHolder problemsHolder;
    public SessionRelatedMethodVisitor(ProblemsHolder problemsHolder) {
        this.problemsHolder = problemsHolder;
    }
    @Override
    public void visitMethod(PsiMethod method) {
        method.accept(new SessionMethodCallExpressionVisitor());
    }
}
