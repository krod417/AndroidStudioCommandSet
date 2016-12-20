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
import com.intellij.codeInsight.template.impl.ConstantNode;
import com.intellij.codeInsight.template.impl.MacroCallNode;
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
public class ToastTemplate extends CustomToastTemplate{


    public ToastTemplate() {
        super("toast");
    }

    @Override
    public String getTemplateString(@NotNull PsiElement element) {
        return Constant.CTOAST_TEMPLATE_NORMAL;
    }
}
