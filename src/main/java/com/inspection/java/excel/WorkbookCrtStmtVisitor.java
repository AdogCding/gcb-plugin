package com.inspection.java.excel;

import com.inspection.java.exception.TooHardForMyBrainException;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;

import java.util.Optional;

public class WorkbookCrtStmtVisitor extends JavaElementVisitor {
    private static final String QUALIFIED_NAME = "com.sunline.Workbook";
    private static final String NON_QUALIFIED_NAME = "Workbook";
    @Override
    public void visitNewExpression(PsiNewExpression psiNewExpression) {
        PsiJavaCodeReferenceElement reference = psiNewExpression.getClassReference();
        if (reference == null) {
            return ;
        }
        if (reference.isQualified() && reference.getQualifiedName() != null && reference.getQualifiedName().equals(QUALIFIED_NAME)) {
            return;
        }
        if (!reference.isQualified() && reference.getReferenceName() != null && reference.getQualifiedName().equals(NON_QUALIFIED_NAME)) {
            return;
        }
        // make sure its our target class
        PsiExpressionList workbookExpressionList = PsiTreeUtil.getChildOfType(psiNewExpression, PsiExpressionList.class);
        if (workbookExpressionList == null) {
            return;
        }
        // no help to handle this situation
        if (workbookExpressionList.getExpressionCount() != 1) {
            return;
        }
        PsiExpression expression = workbookExpressionList.getExpressions()[0];
        // my little brain can only handle this
        if (!(expression instanceof PsiReferenceExpression)) {
            return;
        }
        PsiReferenceExpression referenceExpression = (PsiReferenceExpression) expression;
        PsiElement resolvedRef = referenceExpression.resolve();
        if (!(resolvedRef instanceof PsiIdentifier)) {
            return ;
        }
    }
}
