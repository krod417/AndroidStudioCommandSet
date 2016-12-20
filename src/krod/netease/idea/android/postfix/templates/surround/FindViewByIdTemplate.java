/*
 * Copyright (C) 2015 takahirom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package krod.netease.idea.android.postfix.templates.surround;

import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.impl.ConstantNode;
import com.intellij.codeInsight.template.impl.MacroCallNode;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import krod.netease.idea.android.postfix.internal.AbstractRichStringBasedPostfixTemplate;
import krod.netease.idea.android.postfix.macro.FindViewByIdMacro;
import krod.netease.idea.android.postfix.utils.AndroidPostfixTemplatesUtils;
import krod.netease.idea.android.utils.AndroidUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Postfix template for android findViewById.
 *
 * @author takahirom
 */
public class FindViewByIdTemplate extends AbstractRichStringBasedPostfixTemplate {

//    public static final Condition<PsiElement> IS_NON_NULL_NUMBER = new Condition<PsiElement>() {
//        @Override
//        public boolean value(PsiElement element) {
//            System.out.println(element.getClass().getName());
//            return IS_NUMBER.value(element) && !AndroidPostfixTemplatesUtils.isAnnotatedNullable(element);
//        }
//
//    };


    public static final Condition<PsiElement> IS_FIELD = new Condition<PsiElement>() {
        @Override
        public boolean value(PsiElement psiElement) {
            if (AndroidPostfixTemplatesUtils.IS_NON_NULL.value(psiElement)) {
                PsiType psiType = ((PsiReferenceExpression)psiElement).getType();//获取当前变量类型  getCanonicalText当前变量类型的名称
                return AndroidUtils.isViewType(psiType);
            } else if (psiElement instanceof  PsiReferenceExpression) {
                PsiReferenceExpression psiReferenceExpression = PsiTreeUtil.getChildOfType(psiElement, PsiReferenceExpression.class);
                if (psiReferenceExpression != null && AndroidPostfixTemplatesUtils.IS_NON_NULL.value(psiReferenceExpression)) {
                    PsiType childPsiType = psiReferenceExpression.getType();//获取当前变量类型  getCanonicalText当前变量类型的名称
                    return childPsiType != null && AndroidUtils.isViewType(childPsiType);
                } else {
                    return false;
                }
            }
            return false;
        }
    };


    public FindViewByIdTemplate() {
        this("find", "findViewById(expr);");
    }

    public FindViewByIdTemplate(@NotNull String name, @NotNull String example) {
        super(name, example, IS_FIELD);
    }

    @Override
    public String getTemplateString(@NotNull PsiElement element) {
        return "$expr$$END$";
    }

    @Override
    protected void addExprVariable(@NotNull PsiElement expr, Template template) {
        final FindViewByIdMacro findViewByIdMacro = new FindViewByIdMacro();
        MacroCallNode macroCallNode = new MacroCallNode(findViewByIdMacro);
        macroCallNode.addParameter(new ConstantNode(expr.getText()));
        template.addVariable("expr", macroCallNode, false);
    }

//    代码参考执行局部变量生成
//    @Override
//    protected void onTemplateFinished(final TemplateManager manager, final Editor editor, Template template) {
//        final ActionManager actionManager = ActionManagerImpl.getInstance();
//        final String editorCompleteStatementText = "IntroduceVariable";
//        final AnAction action = actionManager.getAction(editorCompleteStatementText);
//        actionManager.tryToExecute(action, ActionCommand.getInputEvent(editorCompleteStatementText), null, ActionPlaces.UNKNOWN, true);
//    }

//    代码参考执行成员变量生成
//    @Override
//    protected void onTemplateFinished(final TemplateManager manager, final Editor editor, Template template) {
//        final ActionManager actionManager = ActionManagerImpl.getInstance();
//        final String editorCompleteStatementText = "IntroduceField";
//        final AnAction action = actionManager.getAction(editorCompleteStatementText);
//        actionManager.tryToExecute(action, ActionCommand.getInputEvent(editorCompleteStatementText), null, ActionPlaces.UNKNOWN, true);
//    }
}
