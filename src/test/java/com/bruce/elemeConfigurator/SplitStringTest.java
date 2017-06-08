package com.bruce.elemeConfigurator;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author bruce.ge
 * @Date 2017/6/7
 * @Description
 */
public class SplitStringTest {
    @Test
    public void testSplitString(){
        String s  ="archive.jdbc.url={{_ .archive_jdbc_url}}\n" +
                "archive.jdbc.user={{_ .archive_jdbc_user}}\n" +
                "archive.jdbc.password={{_ .archive_jdbc_password}}";



        Pattern pattern = Pattern.compile("\\{\\{_.*\\}\\}");

        Matcher matcher = pattern.matcher(s);

        List<String> stringList = Lists.newArrayList();

        while(matcher.find()){
            String matcherString = s.substring(matcher.start(),matcher.end());
            stringList.add(matcherString.substring(6,matcherString.length()-2));
        }
        for (String s1 : stringList) {
            System.out.println(s1);
        }

    }

    @Test
    public void testExtractEtplFromProperty(){
        String propertyString =  "scorpio.jdbc.acquireIncrement=10\n" +
                "scorpio.jdbc.idleConnectionTestPeriod=50\n" +
                "scorpio.jdbc.maxPoolSize=100\n" +
                "scorpio.jdbc.minPoolSize=10\n" +
                "scorpio.jdbc.maxStatements=0\n" +
                "scorpio.jdbc.checkoutTimeout=3000";

        List<String> lists = Lists.newArrayList();
        String[] split = propertyString.split("\n");
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
        for (String list : lists) {
            System.out.println(list);
        }

        StringBuilder builder = new StringBuilder();
        for (String s : lists) {
            builder.append(s).append("={{_ .").append(s.replaceAll("\\.","_")).append("}}\n");
        }

        System.out.println(builder.toString());
    }

    @Test
    public void handleWithProerty(){
        String propertyString =  "scorpio.jdbc.acquireIncrement=10\n" +
                "scorpio.jdbc.idleConnectionTestPeriod=50\n" +
                "scorpio.jdbc.maxPoolSize=100\n" +
                "scorpio.jdbc.minPoolSize=10\n" +
                "scorpio.jdbc.maxStatements=0\n" +
                "scorpio.jdbc.checkoutTimeout=3000";
        StringBuilder builder = Configurator.handleWithProperyFile(propertyString);
        System.out.println(builder.toString());
    }
}
