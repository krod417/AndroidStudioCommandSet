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
import com.intellij.codeInsight.template.impl.TextExpression;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.util.PsiTreeUtil;
import krod.netease.idea.android.postfix.internal.AbstractRichStringBasedPostfixTemplate;
import krod.netease.idea.android.utils.AndroidUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Postfix template for android View visibility.
 *
 * @author kikuchy
 */
public class VisibleGoneTemplate extends AbstractRichStringBasedPostfixTemplate {

    public static final Condition<PsiElement> IS_VIEW = new Condition<PsiElement>() {
        @Override
        public boolean value(PsiElement psiElement) {
            if (psiElement instanceof PsiReferenceExpression) {
                PsiIdentifier psiIdentifier = PsiTreeUtil.getChildOfType(psiElement, PsiIdentifier.class);
                if (psiIdentifier != null) {
                    if (("v".equals(psiIdentifier.getText()) || "g".equals(psiIdentifier.getText()))) {
                        PsiReferenceExpression child = PsiTreeUtil.getChildOfType(psiElement, PsiReferenceExpression.class);
                        if (child != null) {
                            return AndroidUtils.isViewType(child.getType());//获取当前变量类型  getCanonicalText当前变量类型的名称
                        }
                        return false;
                    } else {
                        return AndroidUtils.isViewType(((PsiReferenceExpression) psiElement).getType());
                    }
                } else {
                    return AndroidUtils.isViewType(((PsiReferenceExpression) psiElement).getType());
                }
            }
            return false;
        }
    };

    public VisibleGoneTemplate() {
        this("sv");
    }

    public VisibleGoneTemplate(@NotNull String alias) {
        super(alias, "(expr) ? View.VISIBLE : View.GONE", IS_VIEW);
    }

    @Override
    public String getTemplateString(@NotNull PsiElement element) {
        PsiIdentifier psiIdentifier = PsiTreeUtil.getChildOfType(element, PsiIdentifier.class);
        String status = "VISIBLE";
        if (psiIdentifier != null) {
            if ("g".equals(psiIdentifier.getText())) {
                status = "GONE";
            }
        }
        return "$expr$.setVisibility(View." + status + ");$END$";
    }

    @Override
    protected void addExprVariable(@NotNull PsiElement expr, Template template) {
        PsiIdentifier psiIdentifier = PsiTreeUtil.getChildOfType(expr, PsiIdentifier.class);
        String value = expr.getText();
        if (psiIdentifier != null && psiIdentifier.getText().matches("(g|v)")) {
            value = PsiTreeUtil.getChildOfType(expr, PsiReferenceExpression.class).getText();
        }
        template.addVariable("expr", new TextExpression(value), false);
    }
}
