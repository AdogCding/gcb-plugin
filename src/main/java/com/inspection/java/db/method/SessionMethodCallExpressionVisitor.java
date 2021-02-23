package com.inspection.java.db.method;

import com.inspection.java.db.bean.PsiOpenSessionOperationBean;
import com.inspection.java.db.utils.SessionMethodUtils;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiMethodCallExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 判断是否存在A.open()后没有A.close()，假设open(), close()是操作的同一个session
 */
public class SessionMethodCallExpressionVisitor extends JavaElementVisitor {
    private static final Logger logger = LoggerFactory.getLogger(SessionRelatedMethodVisitor.class);
    private List<PsiOpenSessionOperationBean> openSessionBeanList;
    @Override
    public void visitMethodCallExpression(PsiMethodCallExpression expression) {
        if (SessionMethodUtils.isOpenSessionMethodCall(expression)) {
            PsiOpenSessionOperationBean openSessionBean = new PsiOpenSessionOperationBean();
        }
    }
}
