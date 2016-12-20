package krod.netease.idea.android.action.generator;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;
import krod.netease.idea.android.action.write.InflateThisExpressionAction;
import org.jetbrains.annotations.NotNull;

public class ActivityViewFieldVariable extends AbstractActivityViewAction {

    @Override
    public void generate(PsiMethodCallExpression psiMethodCallExpression, PsiFile xmlFile, Editor editor, @NotNull PsiFile file) {
        new InflateThisExpressionAction(psiMethodCallExpression, xmlFile).invoke(file.getProject(), editor, file);
    }

}
