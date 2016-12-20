package krod.netease.idea.android.action;

import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateEditingAdapter;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.codeInsight.template.impl.TemplateState;
import com.intellij.featureStatistics.FeatureUsageTracker;
import com.intellij.ide.actions.GotoActionBase;
import com.intellij.ide.util.gotoByName.ChooseByNameFilter;
import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.UndoConfirmationPolicy;
import com.intellij.openapi.command.undo.UndoUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlToken;
import krod.netease.idea.android.action.search.SearchStringFilterFactory;
import krod.netease.idea.android.action.search.SearchStringItemProvider;
import krod.netease.idea.android.action.search.SearchStringModel;
import krod.netease.idea.android.models.StringElement;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.intellij.openapi.application.ApplicationManager.getApplication;

public class SearchStringsAction extends GotoActionBase implements DumbAware {

    @Override
    protected void gotoActionPerformed(AnActionEvent e) {
        final Project project = e.getData(CommonDataKeys.PROJECT);
        if (project == null) return;

        FeatureUsageTracker.getInstance().triggerFeatureUsed("navigation.popup.file");

        final SearchStringModel searchStringModel = new SearchStringModel(project);
        GotoActionCallback<StringElement> callback = new GotoActionCallback<StringElement>() {
            @Override
            protected ChooseByNameFilter<StringElement> createFilter(@NotNull ChooseByNamePopup popup) {
                return SearchStringFilterFactory.get(project).create(popup, searchStringModel);
            }

            @Override
            public void elementChosen(ChooseByNamePopup popup, Object element) {
                if (element != null && element instanceof StringElement) {
                    insertToEditor(project, (StringElement) element);
                }
            }
        };

        SearchStringItemProvider provider = new SearchStringItemProvider(getPsiContext(e));
        showNavigationPopup(e, searchStringModel, callback, "strings matching pattern", true, true, provider);
    }

    private void insertToEditor(final Project project, final StringElement stringElement) {
        CommandProcessor.getInstance().executeCommand(project, new Runnable() {
            @Override
            public void run() {
                getApplication().runWriteAction(new Runnable() {
                    @Override
                    public void run() {
                        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                        if (editor != null) {
                            int offset = editor.getCaretModel().getOffset();
                            PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
                            PsiElement element = file.findElementAt(offset);

                            //Document document = editor.getDocument();
                            boolean isWhiteSpace = (element instanceof  PsiWhiteSpace);

                            String key = createCodeString(stringElement, file.getFileType().getName(), isWhiteSpace);

                            if (key != null) {
                                if (element instanceof XmlToken) {
                                    XmlAttribute xmlAttribute = PsiTreeUtil.getParentOfType(element, XmlAttribute.class);
                                    xmlAttribute.setValue(key);
                                } else if (element instanceof PsiJavaToken || element instanceof  PsiWhiteSpace) {
                                    updateCode(element, editor, project, file, key, isWhiteSpace);
                                } else {
                                    System.out.println("不支持的类型" + element.getClass().getName());
                                }

                                PsiDocumentManager.getInstance(project).commitAllDocuments();
                                UndoUtil.markPsiFileForUndo(file);

                                //document.insertString(offset, key);//执行插入操作
                                //editor.getCaretModel().moveToOffset(offset + key.length());//移动光标
                            }
                        }
                    }
                });
            }
        }, "WriteStringKeyCommand", "", UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION);
    }

    public static void updateCode(PsiElement element, Editor editor, Project project, PsiFile file, String value, boolean isWhiteSpace) {
        int offset2 = element.getTextOffset();
        editor.getCaretModel().moveToOffset(offset2);
        final RangeMarker marker1 = editor.getDocument().createRangeMarker(offset2, offset2);
        //如果是空白区域则不执行删除操作
        if (!isWhiteSpace) {
            TextRange elementRange1 = element.getTextRange();
            editor.getDocument().deleteString(elementRange1.getStartOffset(), elementRange1.getEndOffset());
        }
        TemplateImpl template = new TemplateImpl("", value, "");
        marker1.setGreedyToLeft(true);
        marker1.setGreedyToRight(true);
        TemplateManager.getInstance(project).startTemplate(editor, template, false, (Map)null, new TemplateEditingAdapter() {
            public void waitingForInput(Template template) {
                JavaCodeStyleManager.getInstance(project).shortenClassReferences(file, marker1.getStartOffset(), marker1.getEndOffset());
            }

            public void beforeTemplateFinished(TemplateState state, Template template) {
                JavaCodeStyleManager.getInstance(project).shortenClassReferences(file, marker1.getStartOffset(), marker1.getEndOffset());
            }
        });
    }

    public static String createCodeString(StringElement element, String fileType, boolean isWhiteSpace) {
        String value = null;
        if ("JAVA".equals(fileType)) {
            String startSuff = "";
            if (isWhiteSpace) {
                startSuff = "\n";
            }
            switch (element.getTag()) {
                case "string":
                    value = String.format("%sgetString(R.string.%s);", startSuff, element.getName());
                    break;
                case "dimen":
                    value = String.format("%sgetResources().getDimensionPixelOffset(R.dimen.%s);", startSuff, element.getName());
                    break;
                case "drawable":
                    value = String.format("%sContextCompat.getDrawable(this, R.drawable.%s);", startSuff, element.getName());
                    break;
                case "color":
                    value = String.format("%sContextCompat.getColor(this, R.color.%s);", startSuff, element.getName());
                    break;
                    default:
                        break;
            }
        } else if ("XML".equals(fileType)) {
            value = String.format("@%s/%s", element.getTag(), element.getName());
        }
        return value;
    }
}
