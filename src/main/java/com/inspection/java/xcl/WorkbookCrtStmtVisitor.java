package com.inspection.java.xcl;

import com.inspection.java.utils.CrapTemplate;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkbookCrtStmtVisitor extends JavaElementVisitor {
    private final ProblemsHolder problemsHolder;
    private static final String HSSF_WORKBOOK_QNAME = "org.apache.poi.hssf.usermodel.HSSFWorkbook";
    private static final String XSSF_WORKBOOK_QNAME = "org.apache.poi.xssf.usermodel.XSSFWorkbook";
    private static final String IGNORED_FILE = "com.sunline.gfnfrs.core.excel";
    private static final Logger logger = LoggerFactory.getLogger(WorkbookCrtStmtVisitor.class);
    private final String DESCRIPTION_TEMPLATE = CrapTemplate.getCrapStmt("It's not a good way to create workbook");
    public WorkbookCrtStmtVisitor(ProblemsHolder problemsHolder) {
        this.problemsHolder = problemsHolder;
    }
    @Override
    public void visitNewExpression(PsiNewExpression psiNewExpression) {
        PsiClass psiClass = PsiTreeUtil.getParentOfType(psiNewExpression, PsiClass.class);
        // 不检查这个class里的
        if (psiClass != null && psiClass.getQualifiedName()!= null && psiClass.getQualifiedName().equals(IGNORED_FILE)) {
            return;
        }
        PsiJavaCodeReferenceElement reference = psiNewExpression.getClassReference();
        if (reference == null) {
            return ;
        }
        PsiElement workbookClassPsiEl = reference.resolve();
        if (!(workbookClassPsiEl instanceof PsiClass)) {
            return;
        }
        PsiClass wbPsiClass = (PsiClass) workbookClassPsiEl;
        String qName = wbPsiClass.getQualifiedName();
        if (qName != null && (qName.equals(XSSF_WORKBOOK_QNAME) || qName.equals(HSSF_WORKBOOK_QNAME))) {
            problemsHolder.registerProblem(psiNewExpression, DESCRIPTION_TEMPLATE, new ImproperWorkBookCrtFixer());
        }
    }
}
