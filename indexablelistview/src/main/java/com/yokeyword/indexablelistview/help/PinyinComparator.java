package com.yokeyword.indexablelistview.help;


import com.yokeyword.indexablelistview.IndexEntity;

import java.util.Comparator;

/**
 * Created by YoKeyword on 16/3/20.
 */
public class PinyinComparator<T extends IndexEntity> implements Comparator<T> {

    @Override
    public int compare(T lhs, T rhs) {
        return lhs.getFirstSpell().compareTo(rhs.getFirstSpell());
    }
}