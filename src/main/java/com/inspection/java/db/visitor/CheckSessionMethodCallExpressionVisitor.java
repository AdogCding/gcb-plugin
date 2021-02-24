package com.inspection.java.db.visitor;

import com.inspection.java.db.bean.PsiCloseSessionOperationBean;
import com.inspection.java.db.bean.PsiOpenSessionOperationBean;
import com.inspection.java.db.utils.SessionMethodUtils;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiMethodCallExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 判断是否存在A.open()后没有A.close()，假设open(), close()是操作的同一个session
 * 默认A只会持有一个JarfSession对象
 */
public class CheckSessionMethodCallExpressionVisitor extends JavaElementVisitor {
    private static final Logger logger = LoggerFactory.getLogger(SessionRelatedMethodVisitor.class);
    private final List<PsiOpenSessionOperationBean> openSessionBeanList = new ArrayList<>();
    @Override
    public void visitMethodCallExpression(PsiMethodCallExpression expression) {
        if (SessionMethodUtils.isOpenSessionMethodCall(expression)) {
            PsiOpenSessionOperationBean openBean = SessionMethodUtils.getOpenSessionOperationBean(expression);
            if (openBean == null) {
                return;
            }
            openSessionBeanList.add(openBean);
        }
        if (SessionMethodUtils.isCloseSessionMethodCall(expression)) {
            PsiCloseSessionOperationBean closeBean = SessionMethodUtils.getCloseSessionOperationBean(expression);
            if (closeBean == null) {
                return;
            }
            List<PsiOpenSessionOperationBean> closedOpenBeanList = new ArrayList<>();
            for(PsiOpenSessionOperationBean opBean: openSessionBeanList) {
                if (SessionMethodUtils.isSymmetric(opBean, closeBean)) {
                    closedOpenBeanList.add(opBean);
                }
            }
            openSessionBeanList.removeAll(closedOpenBeanList);
        }
    }
    public List<PsiOpenSessionOperationBean> getOpenSessionBeanList() {
        return openSessionBeanList;
    }
}
