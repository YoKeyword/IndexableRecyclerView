package me.yokeyword.indexablelistview;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import me.yokeyword.indexablelistview.help.PinyinComparator;
import me.yokeyword.indexablelistview.help.PinyinUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by YoKeyword on 16/3/20.
 */
public abstract class IndexableAdapter<T extends IndexEntity> extends BaseAdapter {
    protected static final int TYPE_CONTENT = -100;
    protected static final int TYPE_INDEX = -99;

    protected SparseArray<String> mTitleMap = new SparseArray<>();

    private List<T> mItems = new ArrayList<>();
    private List<T> mFilterList;

    private ViewHolder mContentHolder;
    private TextView mTvTitle;

    private List<String> mHeaderIndexs = new ArrayList<>();

    private int mHeaderSize;

    private IndexHeaderEntity[] mHeaderEntitied;

    private boolean mIsFilter;

    private ViewGroup mParentView;

    private boolean mNeedShutdown;

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        if (mIsFilter) {
            return mFilterList.size();
        }
        return mItems.size() + mTitleMap.size();
    }


    @Override
    public Object getItem(int position) {
        if (mIsFilter) {
            return mFilterList.get(position);
        }

        if (mTitleMap.get(position) != null) {
            return mTitleMap.get(position);
        } else {
            return mItems.get(getItemMappingPostion(position));
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (mIsFilter) {
            return TYPE_CONTENT;
        }

        if (mTitleMap.get(position) != null) {
            return TYPE_INDEX;
        }
        return TYPE_CONTENT;
    }

    @Override
    public int getViewTypeCount() {
        if (mIsFilter) {
            return 1;
        }
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);

        if (convertView == null) {
            if (type == TYPE_CONTENT) {
                mContentHolder = onCreateViewHolder(parent);
                convertView = mContentHolder.getItemView();
                convertView.setTag(mContentHolder);
            } else {
                mTvTitle = onCreateTitleViewHolder(parent);
                convertView = mTvTitle;
                convertView.setTag(mTvTitle);
            }
        } else {
            if (type == TYPE_CONTENT) {
                mContentHolder = (ViewHolder) convertView.getTag();
            } else {
                mTvTitle = (TextView) convertView.getTag();
            }
        }

        if (type == TYPE_CONTENT) {
            T item = getRealItem(position);
            onBindViewHolder(mContentHolder, item);
        } else {
            String firstSpell = getIndex(position);
            mTvTitle.setText(firstSpell);
        }

        return convertView;
    }

    protected abstract TextView onCreateTitleViewHolder(ViewGroup parent);

    protected abstract ViewHolder onCreateViewHolder(ViewGroup parent);

    protected abstract void onBindViewHolder(ViewHolder holder, T cityEntity);

    TextView getTitleTextView() {
        if (mTvTitle == null) {
            return onCreateTitleViewHolder(mParentView);
        }
        return mTvTitle;
    }

    void setParent(ViewGroup viewGroup) {
        this.mParentView = viewGroup;
    }

    protected class ViewHolder {
        View itemView;

        public ViewHolder(View view) {
            itemView = view;
        }

        View getItemView() {
            return itemView;
        }
    }

    void setNeedShutdown(boolean needShutdown) {
        mNeedShutdown = needShutdown;
    }

    boolean isNeedShutdown() {
        return mNeedShutdown;
    }

    void setDatas(final List<T> items, final IndexHeaderEntity... headerEntities) {
        mTitleMap.clear();
        mHeaderIndexs.clear();

        // 给数据源赋值 拼音,首字母
        if (processIndexEntity(items)) return;
        Collections.sort(items, new PinyinComparator());

        mItems = items;
        mHeaderEntitied = headerEntities;
        // 给headerEntity赋值 拼音
        processHeaderEntity(headerEntities);

        mHeaderSize = 0;
        for (int i = 0; i < headerEntities.length; i++) {
            if (mNeedShutdown) return;

            IndexHeaderEntity headerEntity = headerEntities[i];
            List<T> headerList = headerEntity.getHeaderList();

            mTitleMap.put(mHeaderSize, headerEntity.getHeaderTitle());
            mItems.addAll(mHeaderSize - i, headerList);

            mHeaderSize = mHeaderSize + 1 + headerList.size();

            for (T t : headerList) {
                if (mNeedShutdown) return;

                t.setFirstSpell(headerEntity.getIndex());
            }

            mHeaderIndexs.add(headerEntity.getIndex());
        }

        String currentFirstSpell = "";
        for (int i = mHeaderSize - headerEntities.length; i < mItems.size(); i++) {
            if (mNeedShutdown) return;

            String firstSpell = mItems.get(i).getFirstSpell();
            if (!currentFirstSpell.equals(firstSpell)) {
                mTitleMap.put(i + mTitleMap.size(), firstSpell);
                currentFirstSpell = firstSpell;
            }
        }
    }

    private boolean processIndexEntity(List<T> items) {
        for (T t : items) {
            if (mNeedShutdown) return true;
            String pinyin = PinyinUtil.getPingYin(t.getName());
            boolean isPolyphone = PinyinUtil.matchingPolyphone(pinyin);
            if (!isPolyphone) {
                t.setFirstSpell(pinyin.substring(0, 1).toUpperCase());
                t.setSpell(pinyin);
            } else {
                t.setFirstSpell(PinyinUtil.getMatchingFirstPinyin(pinyin).toUpperCase());
                t.setSpell(PinyinUtil.getMatchingPinyin(pinyin));
                t.setName(PinyinUtil.getMatchingHanzi(t.getName()));
            }
        }
        return false;
    }

    private void processHeaderEntity(IndexHeaderEntity[] headerEntities) {
        for (IndexHeaderEntity indexHeaderEntity : headerEntities) {
            for (Object o : indexHeaderEntity.getHeaderList()) {
                IndexEntity t = (IndexEntity) o;
                String pinyin = PinyinUtil.getPingYin(t.getName());
                boolean isPolyphone = PinyinUtil.matchingPolyphone(pinyin);
                if (!isPolyphone) {
                    t.setSpell(pinyin);
                } else {
                    t.setSpell(PinyinUtil.getMatchingPinyin(pinyin));
                    t.setName(PinyinUtil.getMatchingHanzi(t.getName()));
                }
            }
        }
    }

    public SparseArray<String> getTitleMap() {
        return mTitleMap;
    }

    public int getIndexMapPosition(int position) {
        return mTitleMap.keyAt(position);
    }

    public String getIndex(int position) {
        return mTitleMap.get(position);
    }

    public List<String> getHeaderIndexs() {
        return mHeaderIndexs;
    }

    int getHeaderSize() {
        return mHeaderEntitied.length;
    }

    int getLastestTitlePostion(int position) {
        for (int i = position; i >= 0; i--) {
            String value = mTitleMap.get(i);
            if (value != null) {
                return i - mTitleMap.indexOfKey(i);
            }
        }
        return position;
    }

    public T getRealItem(int position) {
        if (mIsFilter && mFilterList.size() > 0) {
            return mFilterList.get(position);
        }
        if (mItems.size() <= 0) return null;
        return mItems.get(getItemMappingPostion(position));
    }

    public int getItemMappingPostion(int position) {
        for (int i = position - 1; i >= 0; i--) {
            String value = mTitleMap.get(i);
            if (value != null) {
                return position - 1 - mTitleMap.indexOfKey(i);
            }
        }
        return position;
    }

    String getItemFirstSpell(int position) {
        if (position < mHeaderSize) {
            // header区域
            for (int i = position; i >= 0; i--) {
                if (mTitleMap.indexOfKey(i) > -1) {
                    String title = mTitleMap.get(i);
                    for (IndexHeaderEntity item : mHeaderEntitied) {
                        if (item.getHeaderTitle().equals(title)) {
                            return item.getIndex();
                        }
                    }
                }
            }
        }
        return mItems.get(getLastestTitlePostion(position)).getFirstSpell();
    }

    String getItemTitle(int position) {
        if (position < mHeaderSize) {
            // header区域
            for (int i = position; i >= 0; i--) {
                if (mTitleMap.indexOfKey(i) > -1) {
                    String title = mTitleMap.get(i);
                    for (IndexHeaderEntity item : mHeaderEntitied) {
                        if (item.getHeaderTitle().equals(title)) {
                            return item.getHeaderTitle();
                        }
                    }
                }
            }
        }
        return mItems.get(getLastestTitlePostion(position)).getFirstSpell();
    }


    public List<T> getSourceItems() {
        return mItems;
    }

    void setFilterDatas(List<T> filterList) {
        mIsFilter = filterList != null;

        if (filterList != null) {
            if (mFilterList == null) {
                mFilterList = new ArrayList<>();
            } else {
                mFilterList.clear();
            }
            mFilterList.addAll(filterList);
        } else {
            mFilterList = null;
        }

        notifyDataSetChanged();
    }

    boolean isFilter() {
        return mIsFilter;
    }
}
