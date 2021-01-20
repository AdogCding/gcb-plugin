package com.inspection.java.rpl;

import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonDaoChecker extends JavaElementVisitor {
    private boolean result = false;
    private final Logger logger = LoggerFactory.getLogger(CommonDaoChecker.class);
    private final String COMMON_DAO_NAME = "CommonDao";

    @Override
    public void visitMethod(PsiMethod method) {
        PsiClass psiClass = method.getContainingClass();
        if (psiClass == null) {
            logger.debug("无法找到方法所在到class");
            return;
        }
        String qName = psiClass.getQualifiedName();
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
