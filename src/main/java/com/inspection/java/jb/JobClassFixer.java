package com.inspection.java.jb;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JobClassFixer implements LocalQuickFix {
    private static final String QUARTZ_JOB_NAME = "org.quartz.Job";
    private static final String QUARTZ_JOB_CONTEXT = "org.quartz.JobExecutionContext";
    private static final String JRAF_JOB_NAME = "com.sunline.gfnfrs.core.quartz.JrafJob";
    private static final String JRAF_CONTEXT  = "com.sunline.gfnfrs.core.quartz.JrafJobExecutionContext";
    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getName() {
        return "Job fixer";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return getName();
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        PsiElementFactory elementFactory = JavaPsiFacade.getInstance(project).getElementFactory();
        PsiElement element = descriptor.getPsiElement();
        if (!(element instanceof PsiClass)) {
            return;
        }
        PsiClass psiClass = (PsiClass) element;
        PsiReferenceList implementRefList = psiClass.getImplementsList();
        if (implementRefList == null) {
            return;
        }
        // 实现的所有interface
        PsiJavaCodeReferenceElement[] referenceElements = implementRefList.getReferenceElements();
        if (referenceElements == null) {
            return;
        }
        for(PsiJavaCodeReferenceElement javaCodeReferenceElement: referenceElements) {
            String qName = javaCodeReferenceElement.getQualifiedName();
            if (QUARTZ_JOB_NAME.equals(qName)) {
                PsiJavaCodeReferenceElement prevEl = PsiTreeUtil.getPrevSiblingOfType(javaCodeReferenceElement, PsiJavaCodeReferenceElement.class);
                PsiJavaCodeReferenceElement nextEl = PsiTreeUtil.getNextSiblingOfType(javaCodeReferenceElement, PsiJavaCodeReferenceElement.class);
                if (prevEl == null && nextEl == null) {
                    implementRefList.deleteChildRange(implementRefList.getFirstChild(),
                            implementRefList.getLastChild());
                } else if (prevEl == null) {
                    implementRefList.deleteChildRange(javaCodeReferenceElement, nextEl.getPrevSibling());
                } else {
                    implementRefList.deleteChildRange(prevEl.getNextSibling(), javaCodeReferenceElement);
                }
            }
        }
        // 继承JrafJob
        PsiReferenceList extendRefList = psiClass.getExtendsList();
        if (extendRefList != null && Optional.ofNullable(PsiTreeUtil.getChildrenOfType(extendRefList, PsiJavaCodeReferenceElement.class)).orElse(new PsiJavaCodeReferenceElement[0]).length > 0) {
            return;
        }
        PsiClass jrafJobClass = javaPsiFacade.findClass(JRAF_JOB_NAME, GlobalSearchScope.allScope(project));
        if (jrafJobClass == null) {
            return;
        }
        PsiJavaCodeReferenceElement extJvCdEl = elementFactory.createClassReferenceElement(jrafJobClass);
        if (extendRefList != null) {
            extendRefList.add(extJvCdEl);
        }
        substituteMethod(javaPsiFacade, elementFactory, psiClass);
    }

    private void substituteMethod(JavaPsiFacade javaPsiFacade, PsiElementFactory elementFactory, PsiClass psiClass) {
        PsiMethod[] methods = psiClass.findMethodsByName("execute", false);
        for(PsiMethod method: methods) {
            if (method.getParameterList().getParameters().length != 1) {
                return;
            }
            PsiParameterList psiParameterList = method.getParameterList();
            PsiParameter parameter = psiParameterList.getParameters()[0];
            // 如果是
            if (QUARTZ_JOB_CONTEXT.equals(parameter.getType().getCanonicalText())) {
                parameter.delete();
                PsiClass jrafContext = javaPsiFacade.findClass(JRAF_CONTEXT, GlobalSearchScope.allScope(javaPsiFacade.getProject()));
                if (jrafContext == null) {
                    return;
                }
                PsiClassType jrafContextType = elementFactory.createType(jrafContext);
                psiParameterList.add(elementFactory.createParameter("fart", jrafContextType));
                break;
            }
        }
    }
}
