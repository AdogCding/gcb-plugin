package com.inspection.java.db.bean;

import com.intellij.psi.PsiElement;

/**
 * xxx.openSession()
 */
public class PsiOpenSessionOperationBean {
    // xxx
    private PsiElement caller;
    // openSession()
    private PsiElement callee;

    public PsiElement getCaller() {
        return caller;
    }

    public void setCaller(PsiElement caller) {
        this.caller = caller;
    }

    public PsiElement getCallee() {
        return callee;
    }

    public void setCallee(PsiElement callee) {
        this.callee = callee;
    }
}