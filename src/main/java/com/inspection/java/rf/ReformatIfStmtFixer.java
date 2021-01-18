package com.inspection.java.rf;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiIfStatement;
import com.intellij.psi.PsiStatement;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class ReformatIfStmtFixer implements LocalQuickFix {

    @Override
    public @Nls(capitalization = Nls.Capitalization.Sentence) @NotNull String getName() {
        return "ReformatIfStmt";
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Sentence) @NotNull String getFamilyName() {
        return getName();
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        PsiIfStatement ifStmt = (PsiIfStatement) descriptor.getPsiElement();
        PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        PsiStatement substituteStmt = factory.createStatementFromText(ifStmt.getText(), ifStmt.getContext());
        ifStmt.replace(substituteStmt);
    }
}
