package krod.netease.idea.android.action.search;

import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import krod.netease.idea.android.models.StringElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;

/**
 * @see com.intellij.ide.util.gotoByName.GotoFileModel
 */
public final class SearchStringModel extends FilteringGotoByModel<StringElement> implements DumbAware {

    public SearchStringModel(@NotNull Project project) {
        super(project, Extensions.getExtensions(ChooseByNameContributor.SYMBOL_EP_NAME));
    }

    @Nullable
    @Override
    protected StringElement filterValueFor(NavigationItem item) {
        return item instanceof StringElement ? (StringElement) item : null;
    }

    @Override
    public String getPromptText() {
        return "Enter string text:";
    }

    @Override
    public String getNotInMessage() {
        return "Not matches found";
    }

    @Override
    public String getNotFoundMessage() {
        return "Not found";
    }

    @Nullable
    @Override
    public String getCheckBoxName() {
        return null;
    }

    @Override
    public char getCheckBoxMnemonic() {
        return SystemInfo.isMac ? 'P' : 'n';
    }

    @Override
    public boolean loadInitialCheckBoxState() {
        return false;
    }

    @Override
    public void saveInitialCheckBoxState(boolean state) {
        // Do nothing
    }

    @NotNull
    @Override
    public String[] getSeparators() {
        return new String[]{"/", "\\"};
    }

    @Override
    public ListCellRenderer getListCellRenderer() {
        return new StringElementListCellRenderer();
    }

    @Nullable
    @Override
    public String getFullName(final Object element) {
        if (element instanceof StringElement) {
            return ((StringElement) element).getValue();
        }

        return getElementName(element);
    }

    @Override
    public boolean willOpenEditor() {
        return true;
    }

    @NotNull
    @Override
    public String removeModelSpecificMarkup(@NotNull String pattern) {
        if ((pattern.endsWith("/") || pattern.endsWith("\\"))) {
            return pattern.substring(0, pattern.length() - 1);
        }
        return pattern;
    }

    @Nullable
    @Override
    public synchronized Collection<StringElement> getFilterItems() {
        return super.getFilterItems();
    }

}
