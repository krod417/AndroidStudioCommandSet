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

import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.impl.ConstantNode;
import com.intellij.codeInsight.template.impl.MacroCallNode;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.PsiTreeUtil;
import krod.netease.idea.android.postfix.internal.AbstractRichStringBasedPostfixTemplate;
import krod.netease.idea.android.postfix.macro.TagMacro;
import krod.netease.idea.android.postfix.macro.ToStringIfNeedMacro;
import krod.netease.idea.android.postfix.utils.AndroidPostfixTemplatesUtils;
import krod.netease.idea.android.utils.AndroidUtils;
import krod.netease.idea.android.utils.Constant;
import org.jetbrains.annotations.NotNull;


/**
 * Postfix template for android Log.
 *
 * @author takahirom
 */
public class CustomLogTemplate extends AbstractRichStringBasedPostfixTemplate {

    public final static String MATCHES = "(e|v|i|d)";

    public static final Condition<PsiElement> IS_LOG = new Condition<PsiElement>() {
        @Override
        public boolean value(PsiElement psiElement) {
            return AndroidUtils.checkCustomNonNull(psiElement, MATCHES);
        }
    };

    public CustomLogTemplate() {
        this("clog", "LogTemplate.?(TAG, expr);", IS_LOG);
    }

    public CustomLogTemplate(String name) {
        this(name, "LogTemplate.?(TAG, expr);", IS_LOG);
    }

    public CustomLogTemplate(@NotNull String name, @NotNull String example, @NotNull Condition<PsiElement> typeChecker) {
        super(name, example, typeChecker);
    }

    @Override
    public String getTemplateString(@NotNull PsiElement element) {
        return AndroidUtils.getTemplate(Constant.CLOG_TEMPLATE, Constant.CLOG_TEMPLATE_NORMAL);
    }

    @Override
    protected void addExprVariable(@NotNull PsiElement expr, Template template) {
        MacroCallNode macroCallNode = new MacroCallNode(new ToStringIfNeedMacro());
        macroCallNode.addParameter(new ConstantNode(AndroidUtils.getCheckValue(expr)));
        template.addVariable("expr", macroCallNode, false);
    }

    @Override
    protected void setVariables(@NotNull Template template, @NotNull PsiElement element) {
        MacroCallNode node = new MacroCallNode(new TagMacro());
        template.addVariable("TAG", node, new ConstantNode(""), false);

        MacroCallNode methodNode = new MacroCallNode(new ToStringIfNeedMacro());
        methodNode.addParameter(new ConstantNode("d"));
        PsiIdentifier psiIdentifier = PsiTreeUtil.getChildOfType(element, PsiIdentifier.class);

        if (!AndroidPostfixTemplatesUtils.IS_NON_NULL.value(element) && psiIdentifier != null && psiIdentifier.getText().matches(MATCHES)) {
            methodNode.addParameter(new ConstantNode(psiIdentifier.getText()));
        }

        template.addVariable("method", methodNode, new ConstantNode(""), false);
    }
}
