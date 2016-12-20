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
import com.intellij.codeInsight.template.macro.ClassNameMacro;
import com.intellij.codeInsight.template.macro.MacroUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.Nullable;

/**
 * macro for android Resource parameter.
 *
 * @author takahirom
 */
public class ResourceMacro extends Macro {


    public String getName() {
        return "resource";
    }

    public String getPresentableName() {
        return "resource";
    }

    @Nullable
    @Override
    public Result calculateResult(Expression[] expressions, ExpressionContext context) {
        final String exprText = expressions[0].calculateResult(context).toString();
        return new TextResult(exprText);
    }

}
