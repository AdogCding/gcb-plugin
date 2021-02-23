package com.inspection.java.rpl;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiImportStatement;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class CommonDaoReplaceFixer implements LocalQuickFix {
    private String cdMethodName;

    public CommonDaoReplaceFixer(String cdMethodName) {
        this.cdMethodName = cdMethodName;
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Sentence) @NotNull String getName() {
        return "Replace with DBUtils";
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Sentence) @NotNull String getFamilyName() {
        return getName();
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        PsiMethodCallExpression methodCallExpr = (PsiMethodCallExpression) descriptor.getPsiElement();
        // DBUtils的PsiClass
        PsiClass psiDBClass = JavaPsiFacade
                .getInstance(project).findClass(Constants.DB_CLASS, GlobalSearchScope.allScope(project));
        if (psiDBClass == null) {
            return;
        }
        PsiElementFactory factory =
                JavaPsiFacade.getInstance(project).getElementFactory();
        // import DBUtils的import statement
        PsiImportStatement importStatement = factory.createImportStatement(psiDBClass);
        PsiReferenceExpression methodRefExpr = methodCallExpr.getMethodExpression();
        // 替换方法名称
        PsiIdentifier methodId = PsiTreeUtil.getChildOfType(methodRefExpr, PsiIdentifier.class);
        if (methodId == null) {
            return;
        }
        PsiIdentifier subMethodId = factory.createIdentifier(MethodMap.get(cdMethodName));
        methodId.replace(subMethodId);
        // 替换调用者
        // 新的调用者
        PsiReferenceExpression dbUtilsRefExpr = factory.createReferenceExpression(psiDBClass);
        // 找到原有调用者
        PsiExpression callerExpr = PsiTreeUtil.getChildOfType(methodRefExpr, PsiExpression.class);
        if (callerExpr == null) {
            return;
        }
        PsiExpression callerExprCy = factory.createExpressionFromText(callerExpr.getText(), callerExpr.getContext());
        callerExpr.replace(dbUtilsRefExpr);
        // 原有的方法参数
        PsiExpressionList exprList = methodCallExpr.getArgumentList();
        // 原有方法的第一个参数
        PsiExpression firstArg = PsiTreeUtil.getChildOfType(exprList, PsiExpression.class);
        PsiElement anchor = firstArg;
        if (firstArg == null) {
            anchor = exprList.getLastChild();
        }
        exprList.addBefore(callerExprCy, anchor);
    }
}
