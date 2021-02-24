package com.inspection.java.db.visitor;

import com.inspection.java.db.bean.PsiOpenSessionOperationBean;
import com.inspection.java.utils.CrapTemplate;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiMethod;

import java.util.List;

/**
 */
public class SessionRelatedMethodVisitor extends JavaElementVisitor {
    private final ProblemsHolder problemsHolder;
    private final String DESCRIPTION_TEMPLATE = CrapTemplate.getCrapStmt("应该关闭数据库会话");
    public SessionRelatedMethodVisitor(ProblemsHolder problemsHolder) {
        this.problemsHolder = problemsHolder;
    }
    @Override
    public void visitMethod(PsiMethod method) {
        CheckSessionMethodCallExpressionVisitor checker =
                new CheckSessionMethodCallExpressionVisitor();
        method.accept(checker);
        List<PsiOpenSessionOperationBean> opList = checker.getOpenSessionBeanList();
        opList.forEach((el)->problemsHolder.registerProblem(el.getCaller(), DESCRIPTION_TEMPLATE, null));
    }
}
