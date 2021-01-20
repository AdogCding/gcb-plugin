package com.inspection.java.rpl;

import com.inspection.java.utils.CrapTemplate;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.util.PsiTreeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonDaoUsageVisitor extends JavaElementVisitor {
    private Logger logger = LoggerFactory.getLogger(CommonDaoUsageVisitor.class);
    private ProblemsHolder problemsHolder;
    private static final String DESCRIPTION_TEMPLATE =
            CrapTemplate.getCrapStmt("CommonDao要被替换成DBUtils");
    private static final String TARGET = "CommonDao";

    public CommonDaoUsageVisitor(ProblemsHolder problemsHolder) {
        this.problemsHolder = problemsHolder;
    }

    @Override
    public void visitMethodCallExpression(PsiMethodCallExpression expression) {
        PsiReferenceExpression refExpr = expression.getMethodExpression();
        PsiIdentifier identifier = PsiTreeUtil.getChildOfType(refExpr, PsiIdentifier.class);
        if (identifier == null || !(MethodMapper.contains(identifier.getText()))) {
            return;
        }
        String cdMethodName = identifier.getText();
        PsiReference methodRef = refExpr.getReference();
        if (methodRef == null) {
            logger.debug("找不到方法的引用");
            return;
        }
        PsiElement el = methodRef.resolve();
        if (el == null) {
            logger.debug("引用无法resolve");
            return;
        }
        CommonDaoChecker checker = new CommonDaoChecker();
        el.accept(checker);
        if (checker.getResult()) {
            problemsHolder.registerProblem(expression, DESCRIPTION_TEMPLATE, new CommonDaoReplaceFixer(cdMethodName));
        }
    }

}
