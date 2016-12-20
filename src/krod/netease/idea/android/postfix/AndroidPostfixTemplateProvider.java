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
package krod.netease.idea.android.postfix;

import com.intellij.codeInsight.template.postfix.templates.JavaPostfixTemplateProvider;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplate;
import com.intellij.util.containers.ContainerUtil;
import krod.netease.idea.android.postfix.internal.AbstractRichStringBasedPostfixTemplate;
import krod.netease.idea.android.postfix.templates.surround.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Postfix template provider for extension point.
 *
 * @author takahirom
 */
public class AndroidPostfixTemplateProvider extends JavaPostfixTemplateProvider {

    private final HashSet<PostfixTemplate> templates;

    public AndroidPostfixTemplateProvider() {
        templates = ContainerUtil.<PostfixTemplate>newHashSet(
                new ToastTemplate(),
                new LogTemplate(),
                new TextUtilsIsEmptyTemplate(),
                new TextUtilsIsNoEmptyTemplate(),
                new VisibleGoneTemplate(),
                new FindViewByIdTemplate(),
                new CustomLogTemplate(),
                new CustomToastTemplate()
        );
    }

    public void addTemplate(AbstractRichStringBasedPostfixTemplate template) {
        templates.add(template);
    }

    @NotNull
    @Override
    public Set<PostfixTemplate> getTemplates() {
        return templates;
    }
}
