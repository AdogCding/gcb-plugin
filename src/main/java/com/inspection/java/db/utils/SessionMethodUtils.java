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

import java.util.Collection;

public class SessionMethodUtils {

    public static boolean isSymmetric(PsiOpenSessionOperationBean open, PsiCloseSessionOperationBean close) {
        PsiElement openCaller = open.getCaller();
        PsiElement closeCaller = close.getCaller();
        return openCaller.equals(closeCaller);
    }

    public static PsiOpenSessionOperationBean getOpenSessionOperationBean(PsiMethodCallExpression mcExpr) {
        PsiReferenceExpression refExpr = mcExpr.getMethodExpression();
        PsiOpenSessionOperationBean opBean = new PsiOpenSessionOperationBean();
        PsiReferenceExpression callerRefExpr = PsiTreeUtil.getChildOfType(refExpr, PsiReferenceExpression.class);
        if (callerRefExpr == null) {
            return null;
        }
        PsiReference callerRef = callerRefExpr.getReference();
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
        PsiReference callerRef = callerRefExpr.getReference();
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



    public static boolean isOpenSessionMethod(PsiMethod method) {
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

    public static boolean isCloseSessionMethod(PsiMethod method) {
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

    public static boolean containsOpenSessionMethod(PsiMethod method) {
        Collection<PsiMethodCallExpression> methodCallExpressionCollection = PsiTreeUtil
                .findChildrenOfType(method, PsiMethodCallExpression.class);
        for(PsiMethodCallExpression mcExpr: methodCallExpressionCollection) {
            PsiReference mcRef = mcExpr.getMethodExpression().getReference();
            if (mcRef == null) {
                continue;
            }
            PsiElement mcResolved = mcRef.resolve();
            if (!(mcResolved instanceof PsiMethod)) {
                continue;
            }
            PsiMethod resolvedMethod = (PsiMethod) mcResolved;
            if (isOpenSessionMethod(resolvedMethod)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsCloseSessionMethod(PsiMethod method) {
        Collection<PsiMethodCallExpression> methodCallExpressionCollection = PsiTreeUtil
                .findChildrenOfType(method, PsiMethodCallExpression.class);
        for(PsiMethodCallExpression mcExpr: methodCallExpressionCollection) {
            PsiReference mcRef = mcExpr.getMethodExpression().getReference();
            if (mcRef == null) {
                continue;
            }
            PsiElement mcResolved = mcRef.resolve();
            if (!(mcResolved instanceof PsiMethod)) {
                continue;
            }
            PsiMethod resolvedMethod = (PsiMethod) mcResolved;
            if (isCloseSessionMethod(resolvedMethod)) {
                return true;
            }
        }
        return false;
    }
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
        return containsOpenSessionMethod(method);
    }


    public static boolean isCloseSessionMethodCall(PsiMethodCallExpression mcExpr) {
        PsiReferenceExpression mcRefExpr = mcExpr.getMethodExpression();
        PsiReference mcRef = mcRefExpr.getReference();
        if (mcRef == null) {
            return false;
        }
        PsiElement elRef = mcRef.resolve();
        if (!(elRef instanceof PsiMethod)) {
            return false;
        }
        PsiMethod method = (PsiMethod) elRef;
        return containsCloseSessionMethod(method);
    }
}
