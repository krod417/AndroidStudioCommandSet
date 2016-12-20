package krod.netease.idea.android.action.search;

import com.intellij.ide.util.gotoByName.ChooseByNameFilter;
import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import krod.netease.idea.android.models.NormalStringElement;
import krod.netease.idea.android.models.PluralStringElement;
import krod.netease.idea.android.models.QuantityStringElement;
import krod.netease.idea.android.models.StringElement;
import icons.AndroidIcons;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class SearchStringFilterFactory {

    private Project project;

    public static SearchStringFilterFactory get(Project project) {
        return new SearchStringFilterFactory(project);
    }

    private SearchStringFilterFactory(Project project) {
        this.project = project;
    }

    public SearchStringFilter create(ChooseByNamePopup popup, SearchStringModel model) {
        return new SearchStringFilter(popup, model, project);
    }

    private class SearchStringFilter extends ChooseByNameFilter<StringElement> {

        private static final String STRINGS_XML = "strings.xml";
        private static final String COLOR_XML = "colors.xml";
        private static final String DIMEN_XML = "dimens.xml";

        private static final String TAG_RESOURCES = "resources";

        private static final String TAG_PLURALS = "plurals";

        private static final String TAG_ITEM = "item";

        private static final String TAG_QUANTITY = "quantity";

        private static final String TAG_NAME = "name";

        private SearchStringFilter(ChooseByNamePopup popup, SearchStringModel model, Project project) {
            super(popup, model, SearchStringConfiguration.getInstance(project), project);
        }

        /*
         * This method is called in super constructor.
         */
        @Override
        @NotNull
        protected List<StringElement> getAllFilterValues() {
            return loadStringElements();
        }

        @Override
        protected String textForFilterValue(@NotNull StringElement element) {
            return element.getValue();
        }

        @Override
        protected Icon iconForFilterValue(@NotNull StringElement element) {
            return AndroidIcons.AndroidFile;
        }

        private List<StringElement> loadStringElements() {
            List<StringElement> result = new LinkedList<StringElement>();
            List<PsiFile> fileList = new ArrayList<PsiFile>();
            String[] filenames = FilenameIndex.getAllFilenames(project);
            for (String filename : filenames) {
                if (filename.contains(STRINGS_XML) || filename.contains(COLOR_XML) || filename.contains(DIMEN_XML)) {
                    PsiFile[] files = FilenameIndex.getFilesByName(project, filename, GlobalSearchScope.projectScope(project));
                    for (PsiFile file : files) {
                        fileList.add(file);
                    }
                }
            }
            for (PsiFile psiFile : fileList) {
                if (psiFile != null) {
                    PsiDirectory dir = psiFile.getParent();
                    String parentDirName = dir != null ? dir.getName() : "";

                    try {
                        InputStream is = new ByteArrayInputStream(psiFile.getText().getBytes());

                        Document doc = JDOMUtil.loadDocument(is);
                        Element root = doc.getRootElement();
                        Element resources = root.getChild(TAG_RESOURCES);
                        if (resources != null) root = resources;

                        List<Element> elements = root.getChildren();
                        for (Element element : elements) {
                            if (TAG_PLURALS.equals(element.getName())) {
                                List<QuantityStringElement> quantities = new LinkedList<QuantityStringElement>();
                                List<Element> items = element.getChildren(TAG_ITEM);
                                for (Element item : items) {
                                    String key = item.getAttributeValue(TAG_QUANTITY);
                                    String value = item.getText();
                                    quantities.add(new QuantityStringElement(key, value, parentDirName));
                                }
                                result.add(new PluralStringElement(element.getAttributeValue(TAG_NAME), quantities, parentDirName));
                            } else {
                                String key = element.getAttributeValue(TAG_NAME);
                                String value = element.getText();
                                System.out.println(key  + "  " + value + " " +  element.getName() + " " + parentDirName);
                                result.add(new NormalStringElement(key, value, element.getName(), parentDirName));
                            }
                        }
                    } catch (JDOMException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return result;
        }
    }
}
