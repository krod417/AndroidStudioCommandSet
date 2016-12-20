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
import com.intellij.codeInsight.template.impl.TextExpression;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiType;
import krod.netease.idea.android.postfix.internal.AbstractRichStringBasedPostfixTemplate;
import krod.netease.idea.android.postfix.utils.AndroidPostfixTemplatesUtils;
import org.jetbrains.annotations.NotNull;


/**
 * Postfix template for android TextUtils class.
 *
 * @author kikuchy
 */
public class TextUtilsIsNoEmptyTemplate extends TextUtilsIsEmptyTemplate {

    public TextUtilsIsNoEmptyTemplate() {
       super("isnemp");
    }

    @Override
    public String getTemplateString(@NotNull PsiElement element) {
        return "if (!TextUtils.isEmpty($expr$)) {\n\r}$END$";
    }

}
