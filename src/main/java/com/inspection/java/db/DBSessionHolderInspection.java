package com.inspection.java.db;

import com.inspection.java.db.method.SessionRelatedMethodVisitor;
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * 在方法M中，对象A调用了方法m1开启了s1并且没有关闭, A也必须调用方法m2关闭s1，且m1和m2在同一个作用域
 */
public class DBSessionHolderInspection extends AbstractBaseJavaLocalInspectionTool {
    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new SessionRelatedMethodVisitor(holder);
    }
}
