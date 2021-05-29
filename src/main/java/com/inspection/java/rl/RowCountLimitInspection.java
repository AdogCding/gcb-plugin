package com.inspection.java.rl;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.ui.DocumentAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;

/**
 * 100 lines checker
 */
public class RowCountLimitInspection extends AbstractBaseJavaLocalInspectionTool {
    private String comment = "100Ignore";
    private int rowCountLimit = 90;
    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaMethodVisitor(holder, comment, rowCountLimit);
    }

    @Override
    public @Nullable JComponent createOptionsPanel() {
        JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        final JTextField comment = new JTextField(this.comment);
        final JTextField rowCountLimit = new JTextField(String.valueOf(this.rowCountLimit));
        RowCountLimitInspection inspection = this;
        comment.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                inspection.comment = comment.getText();
            }
        });
        rowCountLimit.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                try {
                    inspection.rowCountLimit = Integer.parseInt(rowCountLimit.getText());
                } catch (Exception exception) {
                    // no logger here
                }
            }
        });
        jPanel.add(comment);
        jPanel.add(rowCountLimit);
        return jPanel;
    }
}
