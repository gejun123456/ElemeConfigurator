package com.bruce.elemeConfigurator;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author bruce.ge
 * @Date 2017/6/7
 * @Description
 */
public class ExetractTextDialog extends DialogWrapper {

    private List<String> configureStrings;

    private Map<String,JTextField> fieldMap = new HashMap<>();
    public ExetractTextDialog(Project project, List<String> strings) {
        super(project, true);
        this.configureStrings = strings;
        setTitle("generate json for huskar config");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridBagLayout());
        int myGridy = 0;
        GridBagConstraints bag = new GridBagConstraints();
        for (String configureString : configureStrings) {
            bag.gridy = myGridy;
            bag.gridx=0;
            JLabel jLabel = new JLabel(configureString);
            jPanel.add(jLabel,bag);

            bag.gridx=1;

            JTextField jTextField = new JTextField();
            jTextField.setColumns(20);
            jPanel.add(jTextField,bag);
            fieldMap.put(configureString,jTextField);
            myGridy++;
        }
        return jPanel;
    }


    public Map<String, JTextField> getFieldMap() {
        return fieldMap;
    }

    public void setFieldMap(Map<String, JTextField> fieldMap) {
        this.fieldMap = fieldMap;
    }
}
