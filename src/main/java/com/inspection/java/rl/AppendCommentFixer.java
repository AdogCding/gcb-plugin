package com.inspection.java.rl;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class AppendCommentFixer implements LocalQuickFix {
    private final String comment;

    public AppendCommentFixer(String comment) {
        this.comment = comment;
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Sentence) @NotNull String getName() {
        return "AppendComment";
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Sentence) @NotNull String getFamilyName() {
        return getName();
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        PsiMethod method = (PsiMethod) descriptor.getPsiElement();
        PsiCodeBlock codeBlock = method.getBody();
        if (codeBlock == null) {
            return;
        }
        PsiElementFactory factory = JavaPsiFacade
                .getInstance(project)
                .getElementFactory();
        PsiCodeBlock substituteCodeBlock = factory
                .createCodeBlockFromText(codeBlock.getText(), codeBlock.getContext());
        PsiComment comment = factory.createCommentFromText(this.comment, null);
        substituteCodeBlock.addAfter(comment, substituteCodeBlock.getLBrace());
        codeBlock.replace(substituteCodeBlock);
    }
}
