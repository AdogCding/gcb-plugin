package com.inspection.java.rpl;

import com.inspection.java.utils.CrapTemplate;
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
    private final Logger logger = LoggerFactory.getLogger(CommonDaoUsageVisitor.class);
    private final ProblemsHolder problemsHolder;
    private static final String DESCRIPTION_TEMPLATE =
            CrapTemplate.getCrapStmt("CommonDao要被替换成DBUtils");
    private static final String TARGET = "CommonDao";

    public CommonDaoUsageVisitor(ProblemsHolder problemsHolder) {
        this.problemsHolder = problemsHolder;
    }

    /**
     * 对于每个方法调用，判断方法名称是否是要查找的方法名
     * 如果符合的话，进一步判断，这个方法是否属于我们要查找的类
     * 如果两个条件都符合，那么可以确定这个方法就是我们要找的方法
     * @param expression 方法表达式
     */
    @Override
    public void visitMethodCallExpression(PsiMethodCallExpression expression) {
        PsiReferenceExpression refExpr = expression.getMethodExpression();
        PsiIdentifier identifier = PsiTreeUtil.getChildOfType(refExpr, PsiIdentifier.class);
        if (identifier == null || !(MethodMap.contains(identifier.getText()))) {
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
