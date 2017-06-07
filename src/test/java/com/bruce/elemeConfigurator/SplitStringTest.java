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
}
