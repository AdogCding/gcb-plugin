package com.inspection.java.db.utils;

import com.inspection.java.db.bean.PsiCloseSessionOperationBean;
import com.inspection.java.db.bean.PsiOpenSessionOperationBean;
import com.inspection.java.db.constants.CloseSessionMethodConstants;
import com.inspection.java.db.constants.OpenSessionMethodConstants;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.util.PsiTreeUtil;

public class SessionMethodUtils {

    public static boolean isSymmetric(PsiOpenSessionOperationBean open, PsiCloseSessionOperationBean close) {
        PsiElement openCaller = open.getCaller();
        PsiElement closeCaller = close.getCaller();
        return openCaller.equals(closeCaller);
    }
    /**
     * xxx.method是开操作, xxx要记录下来，
     * @param mcExpr 方法调用表达式
     * @return
     */
    public static PsiOpenSessionOperationBean getOpenSessionOperationBean(PsiMethodCallExpression mcExpr) {
        PsiReferenceExpression refExpr = mcExpr.getMethodExpression();
        PsiOpenSessionOperationBean opBean = new PsiOpenSessionOperationBean();
        PsiReferenceExpression callerRefExpr = PsiTreeUtil.getChildOfType(refExpr, PsiReferenceExpression.class);
        if (callerRefExpr == null) {
            return null;
        }
        PsiIdentifier callerId = PsiTreeUtil.getChildOfType(callerRefExpr, PsiIdentifier.class);
        if (callerId == null) {
            return null;
        }
        PsiReference callerRef = callerId.getReference();
        if (callerRef == null) {
            return null;
        }
        PsiElement callerEl = callerRef.resolve();
        if (callerEl == null) {
            return null;
        }
        opBean.setCaller(callerEl);
        return opBean;
    }

    /**
     *
     * @param methodCallExpression
     * @return
     */
    public static PsiCloseSessionOperationBean getCloseSessionOperationBean(PsiMethodCallExpression methodCallExpression) {
        PsiReferenceExpression refExpr = methodCallExpression.getMethodExpression();
        PsiCloseSessionOperationBean closeSessionOperationBean = new PsiCloseSessionOperationBean();
        PsiReferenceExpression callerRefExpr = PsiTreeUtil.getChildOfType(refExpr, PsiReferenceExpression.class);
        if (callerRefExpr == null) {
            return null;
        }
        PsiIdentifier callerId = PsiTreeUtil.getChildOfType(callerRefExpr, PsiIdentifier.class);
        if (callerId == null) {
            return null;
        }
        PsiReference callerRef = callerId.getReference();
        if (callerRef == null) {
            return null;
        }
        PsiElement callerEl = callerRef.resolve();
        if (callerEl == null) {
            return null;
        }
        closeSessionOperationBean.setCaller(callerEl);
        return closeSessionOperationBean;
    }

    /**
     * 查看xxx.xxx.method()，查看method()中是否有JrafSessiionFactory.openSession()
     * 如果有，认为是开启操作
     * @param mcExpression
     * @return
     */
    public static boolean isOpenSessionMethodCall(PsiMethodCallExpression mcExpression) {
        PsiReferenceExpression refExpr = mcExpression.getMethodExpression();
        PsiReference ref = refExpr.getReference();
        if (ref == null) {
            return false;
        }
        PsiElement el = ref.resolve();
        if (!(el instanceof PsiMethod)) {
            return false;
        }
        PsiMethod method = (PsiMethod) el;
        PsiClass clazz = method.getContainingClass();
        if (clazz == null) {
            return false;
        }
        String qName = clazz.getQualifiedName();
        if (qName == null) {
            return false;
        }
        if (!qName.contains(OpenSessionMethodConstants.CLASS_NAME)) {
            return false;
        }
        PsiIdentifier identifier = method.getNameIdentifier();
        if (identifier == null) {
            return false;
        }
        return identifier.getText().equals(OpenSessionMethodConstants.METHOD_NAME);
    }

    /**
     * xxx.xxxx.method()表达式，查看method()中是否有JarfSession.close()，
     * 如果有，认为是一个关闭操作
     * @param mcExpr
     * @return
     */
    public static boolean isCloseSessionMethodCall(PsiMethodCallExpression mcExpr) {
        PsiReferenceExpression refExpr = mcExpr.getMethodExpression();
        PsiReference ref = refExpr.getReference();
        if (ref == null) {
            return false;
        }
        PsiElement el = ref.resolve();
        if (!(el instanceof PsiMethod)) {
            return false;
        }
        PsiMethod method = (PsiMethod) el;
        PsiClass clazz = method.getContainingClass();
        if (clazz == null) {
            return false;
        }
        String qName = clazz.getQualifiedName();
        if (qName == null) {
            return false;
        }
        if (!qName.contains(CloseSessionMethodConstants.CLASS_NAME)) {
            return false;
        }
        PsiIdentifier identifier = method.getNameIdentifier();
        if (identifier == null) {
            return false;
        }
        return identifier.getText().equals(CloseSessionMethodConstants.METHOD_NAME);
    }
}
