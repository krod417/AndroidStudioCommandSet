package krod.netease.idea.android.setting.templates;

import com.intellij.icons.AllIcons;
import com.intellij.ide.highlighter.JavaFileHighlighter;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.ex.util.LexerEditorHighlighter;
import com.intellij.openapi.options.BaseConfigurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.SeparatorFactory;
import com.intellij.ui.roots.ToolbarPanel;

import krod.netease.idea.android.postfix.AndroidPostfixTemplateProvider;
import krod.netease.idea.android.postfix.templates.surround.VisibleGoneTemplate;
import krod.netease.idea.android.setting.TemplateSettings;
import krod.netease.idea.android.setting.ui.DialogsFactory;
import krod.netease.idea.android.setting.ui.StringResources;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Copyright 2014 Tomasz Morcinek. All rights reserved.
 */
public class TemplateConfigurable extends BaseConfigurable {

    private JPanel editorPanel = new JPanel(new GridLayout());

    private Editor editor;

    private TemplateSettings templateSettings;

    private final String templateName;
    private final String templateHeaderText;
    private final String displayName;
    private String template;

    public TemplateConfigurable(String displayName, String templateHeaderText, String templateName) {
        this.displayName = displayName;
        this.templateHeaderText = templateHeaderText;
        this.templateName = templateName;
    }

    public TemplateConfigurable(String displayName, String templateHeaderText, String templateName, String template) {
        this.displayName = displayName;
        this.templateHeaderText = templateHeaderText;
        this.templateName = templateName;
        this.template = template;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        templateSettings = TemplateSettings.getInstance();
        if (!templateSettings.isUsingCustomTemplateForName(templateName) && template != null) {
            System.out.println(template);
            templateSettings.setTemplateForName(templateName, template);
        }
        editor = createEditorInPanel(templateSettings.provideTemplateForName(templateName));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(400, 300));
        panel.add(SeparatorFactory.createSeparator(templateHeaderText, null), BorderLayout.PAGE_START);
        panel.add(new ToolbarPanel(editorPanel, new DefaultActionGroup(new ResetToDefaultAction())), BorderLayout.CENTER);
        return panel;
    }

    private Editor createEditorInPanel(String string) {
        EditorFactory editorFactory = EditorFactory.getInstance();
        Editor editor = editorFactory.createEditor(editorFactory.createDocument(string));

        EditorSettings editorSettings = editor.getSettings();
        editorSettings.setVirtualSpace(false);
        editorSettings.setLineMarkerAreaShown(false);
        editorSettings.setIndentGuidesShown(false);
        editorSettings.setLineNumbersShown(false);
        editorSettings.setFoldingOutlineShown(false);
        editorSettings.setAdditionalColumnsCount(3);
        editorSettings.setAdditionalLinesCount(3);

        editor.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            public void documentChanged(DocumentEvent e) {
                onTextChanged();
            }
        });

        ((EditorEx) editor).setHighlighter(getEditorHighlighter());

        addEditorToPanel(editor);

        return editor;
    }

    private LexerEditorHighlighter getEditorHighlighter() {
        return new LexerEditorHighlighter(new JavaFileHighlighter(), EditorColorsManager.getInstance().getGlobalScheme());
    }

    private void onTextChanged() {
        myModified = true;
    }

    private void addEditorToPanel(Editor editor) {
        editorPanel.removeAll();
        editorPanel.add(editor.getComponent());
    }

    @Override
    public void disposeUIResources() {
        if (editor != null) {
            EditorFactory.getInstance().releaseEditor(editor);
            editor = null;
        }
        templateSettings = null;
    }

    @Override
    public void apply() throws ConfigurationException {
        if (template != null) {
            new AndroidPostfixTemplateProvider().addTemplate(new VisibleGoneTemplate());
        }
        templateSettings.setTemplateForName(templateName, editor.getDocument().getText());
        setUnmodified();
    }

    @Override
    public void reset() {
        EditorFactory.getInstance().releaseEditor(editor);
        String coustomTemplate = templateSettings.provideTemplateForName(templateName);
        if (coustomTemplate == null && coustomTemplate.isEmpty()) {
            coustomTemplate = template;
        }
        editor = createEditorInPanel(coustomTemplate);
        setUnmodified();
    }

    private void setUnmodified() {
        setModified(false);
    }

    @Nls
    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return editorPanel;
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    class ResetToDefaultAction extends AnAction {

        ResetToDefaultAction() {
            super(StringResources.RESET_TO_DEFAULT_ACTION_TITLE, StringResources.RESET_TO_DEFAULT_ACTION_DESCRIPTION, AllIcons.Actions.Reset);
        }

        @Override
        public void actionPerformed(AnActionEvent anActionEvent) {
            if (DialogsFactory.openResetTemplateDialog()) {
                templateSettings.removeTemplateForName(templateName);
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    @Override
                    public void run() {
                        if (template == null || template.isEmpty()) {
                            editor.getDocument().setText(templateSettings.provideTemplateForName(templateName));
                        } else {
                            System.out.println(template);
                            editor.getDocument().setText(template);
                        }
                        setUnmodified();
                    }
                });
            }
        }

        @Override
        public void update(AnActionEvent e) {
            super.update(e);
            e.getPresentation().setEnabled(templateSettings.isUsingCustomTemplateForName(templateName));
        }
    }
}
