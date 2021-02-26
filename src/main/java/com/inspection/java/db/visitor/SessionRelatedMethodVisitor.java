package com.inspection.java.db.visitor;

import com.inspection.java.db.bean.PsiOpenSessionOperationBean;
import com.inspection.java.db.fix.IdleLocalFix;
import com.inspection.java.utils.CrapTemplate;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.Collection;
import java.util.List;

/**
 */
public class SessionRelatedMethodVisitor extends JavaElementVisitor {
    private final ProblemsHolder problemsHolder;
    private final String DESCRIPTION_TEMPLATE = CrapTemplate.getCrapStmt("risky open session operation");
    public SessionRelatedMethodVisitor(ProblemsHolder problemsHolder) {
        this.problemsHolder = problemsHolder;
    }
    @Override
    public void visitMethod(PsiMethod method) {
        CheckSessionMethodCallExpressionVisitor checker =
                new CheckSessionMethodCallExpressionVisitor();
        Collection<PsiMethodCallExpression> mceCollection = PsiTreeUtil
                .findChildrenOfType(method, PsiMethodCallExpression.class);
        for(PsiMethodCallExpression mce: mceCollection) {
            mce.accept(checker);
        }
        List<PsiOpenSessionOperationBean> opList = checker.getOpenSessionBeanList();
        opList.forEach((el)->problemsHolder.registerProblem(el.getMethodCallExpression(), DESCRIPTION_TEMPLATE, new IdleLocalFix()));
    }
}
