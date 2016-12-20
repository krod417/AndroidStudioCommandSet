package krod.netease.idea.android.setting;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import krod.netease.idea.android.setting.templates.TemplateConfigurable;
import krod.netease.idea.android.utils.Constant;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Copyright 2014 Tomasz Morcinek. All rights reserved.
 */
public class MainConfigurable implements Configurable.Composite, Configurable.NoScroll, Configurable {

    private Configurable[] configurables;

    public MainConfigurable() {
        super();
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Android Code Generator";
    }

    @Nullable
    @NonNls
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Override
    public JComponent createComponent() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("<html>Here you can edit 'Android Code Generator' settings. In children pages you can edit template for each code generation method.</html>");
        label.setVerticalAlignment(SwingConstants.TOP);
        panel.add(label, BorderLayout.PAGE_START);
        return panel;
    }

    public void disposeUIResources() {
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
    }

    @Override
    public void reset() {
    }

    public Configurable[] getConfigurables() {
        if (configurables == null) {
            configurables = new Configurable[]{
//                    new TemplateConfigurable("Activity Template", "Setup Template for Activity code generation:", "Activity_template"),
//                    new TemplateConfigurable("Adapter Template", "Setup Template for Adapter code generation:", "Adapter_template"),
//                    new TemplateConfigurable("Fragment Template", "Setup Template for Fragment code generation:", "Fragment_template"),
//                    new TemplateConfigurable("Menu Template", "Setup Template for Menu code generation:", "Menu_template"),
                    new TemplateConfigurable(Constant.FINDID_TEMPLATE_NAME, "Setup Template for findId generation:", Constant.FINDID_TEMPLATE, Constant.FINDID_TEMPLATE_NORMAL),
                    new TemplateConfigurable(Constant.CLOG_TEMPLATE_NAME, "Setup Template for log generation:", Constant.CLOG_TEMPLATE, Constant.CLOG_TEMPLATE_NORMAL),
                    new TemplateConfigurable(Constant.CTOAST_TEMPLATE_NAME, "Setup Template for toast generation:", Constant.CTOAST_TEMPLATE, Constant.CTOAST_TEMPLATE_NORMAL)
            };
        }
        return configurables;
    }
}
