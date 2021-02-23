package com.inspection.java.db.method;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiMethod;

/**
 * 访问一个method，对每个method中的方法调用进行访问。
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
