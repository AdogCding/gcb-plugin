package com.inspection.java.jb;

import com.inspection.java.utils.CrapTemplate;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiReferenceList;

public class JobClassVisitor extends JavaElementVisitor {
    private final ProblemsHolder problemsHolder;
    private static final String JOB_NAME = "org.quartz.Job";
    private static final String IGNORED_CLASS_QNAME = "com.sunline.gfnfrs.core.quartz.JrafJob";
    private final String DEFAULT_TEMPLATE = CrapTemplate.getCrapStmt("继承JrafJob类，不要直接实现Quartz的Job");
    public JobClassVisitor(ProblemsHolder problemsHolder) {
        this.problemsHolder = problemsHolder;
    }

    @Override
    public void visitClass(PsiClass aClass) {
        String qName = aClass.getQualifiedName();
        if (qName == null) {
            return;
        }
        if (qName.equals(IGNORED_CLASS_QNAME)) {
            return;
        }
        PsiClassType[] implementedClassType = aClass.getImplementsListTypes();
        if (implementedClassType == null || implementedClassType.length == 0) {
            return;
        }
        boolean hasImplemented = false;
        for(PsiClassType classType: implementedClassType) {
            if (classType.getCanonicalText(false).equals(JOB_NAME)) {
                hasImplemented = true;
            }
        }
        if (!hasImplemented) {
            return;
        }
        // 不能继承其他的类
        PsiClassType[] extendedClassType = aClass.getExtendsListTypes();
        if (extendedClassType == null || extendedClassType.length >= 1) {
            problemsHolder.registerProblem(aClass, DEFAULT_TEMPLATE, (LocalQuickFix) null);
            return;
        }
        problemsHolder.registerProblem(aClass, DEFAULT_TEMPLATE, new JobClassFixer());
    }
}
