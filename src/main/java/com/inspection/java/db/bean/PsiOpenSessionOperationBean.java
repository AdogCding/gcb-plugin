package com.inspection.java.db.bean;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethodCallExpression;

/**
 * xxx.openSession()
 */
public class PsiOpenSessionOperationBean {
    // xxx
    private PsiElement caller;
    private PsiMethodCallExpression methodCallExpression;
    public PsiElement getCaller() {
        return caller;
    }
    public void setCaller(PsiElement caller) {
        this.caller = caller;
    }

    public PsiMethodCallExpression getMethodCallExpression() {
        return methodCallExpression;
    }

    public void setMethodCallExpression(PsiMethodCallExpression methodCallExpression) {
        this.methodCallExpression = methodCallExpression;
    }
}
