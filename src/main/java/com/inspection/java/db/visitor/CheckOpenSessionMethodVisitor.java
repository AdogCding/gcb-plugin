package com.inspection.java.db.visitor;

import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;

public class CheckOpenSessionMethodVisitor extends JavaElementVisitor {
    @Override
    public void visitMethod(PsiMethod method) {

    }

    @Override
    public void visitMethodCallExpression(PsiMethodCallExpression expression) {

    }
}
