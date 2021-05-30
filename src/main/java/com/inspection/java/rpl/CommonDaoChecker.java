package com.inspection.java.rpl;

import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonDaoChecker extends JavaElementVisitor {
    private boolean result = false;
    private final Logger logger = LoggerFactory.getLogger(CommonDaoChecker.class);

    @Override
    public void visitMethod(PsiMethod method) {
        PsiClass psiClass = method.getContainingClass();
        if (psiClass == null) {
            logger.debug("无法找到方法所在到class");
            return;
        }
        String qName = psiClass.getQualifiedName();
        String COMMON_DAO_NAME = "CommonDao";
        if (qName != null &&
                qName.contains(COMMON_DAO_NAME)) {
            setResult(true);
        }
    }

    private void setResult(boolean result) {
        this.result = result;
    }
    public boolean getResult() {
        return this.result;
    }
}
