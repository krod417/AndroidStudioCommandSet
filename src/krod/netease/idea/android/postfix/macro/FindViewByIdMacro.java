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

package krod.netease.idea.android.postfix.macro;

import com.intellij.codeInsight.template.*;
import com.intellij.codeInsight.template.impl.ConstantNode;
import com.intellij.codeInsight.template.impl.MacroCallNode;
import com.intellij.codeInsight.template.macro.VariableOfTypeMacro;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;
import krod.netease.idea.android.utils.AndroidUtils;
import krod.netease.idea.android.utils.Constant;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

import static krod.netease.idea.android.postfix.utils.AndroidClassName.ACTIVITY;
import static krod.netease.idea.android.postfix.utils.AndroidClassName.VIEW;


/**
 * Created by takam on 2015/05/05.
 */
public class FindViewByIdMacro extends Macro {

    public String getName() {
        return "find_view";
    }

    public String getPresentableName() {
        return "find_view";
    }


    public FindViewByIdMacro() {
    }

    @Nullable
    @Override
    public Result calculateResult(Expression[] expressions, ExpressionContext context) {
        if (expressions.length == 0) {
            return null;
        }

        Project project = context.getProject();
        Expression expression = expressions[0];
        PsiFile file = PsiUtilBase.getPsiFileInEditor(context.getEditor(), project);
        PsiElement psiElement = file.findElementAt(context.getEditor().getCaretModel().getOffset());
        PsiClass psiClass = PsiTreeUtil.getParentOfType(psiElement, PsiClass.class);

        if(PlatformPatterns.psiElement().inside(PsiLocalVariable.class).accepts(psiElement)) {
            PsiLocalVariable psiLocalVariable = PsiTreeUtil.getParentOfType(psiElement, PsiLocalVariable.class);
        }

        String template = AndroidUtils.getTemplate(Constant.FINDID_TEMPLATE, Constant.FINDID_TEMPLATE_NORMAL);
        PsiMethod[] psiMethods = psiClass.findMethodsByName(template, true);
        if (psiMethods.length <= 0) {
            template = Constant.FINDID_TEMPLATE_NORMAL;
        }
        String resource = expression.calculateResult(context).toString();
        String parentView = "";
        if (resource.contains(".")) {
            String temp = resource;
            resource = temp.substring(0, temp.indexOf("."));
            parentView = temp.substring(temp.indexOf(".")+ 1) + ".";
        }
        String text = "%s = %s %s%s (R.id.%s);";
        String fieldType = "";
        if (Constant.FINDID_TEMPLATE_NORMAL.equals(template)) {
            PsiField psiField = psiClass.findFieldByName(resource, true);
            fieldType = "(View)";
            if (psiField != null) {
                PsiType psiType = psiField.getType();
                fieldType = "(" + psiType.getPresentableText() + ")";
            }
        }
        final TextResult defaultResult = new TextResult(String.format(text, resource, fieldType, parentView, template, resource));
        return defaultResult;
//        if (contextVariable == null) {
//            return new TextResult("(" + viewTag + ")findViewById(" + resource + ")");
//        } else {
//            return new TextResult("(" + viewTag + ")" + contextVariable + ".findViewById(" + resource + ")");
//        }


    }

    private String getContextVariable(ExpressionContext context) {
        Result calculateResult = getVariableByFQDN(context, ACTIVITY.toString());
        if (calculateResult == null) {
            // Retry by view
            calculateResult = getVariableByFQDN(context, VIEW.toString());
            if (calculateResult == null) {
                return null;
            }
        }
        final String result = calculateResult.toString();
        if (result == null || "".equals(result)) {
            return null;
        }
        if ("this".equals(result)) {
            return null;
        }
        return result;
    }

    private Result getVariableByFQDN(ExpressionContext context, String fqn) {
        MacroCallNode callNode = new MacroCallNode(new VariableOfTypeMacro());
        callNode.addParameter(new ConstantNode(fqn));
        return callNode.calculateResult(context);
    }

}
