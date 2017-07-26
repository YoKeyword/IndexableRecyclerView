package me.yokeyword.indexablerv;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import me.yokeyword.indexablerecyclerview.R;
import me.yokeyword.indexablerv.database.DataObserver;
import me.yokeyword.indexablerv.database.HeaderFooterDataObserver;
import me.yokeyword.indexablerv.database.IndexBarDataObserver;

/**
 * RecyclerView + IndexBar
 * Created by YoKey on 16/10/6.
 */
@SuppressWarnings("unchecked")
public class IndexableLayout extends FrameLayout {
    // 快速排序，只比对首字母(默认)
    public static final int MODE_FAST = 0;
    // 全字母比较排序, 效率最低
    public static final int MODE_ALL_LETTERS = 1;
    // 每个字母模块内：无需排序，效率最高
    public static final int MODE_NONE = 2;

    private static int PADDING_RIGHT_OVERLAY;
    static final String INDEX_SIGN = "#";

    private Context mContext;
    private boolean mShowAllLetter = true;

    private ExecutorService mExecutorService;
    private Future mFuture;

    private RecyclerView mRecy;
    private IndexBar mIndexBar;
    /**
     * 保存正在Invisible的ItemView
     * <p>
     * 使用mLastInvisibleRecyclerViewItemView来保存当前Invisible的ItemView，
     * 每次有新的ItemView需要Invisible的时候，把旧的Invisible的ItemView设为Visible。
     * 这样就修复了View复用导致的Invisible状态传递的问题。
     */
    private View mLastInvisibleRecyclerViewItemView;

    private boolean mSticyEnable = true;
    private RecyclerView.ViewHolder mStickyViewHolder;
    private String mStickyTitle;

    private RealAdapter mRealAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private IndexableAdapter mIndexableAdapter;

    private TextView mCenterOverlay, mMDOverlay;

    private int mBarTextColor, mBarFocusTextColor;
    private float mBarTextSize, mBarTextSpace, mBarWidth;
    private Drawable mBarBg;

    private DataObserver mDataSetObserver;

    private int mCompareMode = MODE_FAST;
    private Comparator mComparator;
    private Handler mHandler;

    private HeaderFooterDataObserver<EntityWrapper> mHeaderFooterDataSetObserver = new HeaderFooterDataObserver<EntityWrapper>() {
        @Override
        public void onChanged() {
            if (mRealAdapter == null) return;
            mRealAdapter.notifyDataSetChanged();
        }

        @Override
        public void onAdd(boolean header, EntityWrapper preData, EntityWrapper data) {
            if (mRealAdapter == null) return;
            mRealAdapter.addHeaderFooterData(header, preData, data);
        }

        @Override
        public void onRemove(boolean header, EntityWrapper data) {
            if (mRealAdapter == null) return;
            mRealAdapter.removeHeaderFooterData(header, data);
        }
    };

    private IndexBarDataObserver mIndexBarDataSetObserver = new IndexBarDataObserver() {
        @Override
        public void onChanged() {
            mIndexBar.setDatas(mShowAllLetter, mRealAdapter.getItems());
        }
    };

    public IndexableLayout(Context context) {
        this(context, null);
    }

