package krod.netease.idea.android.action.generator;

import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;
import krod.netease.idea.android.utils.AndroidUtils;
import icons.AndroidIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class AbstractActivityViewAction extends BaseGenerateAction {

    public AbstractActivityViewAction() {
        super(null);
    }

    @Override
    protected boolean isValidForFile(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        int offset = editor.getCaretModel().getOffset();
        PsiElement psiElement = file.findElementAt(offset);

        if(!PlatformPatterns.psiElement().inside(PsiMethodCallExpression.class).accepts(psiElement)) {//获取当前行是否存在方法调用
            return false;
        }
        PsiMethodCallExpression psiMethodCallExpression = PsiTreeUtil.getParentOfType(psiElement, PsiMethodCallExpression.class);//获取当前行中类的方法
        //PsiClass psiClass = PsiTreeUtil.getParentOfType(psiElement, PsiClass.class);//获取当前行对应的类
        if(psiMethodCallExpression == null) {
            return false;
        }
        PsiMethod psiMethod = psiMethodCallExpression.resolveMethod();
        if(psiMethodCallExpression == null) {
            return false;
        }
        return "setContentView".equals(psiMethod.getName());
    }

    @Override
    public void actionPerformedImpl(@NotNull final Project project, final Editor editor) {
        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
        if(file == null) {
            return;
        }

        int offset = editor.getCaretModel().getOffset();
        PsiElement psiElement = file.findElementAt(offset);
        if(psiElement == null) {
            return;
        }

        PsiMethodCallExpression psiMethodCallExpression = PsiTreeUtil.getParentOfType(psiElement, PsiMethodCallExpression.class);
        if(psiMethodCallExpression == null) {
            return;
        }

        PsiFile xmlFile = matchInflate(psiMethodCallExpression);
        generate(psiMethodCallExpression, xmlFile, editor, file);
    }

    @Nullable
    public static PsiFile matchInflate(PsiMethodCallExpression psiMethodCallExpression) {

        PsiExpression[] psiExpressions = psiMethodCallExpression.getArgumentList().getExpressions();
        if(psiExpressions.length == 0) {
            return null;
        }

        return AndroidUtils.findXmlResource((PsiReferenceExpression) psiExpressions[0]);
    }

    @Override
    public void update(AnActionEvent event) {
        super.update(event);
        event.getPresentation().setIcon(AndroidIcons.AndroidToolWindow);
    }

    abstract public void generate(PsiMethodCallExpression psiMethodCallExpression, PsiFile xmlFile, Editor editor, @NotNull PsiFile file);

}
