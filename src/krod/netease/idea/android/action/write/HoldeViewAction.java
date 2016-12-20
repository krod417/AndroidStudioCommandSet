package krod.netease.idea.android.action.write;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.DocumentUtil;
import com.intellij.util.IncorrectOperationException;
import com.sun.tools.internal.jxc.ap.Const;
import krod.netease.idea.android.AndroidView;
import krod.netease.idea.android.setting.TemplateSettings;
import krod.netease.idea.android.utils.AndroidUtils;
import krod.netease.idea.android.utils.Constant;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HoldeViewAction extends BaseIntentionAction {
    final private PsiFile xmlFile;
    final private PsiElement psiElement;



    public HoldeViewAction(PsiElement psiElement, PsiFile xmlFile) {
        this.xmlFile = xmlFile;
        this.psiElement = psiElement;
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Android Studio Prettify";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile psiFile) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile) throws IncorrectOperationException {

        DocumentUtil.writeInRunUndoTransparentAction(new Runnable() {
            @Override
            public void run() {
                List<AndroidView> androidViews = AndroidUtils.getIDsFromXML(xmlFile);

                //获取方法的写入实体
                PsiCodeBlock psiCodeBlock = ((PsiMethod)psiElement).getBody();
                if(psiCodeBlock == null) {
                    return;
                }
                // collection class field
                // check if we need to set them
                PsiClass psiClass = PsiTreeUtil.getParentOfType(psiElement, PsiClass.class);
                Set<String> fieldSet = new HashSet<String>();
                for(PsiField field: psiClass.getFields()) {
                    fieldSet.add(field.getName());
                }

                // collect this.foo = "" and (this.)foo = ""
                // collection already init variables
                final Set<String> thisSet = new HashSet<String>();
                PsiTreeUtil.processElements(psiElement.getParent(), new PsiElementProcessor() {

                    @Override
                    public boolean execute(@NotNull PsiElement element) {

                        if(element instanceof PsiThisExpression) {
                            attachFieldName(element.getParent());
                        } else if(element instanceof PsiAssignmentExpression) {
                           attachFieldName(((PsiAssignmentExpression) element).getLExpression());
                        }

                        return true;
                    }

                    private void attachFieldName(PsiElement psiExpression) {

                        if(!(psiExpression instanceof PsiReferenceExpression)) {
                            return;
                        }

                        PsiElement psiField = ((PsiReferenceExpression) psiExpression).resolve();
                        if(psiField instanceof PsiField) {
                            thisSet.add(((PsiField) psiField).getName());
                        }
                    }
                });

                String template = AndroidUtils.getTemplate(Constant.FINDID_TEMPLATE, Constant.FINDID_TEMPLATE_NORMAL);
                if (psiClass.findMethodsByName(template, true).length <= 0) {
                    template = Constant.FINDID_TEMPLATE_NORMAL;
                }
                PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiCodeBlock.getProject());
                for (AndroidView v: androidViews) {

                    if(!fieldSet.contains(v.getFieldName())) {
                        String sb = "private " + v.getName() + " " + v.getFieldName() + ";";
                        //成员变量写入类中
                        psiClass.add(elementFactory.createFieldFromText(sb, psiClass));
                    }

                    if(!thisSet.contains(v.getFieldName())) {
                        String sb1;
                        sb1 = String.format("this.%s = %s(%s);", v.getFieldName(), template, v.getId());

                        //创建要写入的代码段
                        PsiStatement statementFromText = elementFactory.createStatementFromText(sb1, psiElement);
                        //为类创建成员方法
                        //psiClass.add(elementFactory.createMethodFromText(method.toString(), mClass));

                        //写入到对应方法中
                        psiCodeBlock.add(statementFromText);
                        //将initView();方法写在setContentViewStatement方法节点后面
                        //psiCodeBlock.addAfter(mFactory.createStatementFromText("initView();", mClass), setContentViewStatement);
                        //与之相反写在某个节点前面
                        //psiCodeBlock.addBefore()
                    }

                }
                JavaCodeStyleManager.getInstance(psiCodeBlock.getProject()).shortenClassReferences(psiElement.getParent());
                new ReformatAndOptimizeImportsProcessor(psiCodeBlock.getProject(), psiElement.getContainingFile(), true).run();

            }
        });

    }

    @NotNull
    @Override
    public String getText() {
        return "Field View Variables";
    }

    public static String getFindTemplate() {
       TemplateSettings templateSettings = TemplateSettings.getInstance();
       if (templateSettings.isUsingCustomTemplateForName(Constant.FINDID_TEMPLATE)) {
           return templateSettings.provideTemplateForName(Constant.FINDID_TEMPLATE);
       }
       return Constant.FINDID_TEMPLATE_NORMAL;
    }
}
