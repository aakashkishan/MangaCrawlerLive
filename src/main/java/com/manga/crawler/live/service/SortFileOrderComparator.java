package com.manga.crawler.live.service;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SortFileOrderComparator implements Comparator<String> {

    @Override
    public int compare(String a, String b) {
        Pattern p = Pattern.compile("[^\\d]*[\\d]+[^\\d]+([\\d]+)");
        Matcher m1 = p.matcher(a);
        Matcher m2 = p.matcher(b);
        if(m1.find() && m2.find()) {
            return Integer.valueOf(m1.group(1)) - Integer.valueOf(m2.group(1));
        }else {
            return 0;
        }
    }

}
