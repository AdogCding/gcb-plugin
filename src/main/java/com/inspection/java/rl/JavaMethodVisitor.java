package com.inspection.java.rl;

import com.inspection.java.utils.CrapTemplate;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.debugger.SourcePosition;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiAnonymousClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaToken;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodReferenceExpression;
import com.intellij.psi.PsiWhiteSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * check the number of rows
 */
public class JavaMethodVisitor extends JavaElementVisitor {
    private static final Logger logger = LoggerFactory.getLogger(JavaMethodVisitor.class);
    private final ProblemsHolder problemsHolder;
    private int rowCountLimit;
    private String comment;
    private final String DESCRIPTION_TEMPLATE = CrapTemplate.getCrapStmt("this method or class has more than 100 lines");
//    private final AppendCommentFixer fix;
    public JavaMethodVisitor(ProblemsHolder problemsHolder, String comment, int rowCountLimit) {
        this.problemsHolder = problemsHolder;
        this.rowCountLimit = rowCountLimit;
        this.comment = String.format("//%s", comment);
//        fix = new AppendCommentFixer(this.comment);
    }
    @Override
    public void visitMethod(PsiMethod method) {
        super.visitMethod(method);
        PsiElement parent = method.getParent();
        if (parent instanceof PsiAnonymousClass) {
            return;
        }
        if (method.getBody() == null) {
            return;
        }
        PsiCodeBlock codeBlock = method.getBody();
        if (codeBlock.getLBrace() == null) {
            return;
        }
        PsiJavaToken lBrace = codeBlock.getLBrace();
        if (hasFixed(lBrace)) {
            return;
        }
        SourcePosition startLine = SourcePosition.createFromElement(lBrace);
        PsiJavaToken RBrace = codeBlock.getRBrace();
        if (RBrace == null) {
            return;
        }
        SourcePosition endLine = SourcePosition.createFromElement(RBrace);
        if (endLine == null || startLine == null) {
            return;
        }
        int rowCount = endLine.getLine() - startLine.getLine();
        if (rowCount > rowCountLimit) {
            problemsHolder.registerProblem(method, DESCRIPTION_TEMPLATE, new AppendCommentFixer(comment));
        }
        logger.info(String.format("%s: %s", method.getName(), rowCount));
    }

    private boolean hasFixed(PsiJavaToken lBrace) {
        PsiElement el = lBrace.getNextSibling();
        while (el instanceof PsiWhiteSpace) {
            el = el.getNextSibling();
        }
        if (el instanceof PsiComment && el.textMatches(comment)) {
            return true;
        }
        return false;
    }
    @Override
    public void visitMethodReferenceExpression(PsiMethodReferenceExpression expression) {
        super.visitMethodReferenceExpression(expression);
    }
    public int getRowCountLimit() {
        return rowCountLimit;
    }

    public void setRowCountLimit(int rowCountLimit) {
        this.rowCountLimit = rowCountLimit;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
