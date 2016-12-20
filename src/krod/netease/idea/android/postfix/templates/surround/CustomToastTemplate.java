/*
 * Copyright (C) 2014 Bob Browning
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

import com.intellij.codeInsight.template.Macro;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.impl.*;
import com.intellij.codeInsight.template.macro.VariableOfTypeMacro;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.util.PsiTreeUtil;
import krod.netease.idea.android.postfix.internal.AbstractRichStringBasedPostfixTemplate;
import krod.netease.idea.android.postfix.macro.ResourceMacro;
import krod.netease.idea.android.postfix.macro.ToStringIfNeedMacro;
import krod.netease.idea.android.postfix.utils.AndroidPostfixTemplatesUtils;
import krod.netease.idea.android.utils.AndroidUtils;
import krod.netease.idea.android.utils.Constant;
import org.jetbrains.annotations.NotNull;

import static krod.netease.idea.android.postfix.utils.AndroidClassName.CONTEXT;


/**
 * Postfix template for android Toast.
 *
 * @author takahirom
 */
public class CustomToastTemplate extends AbstractRichStringBasedPostfixTemplate {

    public static final Condition<PsiElement> IS_CUSTOM_TOAST = new Condition<PsiElement>() {
        @Override
        public boolean value(PsiElement psiElement) {
            return AndroidUtils.checkCustomNonNull(psiElement, "(l|s){0,1}");
        }
    };

    public CustomToastTemplate() {
        this("ctoast");
    }

    public CustomToastTemplate(@NotNull String alias) {
        super(alias, "TemplateToast($expr$);", IS_CUSTOM_TOAST);
    }


    @Override
    public String getTemplateString(@NotNull PsiElement element) {
        return AndroidUtils.getTemplate(Constant.CTOAST_TEMPLATE, Constant.CTOAST_TEMPLATE_NORMAL);
        //return getStaticPrefix(TOAST, "makeText", element) + "($context$, $expr$, Toast.LENGTH_SHORT).show()$END$";
    }

    @Override
    protected void addExprVariable(@NotNull PsiElement expr, Template template) {
        final ToStringIfNeedMacro normalMacro = new ToStringIfNeedMacro();
        MacroCallNode macroCallNode = new MacroCallNode(normalMacro);
        macroCallNode.addParameter(new ConstantNode(AndroidUtils.getCheckValue(expr)));
        template.addVariable("expr", macroCallNode, false);
    }

    @Override
    protected void setVariables(@NotNull Template template, @NotNull PsiElement element) {
        MacroCallNode contextNode = new MacroCallNode(new VariableOfTypeMacro());
        contextNode.addParameter(new ConstantNode(CONTEXT.toString()));

        MacroCallNode durtionNode = new MacroCallNode(new ToStringIfNeedMacro());
        durtionNode.addParameter(new ConstantNode("Toast.LENGTH_SHORT"));
        PsiIdentifier psiIdentifier = PsiTreeUtil.getChildOfType(element, PsiIdentifier.class);

        if (!AndroidPostfixTemplatesUtils.IS_NON_NULL.value(element) && psiIdentifier != null && psiIdentifier.getText().matches("(l|s){0,1}")) {
            String identifier = psiIdentifier.getText();
            if ("s".equals(identifier)) {
                durtionNode.addParameter(new ConstantNode("Toast.LENGTH_LONG"));
            } else if ("l".equals(identifier)) {
                durtionNode.addParameter(new ConstantNode("Toast.LENGTH_SHORT"));

            }
        }
        template.addVariable("context", contextNode, new ConstantNode(""), false);
        template.addVariable("dur", durtionNode, new ConstantNode(""), false);
    }
}
