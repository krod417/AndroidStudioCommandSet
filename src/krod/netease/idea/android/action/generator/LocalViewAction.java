package krod.netease.idea.android.action.generator;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import krod.netease.idea.android.action.write.InflateLocalVariableAction;
import krod.netease.idea.android.annotator.InflateViewAnnotator;
import org.jetbrains.annotations.NotNull;

public class LocalViewAction extends AbstractInflateViewAction {

    @Override
    public void generate(InflateViewAnnotator.InflateContainer inflateContainer, Editor editor, @NotNull PsiFile file) {
        new InflateLocalVariableAction(inflateContainer.getPsiLocalVariable(), inflateContainer.getXmlFile()).invoke(file.getProject(), editor, file);
    }

}
