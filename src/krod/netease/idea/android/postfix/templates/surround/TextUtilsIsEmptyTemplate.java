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
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.ConstantNode;
import com.intellij.codeInsight.template.impl.MacroCallNode;
import com.intellij.codeInsight.template.impl.TextExpression;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import krod.netease.idea.android.postfix.internal.AbstractRichStringBasedPostfixTemplate;
import krod.netease.idea.android.postfix.macro.ToStringIfNeedMacro;
import krod.netease.idea.android.postfix.utils.AndroidPostfixTemplatesUtils;
import org.jetbrains.annotations.NotNull;


/**
 * Postfix template for android TextUtils class.
 *
 * @author kikuchy
 */
public class TextUtilsIsEmptyTemplate extends AbstractRichStringBasedPostfixTemplate {

    public static final Condition<PsiElement> IS_NON_NULL_STRING = new Condition<PsiElement>() {
        @Override
        public boolean value(PsiElement element) {
            if (AndroidPostfixTemplatesUtils.IS_NON_NULL.value(element)) {
                PsiType psiType = ((PsiReferenceExpression) element).getType();//获取当前变量类型  getCanonicalText当前变量类型的名称
                if (psiType != null && "String".endsWith(psiType.getPresentableText())) {
                    return true;
                }
            }
            return false;
        }
    };
    public TextUtilsIsEmptyTemplate() {
        this("isemp");
    }

    public TextUtilsIsEmptyTemplate(@NotNull String alias) {
        super(alias, "TextUtils.isEmpty(expr)", IS_NON_NULL_STRING);
    }

    @Override
    protected void onTemplateFinished(TemplateManager manager, Editor editor, Template template) {
    }

    @Override
    public String getTemplateString(@NotNull PsiElement element) {
        //getStaticPrefix(TEXT_UTILS, "isEmpty", element);
        return "if (TextUtils.isEmpty($expr$)) {\n\r}$END$";
    }

    @Override
    protected void addExprVariable(@NotNull PsiElement expr, Template template) {
        template.addVariable("expr", new TextExpression(expr.getText()), false);
    }

}
