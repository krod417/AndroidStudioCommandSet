package krod.netease.idea.android.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import krod.netease.idea.android.AndroidView;
import krod.netease.idea.android.postfix.utils.AndroidPostfixTemplatesUtils;
import krod.netease.idea.android.setting.TemplateSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class AndroidUtils {

    @Nullable
    public static PsiFile findXmlResource(@Nullable PsiReferenceExpression referenceExpression) {
        if (referenceExpression == null) return null;

        PsiElement firstChild = referenceExpression.getFirstChild();
        if (firstChild == null || !"R.layout".equals(firstChild.getText())) {
            return null;
        }

        PsiElement lastChild = referenceExpression.getLastChild();
        if(lastChild == null) {
            return null;
        }

        String name = String.format("%s.xml", lastChild.getText());
        PsiFile[] foundFiles = FilenameIndex.getFilesByName(referenceExpression.getProject(), name, GlobalSearchScope.allScope(referenceExpression.getProject()));
        if (foundFiles.length <= 0) {
            return null;
        }

        return foundFiles[0];
    }

    public static List<AndroidView> getProjectViews(Project project) {

        List<AndroidView> androidViews = new ArrayList<AndroidView>();
        for(PsiFile psiFile: getLayoutFiles(project)) {
            androidViews.addAll(getIDsFromXML(psiFile));
        }

        return androidViews;
    }

    public static List<PsiFile> getLayoutFiles(Project project) {

        List<PsiFile> psiFileList = new ArrayList<PsiFile>();

        for (VirtualFile virtualFile : FilenameIndex.getAllFilesByExt(project, "xml")) {
            VirtualFile parent = virtualFile.getParent();
            if (parent != null && "layout".equals(parent.getName())) {
                String relative = VfsUtil.getRelativePath(virtualFile, project.getBaseDir(), '/');
                if (relative != null) {
                    PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
                    if (psiFile != null) {
                        psiFileList.add(psiFile);
                    }
                }
            }
        }

        return psiFileList;
    }

    @Nullable
    public static PsiFile findXmlResource(Project project, String layoutName) {

        if (!layoutName.startsWith("R.layout.")) {
            return null;
        }

        layoutName = layoutName.substring("R.layout.".length());

        String name = String.format("%s.xml", layoutName);
        PsiFile[] foundFiles = FilenameIndex.getFilesByName(project, name, GlobalSearchScope.allScope(project));
        if (foundFiles.length <= 0) {
            return null;
        }

        return foundFiles[0];
    }

    @NotNull
    public static List<AndroidView> getIDsFromXML(@NotNull PsiFile f) {
        final ArrayList<AndroidView> ret = new ArrayList<AndroidView>();
        f.accept(new XmlRecursiveElementVisitor() {
            @Override
            public void visitElement(final PsiElement element) {
                super.visitElement(element);
                String name;
                if (element instanceof XmlTag) {
                    XmlTag t = (XmlTag) element;
                    XmlAttribute id = t.getAttribute("android:id", null);
                    XmlAttribute layout = t.getAttribute("layout", null);//以=分隔name和value
                    String layoutValue;
                    if (layout != null && (layoutValue = layout.getValue()).startsWith("@layout/")) {
                        String value = layoutValue.split("@layout/")[1];
                        PsiFile[] foundFiles = FilenameIndex.getFilesByName(element.getProject(), String.format("%s.xml", value), GlobalSearchScope.allScope(element.getProject()));
                        if (foundFiles.length > 0) {
                            ret.addAll(getIDsFromXML(foundFiles[0]));
                        }
                        name = "View";
                    } else {
                        name = t.getName();
                    }
                    if (id == null) {
                        return;
                    }
                    final String val = id.getValue();
                    if (val == null) {
                        return;
                    }

                    ret.add(new AndroidView(val, t.getName(), id));

                }

            }
        });
        return ret;
    }

    @Nullable
    public static AndroidView getViewType(@NotNull PsiFile f, String findId) {

        // @TODO: replace dup for
        List<AndroidView> views = getIDsFromXML(f);

        for(AndroidView view: views) {
            if(findId.equals(view.getId())) {
                return view;
            }
        }

        return null;
    }

    public static String getTemplate(String templateName, String normalTemplate) {
        TemplateSettings templateSettings = TemplateSettings.getInstance();
        if (templateSettings.isUsingCustomTemplateForName(templateName)) {
            String template = templateSettings.provideTemplateForName(templateName);
            if (template != null && !template.isEmpty()) {
                return template;
            }
        }
        return normalTemplate;
    }

    public static boolean isViewType(PsiType psiType) {
        if (psiType == null) {
           return false;
        }
        String text = psiType.getCanonicalText();
        if (text.contains("android.widget") || text.contains("android.view")) {
            return true;
        }
        if (psiType.getSuperTypes().length > 0) {
            return isViewType(psiType.getSuperTypes()[0]);
        } else {
            return false;
        }
    }

    public static boolean checkCustomNonNull(PsiElement psiElement, String reg) {//"(t){0,1}(e|v|i|d)"
        if (psiElement instanceof PsiLiteralExpression) {
            return true;
        } else if (psiElement instanceof PsiReferenceExpression) {
            PsiIdentifier psiIdentifier = PsiTreeUtil.getChildOfAnyType(psiElement, PsiIdentifier.class);//获取离冒号最近的字符串
            if (AndroidPostfixTemplatesUtils.IS_NON_NULL.value(psiElement)) {
                return true;
            } else if (psiIdentifier != null && psiIdentifier.getText().matches(reg)) {
                PsiReferenceExpression psiReferenceExpression = PsiTreeUtil.getChildOfAnyType(psiElement, PsiReferenceExpression.class);//获取离冒号最近的变量
                PsiLiteralExpression psiLiteralExpression = PsiTreeUtil.getChildOfAnyType(psiElement, PsiLiteralExpression.class);//获取离冒号最近的常量
                if (psiLiteralExpression != null || AndroidPostfixTemplatesUtils.IS_NON_NULL.value(psiReferenceExpression)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    public static String getCheckValue(PsiElement psiElement) {//"(t){0,1}(e|v|i|d)"
        if (psiElement instanceof PsiLiteralExpression || AndroidPostfixTemplatesUtils.IS_NON_NULL.value(psiElement)) {
            return psiElement.getText();
        } else {
            PsiReferenceExpression psiReferenceExpression = PsiTreeUtil.getChildOfAnyType(psiElement, PsiReferenceExpression.class);//获取离冒号最近的变量
            PsiLiteralExpression psiLiteralExpression = PsiTreeUtil.getChildOfAnyType(psiElement, PsiLiteralExpression.class);//获取离冒号最近的常量
            if (psiLiteralExpression != null) {
               return psiLiteralExpression.getText();
            } else if (psiReferenceExpression != null) {
                return psiReferenceExpression.getText();
            }
        }
        return "";
    }
}