    public IndexableLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndexableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * set RealAdapter
     * {@link #setLayoutManager(RecyclerView.LayoutManager)}
     */
    public <T extends IndexableEntity> void setAdapter(final IndexableAdapter<T> adapter) {

        if (mLayoutManager == null) {
            throw new NullPointerException("You must set the LayoutManager first");
        }

        this.mIndexableAdapter = adapter;

        if (mDataSetObserver != null) {
            adapter.unregisterDataSetObserver(mDataSetObserver);
        }
        mDataSetObserver = new DataObserver() {

            @Override
            public void onInited() {
                onSetListener(IndexableAdapter.TYPE_ALL);
                onDataChanged();
            }

            @Override
            public void onChanged() {
                if (mRealAdapter != null) {
                    mRealAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onSetListener(int type) {
                // set listeners
                if ((type == IndexableAdapter.TYPE_CLICK_TITLE || type == IndexableAdapter.TYPE_ALL) && adapter.getOnItemTitleClickListener() != null) {
                    mRealAdapter.setOnItemTitleClickListener(adapter.getOnItemTitleClickListener());
                }
                if ((type == IndexableAdapter.TYPE_LONG_CLICK_TITLE || type == IndexableAdapter.TYPE_ALL) && adapter.getOnItemTitleLongClickListener() != null) {
                    mRealAdapter.setOnItemTitleLongClickListener(adapter.getOnItemTitleLongClickListener());
                }
                if ((type == IndexableAdapter.TYPE_CLICK_CONTENT || type == IndexableAdapter.TYPE_ALL) && adapter.getOnItemContentClickListener() != null) {
                    mRealAdapter.setOnItemContentClickListener(adapter.getOnItemContentClickListener());
                }
                if ((type == IndexableAdapter.TYPE_LONG_CLICK_CONTENT || type == IndexableAdapter.TYPE_ALL) && adapter.getOnItemContentLongClickListener() != null) {
                    mRealAdapter.setOnItemContentLongClickListener(adapter.getOnItemContentLongClickListener());
                }
            }
        };

        adapter.registerDataSetObserver(mDataSetObserver);
        mRealAdapter.setIndexableAdapter(adapter);
        if (mSticyEnable) {
            initStickyView(adapter);
        }
    }

    /**
     * add HeaderView Adapter
     */
    public <T> void addHeaderAdapter(IndexableHeaderAdapter<T> adapter) {
        adapter.registerDataSetObserver(mHeaderFooterDataSetObserver);
        adapter.registerIndexBarDataSetObserver(mIndexBarDataSetObserver);
        mRealAdapter.addIndexableHeaderAdapter(adapter);
    }

    /**
     * removeData HeaderView Adapter
     */
    public <T> void removeHeaderAdapter(IndexableHeaderAdapter<T> adapter) {
        try {
            adapter.unregisterDataSetObserver(mHeaderFooterDataSetObserver);
            adapter.unregisterIndexBarDataSetObserver(mIndexBarDataSetObserver);
            mRealAdapter.removeIndexableHeaderAdapter(adapter);
        } catch (Exception ignored) {
        }
    }

    /**
     * add FooterView Adapter
     */
    public <T> void addFooterAdapter(IndexableFooterAdapter<T> adapter) {
        adapter.registerDataSetObserver(mHeaderFooterDataSetObserver);
        adapter.registerIndexBarDataSetObserver(mIndexBarDataSetObserver);
        mRealAdapter.addIndexableFooterAdapter(adapter);
    }

    /**
     * removeData FooterView Adapter
     */
    public <T> void removeFooterAdapter(IndexableFooterAdapter<T> adapter) {
        try {
            adapter.unregisterDataSetObserver(mHeaderFooterDataSetObserver);
            adapter.unregisterIndexBarDataSetObserver(mIndexBarDataSetObserver);
            mRealAdapter.removeIndexableFooterAdapter(adapter);
        } catch (Exception ignored) {
        }
    }

    /**
     * set sort-mode
     * Deprecated {@link #setCompareMode(int)}
     */
    @Deprecated
    public void setFastCompare(boolean fastCompare) {
        setCompareMode(fastCompare ? MODE_FAST : MODE_ALL_LETTERS);
    }

    @IntDef({MODE_FAST, MODE_ALL_LETTERS, MODE_NONE})
    @Retention(RetentionPolicy.SOURCE)
    @interface CompareMode {
    }

    /**
     * set sort-mode
     */
    public void setCompareMode(@CompareMode int mode) {
        this.mCompareMode = mode;
    }

    /**
     * set sort-mode
     */
    public <T extends IndexableEntity> void setComparator(Comparator<EntityWrapper<T>> comparator) {
        this.mComparator = comparator;
    }

    /**
     * set Sticky Enable
     */
    public void setStickyEnable(boolean enable) {
        this.mSticyEnable = enable;
    }

    /**
     * display all letter-index
     */
    public void showAllLetter(boolean show) {
        mShowAllLetter = show;
    }

    /**
     * display Material Design OverlayView
     */
    public void setOverlayStyle_MaterialDesign(int color) {
        if (mMDOverlay == null) {
            initMDOverlay(color);
        } else {
            ViewCompat.setBackgroundTintList(mMDOverlay, ColorStateList.valueOf(color));
        }
        mCenterOverlay = null;
    }

    /**
     * display Center OverlayView
     */
    public void setOverlayStyle_Center() {
        if (mCenterOverlay == null) {
            initCenterOverlay();
        }
        mMDOverlay = null;
    }

    /**
     * get OverlayView
     */
    public TextView getOverlayView() {
        return mMDOverlay != null ? mMDOverlay : mCenterOverlay;
    }

    /**
     * get RecyclerView
     */
    public RecyclerView getRecyclerView() {
        return mRecy;
    }

    /**
     * Set the enabled state of this IndexBar.
     */
    public void setIndexBarVisibility(boolean visible) {
        mIndexBar.setVisibility(visible ? VISIBLE : GONE);
    }

    private void init(Context context, AttributeSet attrs) {
        this.mContext = context;
        this.mExecutorService = Executors.newSingleThreadExecutor();
        PADDING_RIGHT_OVERLAY = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IndexableRecyclerView);
            mBarTextColor = a.getColor(R.styleable.IndexableRecyclerView_indexBar_textColor, ContextCompat.getColor(context, R.color.default_indexBar_textColor));
            mBarTextSize = a.getDimension(R.styleable.IndexableRecyclerView_indexBar_textSize, getResources().getDimension(R.dimen.default_indexBar_textSize));
            mBarFocusTextColor = a.getColor(R.styleable.IndexableRecyclerView_indexBar_selectedTextColor, ContextCompat.getColor(context, R.color.default_indexBar_selectedTextColor));
            mBarTextSpace = a.getDimension(R.styleable.IndexableRecyclerView_indexBar_textSpace, getResources().getDimension(R.dimen.default_indexBar_textSpace));
            mBarBg = a.getDrawable(R.styleable.IndexableRecyclerView_indexBar_background);
            mBarWidth = a.getDimension(R.styleable.IndexableRecyclerView_indexBar_layout_width, getResources().getDimension(R.dimen.default_indexBar_layout_width));
            a.recycle();
        }

        if (mContext instanceof Activity) {
            ((Activity) mContext).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }

        mRecy = new RecyclerView(context);
        mRecy.setVerticalScrollBarEnabled(false);
        mRecy.setOverScrollMode(View.OVER_SCROLL_NEVER);
        addView(mRecy, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        mIndexBar = new IndexBar(context);
        mIndexBar.init(mBarBg, mBarTextColor, mBarFocusTextColor, mBarTextSize, mBarTextSpace);
        LayoutParams params = new LayoutParams((int) mBarWidth, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        addView(mIndexBar, params);

        mRealAdapter = new RealAdapter();
        mRecy.setHasFixedSize(true);
        mRecy.setAdapter(mRealAdapter);

        initListener();
    }

    /**
     * {@link #setAdapter(IndexableAdapter)}
     *
     * @param layoutManager One of LinearLayoutManager and GridLayoutManager
     */
    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager == null)
            throw new NullPointerException("LayoutManager == null");

        mLayoutManager = layoutManager;
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int spanSize = 0;
                    if (mRealAdapter.getItemViewType(position) == EntityWrapper.TYPE_TITLE) {
                        spanSize = gridLayoutManager.getSpanCount();
                    } else if (mRealAdapter.getItemViewType(position) == EntityWrapper.TYPE_CONTENT) {
                        spanSize = 1;
                    }
                    return spanSize;
                }
            });
        }

        mRecy.setLayoutManager(mLayoutManager);
    }

    private void initListener() {
        mRecy.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                processScrollListener();
            }
        });

        mIndexBar.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int touchPos = mIndexBar.getPositionForPointY(event.getY());
                if (touchPos < 0) return true;

                if (!(mLayoutManager instanceof LinearLayoutManager)) return true;
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mLayoutManager;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        showOverlayView(event.getY(), touchPos);

                        if (touchPos != mIndexBar.getSelectionPosition()) {
                            mIndexBar.setSelectionPosition(touchPos);

                            if (touchPos == 0) {
                                linearLayoutManager.scrollToPositionWithOffset(0, 0);
                            } else {
                                linearLayoutManager.scrollToPositionWithOffset(mIndexBar.getFirstRecyclerViewPositionBySelection(), 0);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (mCenterOverlay != null) mCenterOverlay.setVisibility(GONE);
                        if (mMDOverlay != null) mMDOverlay.setVisibility(GONE);
                        break;
                }
                return true;
            }
        });
    }

    private void processScrollListener() {
        if (!(mLayoutManager instanceof LinearLayoutManager)) return;

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mLayoutManager;

        int firstItemPosition;
        firstItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
        if (firstItemPosition == RecyclerView.NO_POSITION) return;

        mIndexBar.setSelection(firstItemPosition);

        if (!mSticyEnable) return;
        ArrayList<EntityWrapper> list = mRealAdapter.getItems();
        if (mStickyViewHolder != null && list.size() > firstItemPosition) {
            EntityWrapper wrapper = list.get(firstItemPosition);
            String wrapperTitle = wrapper.getIndexTitle();

            if (EntityWrapper.TYPE_TITLE == wrapper.getItemType()) {
                if (mLastInvisibleRecyclerViewItemView != null && mLastInvisibleRecyclerViewItemView.getVisibility() == INVISIBLE) {
                    mLastInvisibleRecyclerViewItemView.setVisibility(VISIBLE);
                    mLastInvisibleRecyclerViewItemView = null;
                }

                mLastInvisibleRecyclerViewItemView = linearLayoutManager.findViewByPosition(firstItemPosition);

                if (mLastInvisibleRecyclerViewItemView != null) {
                    mLastInvisibleRecyclerViewItemView.setVisibility(INVISIBLE);
                }
            }

            // hide -> show
            if (wrapperTitle == null && mStickyViewHolder.itemView.getVisibility() == VISIBLE) {
                mStickyTitle = null;
                mStickyViewHolder.itemView.setVisibility(INVISIBLE);
            } else {
                stickyNewViewHolder(wrapperTitle);
            }

            // GirdLayoutManager
            if (mLayoutManager instanceof GridLayoutManager) {
                GridLayoutManager gridLayoutManager = (GridLayoutManager) mLayoutManager;
                if (firstItemPosition + gridLayoutManager.getSpanCount() < list.size()) {
                    for (int i = firstItemPosition + 1; i <= firstItemPosition + gridLayoutManager.getSpanCount(); i++) {
                        processScroll(linearLayoutManager, list, i, wrapperTitle);
                    }
                }
            } else {   // LinearLayoutManager
                if (firstItemPosition + 1 < list.size()) {
                    processScroll(linearLayoutManager, list, firstItemPosition + 1, wrapperTitle);
                }
            }
        }
    }

    private void processScroll(LinearLayoutManager layoutManager, ArrayList<EntityWrapper> list, int position, String title) {
        EntityWrapper nextWrapper = list.get(position);
        View nextTitleView = layoutManager.findViewByPosition(position);
        if (nextTitleView == null) return;
        if (nextWrapper.getItemType() == EntityWrapper.TYPE_TITLE) {
            if (nextTitleView.getTop() <= mStickyViewHolder.itemView.getHeight() && title != null) {
                mStickyViewHolder.itemView.setTranslationY(nextTitleView.getTop() - mStickyViewHolder.itemView.getHeight());
            }
            if (INVISIBLE == nextTitleView.getVisibility()) {
                //特殊情况：手指向下滑动的时候，需要及时把成为第二个可见View的TitleView设置Visible，
                // 这样才能配合StickyView制造两个TitleView切换的动画。
                nextTitleView.setVisibility(VISIBLE);
            }
            return;
        } else if (mStickyViewHolder.itemView.getTranslationY() != 0) {
            mStickyViewHolder.itemView.setTranslationY(0);
        }
        return;
    }

    private void stickyNewViewHolder(String wrapperTitle) {
        if ((wrapperTitle != null && !wrapperTitle.equals(mStickyTitle))) {

            if (mStickyViewHolder.itemView.getVisibility() != VISIBLE) {
                mStickyViewHolder.itemView.setVisibility(VISIBLE);
            }

            mStickyTitle = wrapperTitle;
            mIndexableAdapter.onBindTitleViewHolder(mStickyViewHolder, wrapperTitle);
        }
    }

    private <T extends IndexableEntity> void initStickyView(final IndexableAdapter<T> adapter) {
        mStickyViewHolder = adapter.onCreateTitleViewHolder(mRecy);
        mStickyViewHolder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.getOnItemTitleClickListener() != null) {
                    int position = mIndexBar.getFirstRecyclerViewPositionBySelection();
                    ArrayList<EntityWrapper> datas = mRealAdapter.getItems();
                    if (datas.size() > position && position >= 0) {
                        adapter.getOnItemTitleClickListener().onItemClick(
                                v, position, datas.get(position).getIndexTitle());
                    }
                }
            }
        });
        mStickyViewHolder.itemView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (adapter.getOnItemTitleLongClickListener() != null) {
                    int position = mIndexBar.getFirstRecyclerViewPositionBySelection();
                    ArrayList<EntityWrapper> datas = mRealAdapter.getItems();
                    if (datas.size() > position && position >= 0) {
                        return adapter.getOnItemTitleLongClickListener().onItemLongClick(
                                v, position, datas.get(position).getIndexTitle());
                    }
                }
                return false;
            }
        });
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) == mRecy) {
                mStickyViewHolder.itemView.setVisibility(INVISIBLE);
                addView(mStickyViewHolder.itemView, i + 1);
                return;
            }
        }
    }


    private void showOverlayView(float y, final int touchPos) {
        if (mIndexBar.getIndexList().size() <= touchPos) return;

        if (mMDOverlay != null) {
            if (mMDOverlay.getVisibility() != VISIBLE) {
                mMDOverlay.setVisibility(VISIBLE);
            }

            if (y < PADDING_RIGHT_OVERLAY - mIndexBar.getTop() && y >= 0) {
                y = PADDING_RIGHT_OVERLAY - mIndexBar.getTop();
            } else if (y < 0) {
                if (mIndexBar.getTop() > PADDING_RIGHT_OVERLAY) {
                    y = 0;
                } else {
                    y = PADDING_RIGHT_OVERLAY - mIndexBar.getTop();
                }
            } else if (y > mIndexBar.getHeight()) {
                y = mIndexBar.getHeight();
            }
            mMDOverlay.setY(mIndexBar.getTop() + y - PADDING_RIGHT_OVERLAY);

            String index = mIndexBar.getIndexList().get(touchPos);
            if (!mMDOverlay.getText().equals(index)) {
                if (index.length() > 1) {
                    mMDOverlay.setTextSize(30);
                }
                mMDOverlay.setText(index);
            }
        }
        if (mCenterOverlay != null) {
            if (mCenterOverlay.getVisibility() != VISIBLE) {
                mCenterOverlay.setVisibility(VISIBLE);
            }
            String index = mIndexBar.getIndexList().get(touchPos);
            if (!mCenterOverlay.getText().equals(index)) {
                if (index.length() > 1) {
                    mCenterOverlay.setTextSize(32);
                }
                mCenterOverlay.setText(index);
            }
        }
    }

    private void initCenterOverlay() {
        mCenterOverlay = new TextView(mContext);
        mCenterOverlay.setBackgroundResource(R.drawable.indexable_bg_center_overlay);
        mCenterOverlay.setTextColor(Color.WHITE);
        mCenterOverlay.setTextSize(40);
        mCenterOverlay.setGravity(Gravity.CENTER);
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics());
        LayoutParams params = new LayoutParams(size, size);
        params.gravity = Gravity.CENTER;
        mCenterOverlay.setLayoutParams(params);
        mCenterOverlay.setVisibility(INVISIBLE);

        addView(mCenterOverlay);
    }

    private void initMDOverlay(int color) {
        mMDOverlay = new AppCompatTextView(mContext);
        mMDOverlay.setBackgroundResource(R.drawable.indexable_bg_md_overlay);
        ((AppCompatTextView) mMDOverlay).setSupportBackgroundTintList(ColorStateList.valueOf(color));
        mMDOverlay.setSingleLine();
        mMDOverlay.setTextColor(Color.WHITE);
        mMDOverlay.setTextSize(38);
        mMDOverlay.setGravity(Gravity.CENTER);
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 72, getResources().getDisplayMetrics());
        LayoutParams params = new LayoutParams(size, size);
        params.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 33, getResources().getDisplayMetrics());
        params.gravity = Gravity.END;
        mMDOverlay.setLayoutParams(params);
        mMDOverlay.setVisibility(INVISIBLE);

        addView(mMDOverlay);
    }

    void onDataChanged() {
        if (mFuture != null) {
            mFuture.cancel(true);
        }
        mFuture = mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                final ArrayList<EntityWrapper> datas = transform(mIndexableAdapter.getItems());
                if (datas == null) return;

                getSafeHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        mRealAdapter.setDatas(datas);
                        mIndexBar.setDatas(mShowAllLetter, mRealAdapter.getItems());

                        if (mIndexableAdapter.getIndexCallback() != null) {
                            mIndexableAdapter.getIndexCallback().onFinished(datas);
                        }

                        processScrollListener();
                    }
                });
            }
        });
    }

    /**
     * List<T> -> List<Indexable<T>
     */
    private <T extends IndexableEntity> ArrayList<EntityWrapper<T>> transform(final List<T> datas) {
        try {
            TreeMap<String, List<EntityWrapper<T>>> map = new TreeMap<>(new Comparator<String>() {
                @Override
                public int compare(String lhs, String rhs) {
                    if (lhs.equals(INDEX_SIGN)) {
                        return rhs.equals(INDEX_SIGN) ? 0 : 1;
                    } else if (rhs.equals(INDEX_SIGN)) {
                        return -1;
                    }
                    return lhs.compareTo(rhs);
                }
            });

            for (int i = 0; i < datas.size(); i++) {
                EntityWrapper<T> entity = new EntityWrapper<>();
                T item = datas.get(i);
                String indexName = item.getFieldIndexBy();
                String pinyin = PinyinUtil.getPingYin(indexName);
                entity.setPinyin(pinyin);

                // init EntityWrapper
                if (PinyinUtil.matchingLetter(pinyin)) {
                    entity.setIndex(pinyin.substring(0, 1).toUpperCase());
                    entity.setIndexByField(item.getFieldIndexBy());
                } else if (PinyinUtil.matchingPolyphone(pinyin)) {
                    entity.setIndex(PinyinUtil.gePolyphoneInitial(pinyin).toUpperCase());
                    entity.setPinyin(PinyinUtil.getPolyphoneRealPinyin(pinyin));
                    String hanzi = PinyinUtil.getPolyphoneRealHanzi(indexName);
                    entity.setIndexByField(hanzi);
                    // 把多音字的真实indexField重新赋值
                    item.setFieldIndexBy(hanzi);
                } else {
                    entity.setIndex(INDEX_SIGN);
                    entity.setIndexByField(item.getFieldIndexBy());
                }
                entity.setIndexTitle(entity.getIndex());
                entity.setData(item);
                entity.setOriginalPosition(i);
                item.setFieldPinyinIndexBy(entity.getPinyin());

                String inital = entity.getIndex();

                List<EntityWrapper<T>> list;
                if (!map.containsKey(inital)) {
                    list = new ArrayList<>();
                    list.add(new EntityWrapper<T>(entity.getIndex(), EntityWrapper.TYPE_TITLE));
                    map.put(inital, list);
                } else {
                    list = map.get(inital);
                }

                list.add(entity);
            }

            ArrayList<EntityWrapper<T>> list = new ArrayList<>();
            for (List<EntityWrapper<T>> indexableEntities : map.values()) {
                if (mComparator != null) {
                    Collections.sort(indexableEntities, mComparator);
                } else {
                    Comparator comparator;
                    if (mCompareMode == MODE_FAST) {
                        comparator = new InitialComparator<T>();
                        Collections.sort(indexableEntities, comparator);
                    } else if (mCompareMode == MODE_ALL_LETTERS) {
                        comparator = new PinyinComparator<T>();
                        Collections.sort(indexableEntities, comparator);
                    }
                }

                list.addAll(indexableEntities);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Handler getSafeHandler() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }
}
