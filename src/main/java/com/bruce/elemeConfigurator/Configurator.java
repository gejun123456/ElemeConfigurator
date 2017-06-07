package com.bruce.elemeConfigurator;

import com.google.common.collect.Lists;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.ui.Messages;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author bruce.ge
 * @Date 2017/6/7
 * @Description
 */
public class Configurator extends AnAction {

    public static final String ONE_CHAR = "\"";

    public Configurator(){
        super(null,null, new ImageIcon(Configurator.class.getClassLoader().getResource("icon/stackoverflow.ico")));
    }
    @Override
    public void actionPerformed(AnActionEvent e) {
        String googleSite = "https://www.google.com/";
        CaretModel caretModel = e.getData(LangDataKeys.EDITOR).getCaretModel();
        Caret currentCaret =
                caretModel.getCurrentCaret();
        String selectedText = currentCaret.getSelectedText();
        List<String> strings = extractConfigText(selectedText);
        ExetractTextDialog dialog = new ExetractTextDialog(e.getProject(),strings);
        boolean b = dialog.showAndGet();
        if(!b){
            return;
        }
        Map<String, JTextField> fieldMap =
                dialog.getFieldMap();
        StringBuilder builder = new StringBuilder("");
        fieldMap.forEach((k,v)->{
            builder.append(ONE_CHAR).append(k).append(ONE_CHAR).append(":{\"ispassword\":0,\"version\":\"default\",\"value\":\"")
                    .append(v.getText()).append("\"},");
        });
        builder.deleteCharAt(builder.length()-1);
        Messages.showInfoMessage(e.getProject(),builder.toString(),"the text");
    }

    private List<String> extractConfigText(String selectedText) {
        Pattern pattern = Pattern.compile("\\{\\{_.*\\}\\}");

        Matcher matcher = pattern.matcher(selectedText);

        List<String> stringList = Lists.newArrayList();

        while(matcher.find()){
            String matcherString = selectedText.substring(matcher.start(),matcher.end());
            stringList.add(matcherString.substring(5,matcherString.length()-2));
        }
        return stringList;
    }
}