package krod.netease.idea.android.action;

import com.android.resources.ResourceType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.undo.UndoUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlToken;
import krod.netease.idea.android.models.NormalStringElement;
import icons.AndroidIcons;
import org.jetbrains.android.actions.CreateXmlResourceDialog;
import org.jetbrains.android.dom.manifest.Manifest;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.util.AndroidResourceUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CreateResourcesAction extends DumbAwareAction {

    public CreateResourcesAction() {
        super("Extract resource", "Extract resource ", AndroidIcons.Android);
    }

    @Override
    public void update(AnActionEvent event) {
        event.getPresentation().setVisible(true);
    }

    @Override
    public void actionPerformed(AnActionEvent event) {

        final PsiFile psiFile = event.getData(PlatformDataKeys.PSI_FILE);
        final Editor editor = event.getData(PlatformDataKeys.EDITOR);
        final Project project = event.getData(PlatformDataKeys.PROJECT);

        if(project == null) {
            return;
        }

        CommandProcessor.getInstance().executeCommand(project, new Runnable() {
            @Override
            public void run() {
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    @Override
                    public void run() {
                      doInvoke(project, editor, psiFile, getPsiElement(psiFile, editor), ResourceType.STRING);
                    }
                });

            }
        }, "Extract resource", "Android Studio Plugin");

    }

    @Nullable
    protected static PsiElement getPsiElement(@Nullable PsiFile file, @Nullable Editor editor) {
        if(file == null || editor == null) {
            return null;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = file.findElementAt(offset);
        return element;
    }

    protected static void doInvoke(Project project, Editor editor, PsiFile file, PsiElement element, ResourceType type) {
        String value = "";
        if (element != null) {
            PsiElement parentElement = element.getParent();
            if(parentElement instanceof XmlAttributeValue) {
                value = ((XmlAttributeValue) parentElement).getValue();
            }

            if(parentElement instanceof PsiLiteralExpression) {
                value = String.valueOf(((PsiLiteralExpression)parentElement).getValue());
            }
        }
        boolean isWhiteSpace = element instanceof PsiWhiteSpace;

        AndroidFacet facet = AndroidFacet.getInstance(file);

        assert facet != null;

        Module attribute = facet.getModule();
        CreateXmlResourceDialog dialog = new CreateXmlResourceDialog(attribute, type, null, value, true, (VirtualFile)null, file.getVirtualFile());
        dialog.setTitle("Extract Resource");
        if(!dialog.showAndGet()) {
            return;
        }
        VirtualFile resourceDir = dialog.getResourceDirectory();
        if(resourceDir == null) {
            //AndroidUtils.reportError(project, AndroidBundle.message("check.resource.dir.error", new Object[]{attribute}));
            return;
        }

        String fileName = dialog.getFileName();
        boolean isAdd = false;
        if (fileName.contains("colors.xml")) {
            type = ResourceType.COLOR;
        } else if (fileName.contains("strings.xml")) {
            type = ResourceType.STRING;
        } else if (fileName.contains("dimens.xml")) {
            type = ResourceType.DIMEN;
        } else if (fileName.contains("ids.xml")) {
            type = ResourceType.ID;
        }

        if (file instanceof XmlFile && (file.getContainingDirectory().getName().contains("layout") || file.getContainingDirectory().getName().contains("menu") || file.getContainingDirectory().getName().contains("drawable"))) {
            isAdd = true;
        } else if (file instanceof PsiJavaFile) {
            isAdd = true;
        }
        if(!AndroidResourceUtil.createValueResource(project, resourceDir, dialog.getResourceName(), type, dialog.getFileName(), dialog.getDirNames(), dialog.getValue())) {
            return;
        }

        if (isAdd) {
            String key = SearchStringsAction.createCodeString(new NormalStringElement(dialog.getResourceName(), dialog.getValue(), type.getName(), resourceDir.getName()), file.getFileType().getName(), isWhiteSpace);
            if (element instanceof XmlToken) {
                XmlAttribute xmlAttribute = PsiTreeUtil.getParentOfType(element, XmlAttribute.class);
                xmlAttribute.setValue(key);
            } else if (element instanceof PsiJavaToken || element instanceof  PsiWhiteSpace) {
                SearchStringsAction.updateCode(element, editor, project, file, key, isWhiteSpace);
            } else {
                System.out.println("不支持的类型" + element.getClass().getName());
            }
        }

        PsiDocumentManager.getInstance(project).commitAllDocuments();
        UndoUtil.markPsiFileForUndo(file);

    }

}
