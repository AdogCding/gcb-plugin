package com.inspection.java.xcl;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiNewExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class ImproperWorkBookCrtFixer implements LocalQuickFix {
    private static final String WORKBOOK_FACTORY_QNAME = "com.sunline.gfnfrs.core.excel.WorkbookFactory";
    private static final String INIT_METHOD = "initImportWorkbook";
    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getName() {
        return "ImproperWorkBookCrtFixer";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return getName();
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        PsiElement psiElement = descriptor.getPsiElement();
        if (!(psiElement instanceof PsiNewExpression)) {
            return;
        }
        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        PsiElementFactory psiElementFactory = javaPsiFacade.getElementFactory();
        PsiClass workFactoryPsiClass = javaPsiFacade.findClass(WORKBOOK_FACTORY_QNAME, GlobalSearchScope.allScope(project));
        if (workFactoryPsiClass == null) {
            return;
        }
        // 创建一个对WorkbookFactory的引用
        PsiReferenceExpression psiWbFactoryCrtRef =  psiElementFactory.createReferenceExpression(workFactoryPsiClass);
        // 创建一个模板
        PsiMethodCallExpression properWayCrtWorkbookCallPlaceholder = (PsiMethodCallExpression) psiElementFactory
                .createExpressionFromText("WorkbookFactory.initImportWorkbook(file)", null);
        PsiReferenceExpression crtMethodRef = properWayCrtWorkbookCallPlaceholder.getMethodExpression();
        PsiReferenceExpression wbFactoryRef = PsiTreeUtil.getChildOfType(crtMethodRef, PsiReferenceExpression.class);
        if (wbFactoryRef == null) {
            return;
        }
        // caller的占位符替换掉
        wbFactoryRef.replace(psiWbFactoryCrtRef);
        //找到bimis的引用
        PsiReferenceExpression bimisRef = findBimisFileRef((PsiNewExpression) psiElement);
        if (bimisRef == null) {
            return;
        }
        PsiExpression bimisRefCy = psiElementFactory.createExpressionFromText(bimisRef.getText(), bimisRef.getContext());
        // 替换参数
        PsiExpressionList psiExpressionList = properWayCrtWorkbookCallPlaceholder.getArgumentList();
        PsiReferenceExpression fooBimisFileRef = PsiTreeUtil.getChildOfType(psiExpressionList, PsiReferenceExpression.class);
        if (fooBimisFileRef == null) {
            return;
        }
        fooBimisFileRef.replace(bimisRefCy);
        psiElement.replace(properWayCrtWorkbookCallPlaceholder);
    }

    private PsiReferenceExpression findBimisFileRef(PsiNewExpression psiNewExpression) {
        PsiExpressionList argumentList =  psiNewExpression.getArgumentList();
        if (argumentList == null || argumentList.getExpressionCount() != 1) {
            return null;
        }
        PsiReferenceExpression inputStreamRef = PsiTreeUtil.getChildOfType(argumentList, PsiReferenceExpression.class);
        if (inputStreamRef == null) {
            return null;
        }
        PsiElement psiElement = inputStreamRef.resolve();
        if (!(psiElement instanceof PsiLocalVariable)) {
            return null;
        }
        PsiLocalVariable inputStreamLocalVar = (PsiLocalVariable) psiElement;
        PsiMethodCallExpression methodCallExpr = PsiTreeUtil.getChildOfType(inputStreamLocalVar, PsiMethodCallExpression.class);
        // 只声明没赋值
        if (methodCallExpr == null) {
            return null;
        }
        // Bimis调用的表达式
        PsiExpression expression = methodCallExpr.getMethodExpression();
        return PsiTreeUtil.getChildOfType(expression, PsiReferenceExpression.class);

    }
}
