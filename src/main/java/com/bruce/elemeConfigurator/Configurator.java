package com.bruce.elemeConfigurator;

import com.google.common.collect.Lists;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
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

    public static final String QUOTE = "\"";

    public Configurator(){
        super(null,null, new ImageIcon(Configurator.class.getClassLoader().getResource("icon/stackoverflow.ico")));
    }
    @Override
    public void actionPerformed(AnActionEvent e) {
        CaretModel caretModel = e.getData(LangDataKeys.EDITOR).getCaretModel();
        Caret currentCaret =
                caretModel.getCurrentCaret();
        VirtualFile currentFile = DataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        StringBuilder builder = generateDataFromFile(e, currentCaret, currentFile);
        if (builder == null) return;

        String copyData = builder.toString();

        String path = currentFile.getPath();

        copyDataAndShowStatusBar(path, copyData,e);

    }

    @Nullable
    private StringBuilder generateDataFromFile(AnActionEvent e, Caret currentCaret, VirtualFile currentFile) {
        String selectedText = currentCaret.getSelectedText();
        String extension = currentFile.getExtension();
        if(extension.equals("properties")){
            //do something with it.
            return handleWithProperyFile(selectedText);
        } else if (extension.equals("etpl")) {
            return handleWithEtplFile(selectedText,e.getProject());
        } else if (extension.equals("xml")){
            return handleWithXmlFile(selectedText,e.getProject());
        } else{
            return null;
        }

    }

    private StringBuilder handleWithXmlFile(String selectedText, Project project) {
        //todo need check this.
        List<String> strings = extractXmlPropFromText(selectedText);
        if(strings.isEmpty()){
            return null;
        }
        ExetractTextDialog dialog = new ExetractTextDialog(project,strings);
        boolean b = dialog.showAndGet();
        if(!b){
            return null;
        }
        Map<String, JTextField> fieldMap =
                dialog.getFieldMap();
        StringBuilder builder = new StringBuilder();
        fieldMap.forEach((k,v)->{
            builder.append(k).append("=").append(v.getText()).append("\n");
        });
        return builder;
    }

    private List<String> extractXmlPropFromText(String selectedText) {
        Pattern pattern = Pattern.compile("\\$\\{.*?\\}");
        Matcher matcher = pattern.matcher(selectedText);
        List<String> extractStrings = Lists.newArrayList();
        while(matcher.find()){
            int start = matcher.start();
            int end = matcher.end();
            extractStrings.add(selectedText.substring(start+2,end-1));
        }
        return extractStrings;
    }

    private StringBuilder handleWithEtplFile(String selectedText,Project project) {
        List<String> strings = extractConfigText(selectedText);
        if(strings.isEmpty()){
            return null;
        }
        ExetractTextDialog dialog = new ExetractTextDialog(project,strings);
        boolean b = dialog.showAndGet();
        if(!b){
            return null;
        }
        Map<String, JTextField> fieldMap =
                dialog.getFieldMap();
        StringBuilder builder = new StringBuilder("{");
        fieldMap.forEach((k,v)->{
            builder.append(QUOTE).append(k).append(QUOTE).append(":{\"ispassword\":0,\"version\":\"default\",\"value\":\"")
                    .append(v.getText()).append("\"},");
        });
        builder.deleteCharAt(builder.length()-1);
        builder.append("}");
        return builder;
    }

    @Nullable
    public static StringBuilder handleWithProperyFile(String selectedText) {
        List<String> extractProperty = extractProperty(selectedText);
        if(extractProperty.isEmpty()){
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (String s : extractProperty) {
            builder.append(s).append("={{_ .").append(s.replaceAll("\\.","_")).append("}}\n");
        }
        return builder;
    }

    @NotNull
    private static List<String> extractProperty(String selectedText) {
        List<String> lists = Lists.newArrayList();
        String[] split = selectedText.split("\n");
        for (String s : split) {
            String u = "";
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if(c!='='){
                    u+=c;
                }else {
                    break;
                }
            }
            lists.add(u);
        }
        return lists;
    }

    private void copyDataAndShowStatusBar(String path, String copyData,AnActionEvent e) {
        StringSelection stringSelection = new StringSelection(copyData);
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents(stringSelection, null);

                StatusBar statusBar = WindowManager.getInstance()
                .getStatusBar(DataKeys.PROJECT.getData(e.getDataContext()));
        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder("already copy "+copyData+" to clipboard", MessageType.INFO, null)
                .setFadeoutTime(7500)
                .createBalloon()
                .show(RelativePoint.getCenterOf(statusBar.getComponent()),
                        Balloon.Position.atRight);
    }

    @NotNull
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