package krod.netease.idea.android.models;

public final class NormalStringElement extends AbstractStringElement {

    private final String name;

    private final String value;

    private final String parentDirName;

    private final String tag;

    public NormalStringElement(String name, String value, String tag, String parentDirName) {
        this.value = value;
        this.name = name;
        this.parentDirName = parentDirName;
        this.tag = tag;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getParentDirName() {
        return parentDirName;
    }

}
