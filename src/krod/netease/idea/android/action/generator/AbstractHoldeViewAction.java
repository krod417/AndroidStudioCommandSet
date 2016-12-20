package krod.netease.idea.android.action.generator;

import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;
import krod.netease.idea.android.action.write.HoldeViewAction;
import krod.netease.idea.android.utils.AndroidUtils;
import icons.AndroidIcons;
import org.jetbrains.annotations.NotNull;

public class AbstractHoldeViewAction extends BaseGenerateAction {

    public AbstractHoldeViewAction() {
        super(null);
    }

    @Override
    protected boolean isValidForFile(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        int offset = editor.getCaretModel().getOffset();
        PsiElement psiElement = file.findElementAt(offset);
        if(psiElement == null) {
            return false;
        }
        PsiMethod psiMethod = PsiTreeUtil.getParentOfType(psiElement, PsiMethod.class);//获取当前行中类的方法
        PsiReferenceExpression psiReferenceExpression = PsiTreeUtil.getParentOfType(psiElement, PsiReferenceExpression.class);//获取当前行中的参数
        //PsiClass psiClass = PsiTreeUtil.getParentOfType(psiElement, PsiClass.class);//获取当前行对应的类
        if(psiMethod == null) {
            return false;
        }
        if (psiReferenceExpression == null || !psiReferenceExpression.getText().startsWith("R.layout.")) {
            return false;
        }
        return "itemViewId".equals(psiMethod.getName());
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

        PsiMethod psiMethod = PsiTreeUtil.getParentOfType(psiElement, PsiMethod.class);//获取当前行中类的方法
        PsiReferenceExpression psiReferenceExpression = PsiTreeUtil.getParentOfType(psiElement, PsiReferenceExpression.class);//获取当前行中的参数
        if(psiMethod == null) {
            return ;
        }
        if (psiReferenceExpression == null || !psiReferenceExpression.getText().startsWith("R.layout.")) {
            return ;
        }

        PsiFile xmlFile = AndroidUtils.findXmlResource(project, psiReferenceExpression.getText());
        PsiClass psiClass = PsiTreeUtil.getParentOfType(psiElement, PsiClass.class);//获取当前行对应的类
        PsiMethod[] psiMethods = psiClass.findMethodsByName("afterViewCreated", false);//通过类查找对应名称的方法
        if (psiMethods.length <= 0) {
            return;
        }

        System.out.println(psiMethods[0].findElementAt(0).getClass().getName() + psiMethods[0].getChildren().length);
        generate(psiMethods[0], xmlFile, editor, file);
    }

    @Override
    public void update(AnActionEvent event) {
        super.update(event);
        event.getPresentation().setIcon(AndroidIcons.AndroidToolWindow);
    }

    public void generate(PsiMethod psiMethod, PsiFile xmlFile, Editor editor, @NotNull PsiFile file) {
        new HoldeViewAction(psiMethod, xmlFile).invoke(file.getProject(), editor, file);
    }

}
