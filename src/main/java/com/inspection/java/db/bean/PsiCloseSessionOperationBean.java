package com.inspection.java.db.bean;

import com.intellij.psi.PsiElement;

/**
 * 关闭操作的抽象对象
 */
public class PsiCloseSessionOperationBean {

    private PsiElement caller;
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
