package krod.netease.idea.android.action.generator;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import krod.netease.idea.android.action.write.InflateThisExpressionAction;
import krod.netease.idea.android.annotator.InflateViewAnnotator;
import org.jetbrains.annotations.NotNull;

public class FieldViewInflateViewAction extends AbstractInflateViewAction {

    @Override
    public void generate(InflateViewAnnotator.InflateContainer inflateContainer, Editor editor, @NotNull PsiFile file) {
        new InflateThisExpressionAction(inflateContainer.getPsiLocalVariable(), inflateContainer.getXmlFile()).invoke(file.getProject(), editor, file);
    }

}
