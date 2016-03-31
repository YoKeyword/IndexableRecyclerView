package com.yokeyword.indexablelistview.help;

import com.github.promeg.pinyinhelper.Pinyin;

/**
 * Created by YoKeyword on 16/3/20.
 */
public class PinyinUtil {

    /**
     * 将字符串中的中文转化为拼音,其他字符不变
     */
    public static String getPingYin(String inputString) {
        char[] input = inputString.trim().toCharArray();
        String output = "";
        for (int i = 0; i < input.length; i++) {
            output += Pinyin.toPinyin(input[i]);
        }
        return output.toLowerCase();
    }
}
