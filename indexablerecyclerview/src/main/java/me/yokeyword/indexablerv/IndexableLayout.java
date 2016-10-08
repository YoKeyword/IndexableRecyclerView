package me.yokeyword.indexablerv;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import me.yokeyword.indexablerecyclerview.R;

/**
 * RecyclerView + IndexBar
 * Created by YoKey on 16/10/6.
 */
@SuppressWarnings("unchecked")
public class IndexableLayout extends FrameLayout {
    private static final int TYPE_CENTER = 1;
    private static final int TYPE_MD = 2;

    private static int PADDING_RIGHT_OVERLAY;
    static final String INDEX_SIGN = "#";

    private Context mContext;
    private boolean mShowAllLetter = true;

    private ExecutorService mExecutorService;
    private Future mFuture;

    private RecyclerView mRecy;
    private IndexBar mIndexBar;

    private RealAdapter mRealAdapter;
    private LinearLayoutManager mLayoutManager;

    private IndexableAdapter mIndexableAdapter;

    private TextView mCenterOverlay, mMDOverlay;

    private int mBarTextColor, mBarFocusTextColor, mMDOverlayColor;
    private float mBarTextSize;
    private int mTypeOverlay;

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
     */
    public <T> void setAdapter(IndexableAdapter<T> adapter) {
        this.mIndexableAdapter = adapter;
        mRealAdapter.setIndexableAdapter(adapter);
        adapter.setLayout(this);
    }

    /**
     * add HeaderView Adapter
     */
    public <T> void addHeaderAdapter(IndexableHeaderAdapter<T> adapter) {
        mRealAdapter.addIndexableHeaderAdapter(adapter);
    }

    /**
     * set Index-ItemView click listener
     */
    public void setOnItemIndexClickListener(OnItemIndexClickListener listener) {
        mRealAdapter.setOnItemIndexClickListener(listener);
    }

    /**
     * set Content-ItemView click listener
     */
    public <T> void setOnItemContentClickListener(OnItemContentClickListener<T> listener) {
        mRealAdapter.setOnItemContentClickListener(listener);
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
    public void setMDOverlayStyle(int color) {
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
    public void setCenterOverlayStyle() {
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

    private void init(Context context, AttributeSet attrs) {
        this.mContext = context;
        this.mExecutorService = Executors.newSingleThreadExecutor();
        PADDING_RIGHT_OVERLAY = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IndexableRecyclerView);
            mBarTextColor = a.getColor(R.styleable.IndexableRecyclerView_indexBar_textColor, ContextCompat.getColor(context, R.color.default_indexBar_textColor));
            mBarTextSize = a.getDimension(R.styleable.IndexableRecyclerView_indexBar_textSize, getResources().getDimension(R.dimen.default_indexBar_textSize));
            mBarFocusTextColor = a.getColor(R.styleable.IndexableRecyclerView_indexBar_selectedTextColor, ContextCompat.getColor(context, R.color.dafault_indexBar_selectedTextColor));
            mMDOverlayColor = a.getColor(R.styleable.IndexableRecyclerView_MDOverlayColor, ContextCompat.getColor(context, R.color.default_MDOverlayColor));
            mTypeOverlay = a.getInt(R.styleable.IndexableRecyclerView_typeOverlay, 0);
            a.recycle();
        }

        if (mContext instanceof Activity) {
            ((Activity) mContext).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }

        mRecy = new RecyclerView(context);
        mIndexBar = new IndexBar(context);
        mRecy.setVerticalScrollBarEnabled(false);
        mRecy.setOverScrollMode(View.OVER_SCROLL_NEVER);
        addView(mRecy, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        mIndexBar = new IndexBar(context);
        mIndexBar.init(mBarTextColor, mBarFocusTextColor, mBarTextSize);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        addView(mIndexBar, params);

        mRealAdapter = new RealAdapter();
        mLayoutManager = new LinearLayoutManager(context);
        mRecy.setLayoutManager(mLayoutManager);
        mRecy.setHasFixedSize(true);
        mRecy.setAdapter(mRealAdapter);

        initListener();

        if (mTypeOverlay == TYPE_CENTER) {
            initCenterOverlay();
        } else if (mTypeOverlay == TYPE_MD) {
            initMDOverlay(mMDOverlayColor);
        }
    }

    private void initListener() {
        mRecy.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mIndexBar.setSelection(mLayoutManager.findFirstVisibleItemPosition());
            }
        });

        mIndexBar.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int touchPos = mIndexBar.getPositionForPoint(event.getY());
                if (touchPos < 0) return true;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        showOverlayView(event.getY(), touchPos);

                        if (touchPos != mIndexBar.getSelectionPosition()) {
                            mIndexBar.setSelectionPosition(touchPos);
                            mLayoutManager.scrollToPositionWithOffset(mIndexBar.getRecyPosition(), 0);
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
                mMDOverlay.setText(index);
            }
        }
        if (mCenterOverlay != null) {
            if (mCenterOverlay.getVisibility() != VISIBLE) {
                mCenterOverlay.setVisibility(VISIBLE);
            }
            String index = mIndexBar.getIndexList().get(touchPos);
            if (!mCenterOverlay.getText().equals(index)) {
                mCenterOverlay.setText(index);
            }
        }
//        if (mOnIndexSelectedListener != null) {
//            mListView.post(new Runnable() {
//                @Override
//                public void run() {
//                    int position = mListView.getFirstVisiblePosition();
//                    String realIndexTitle = mAdapter.getItemTitle(position);
//                    mOnIndexSelectedListener.onSelection(touchPos, realIndexTitle);
//                }
//            });
//        }
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
        mMDOverlay = new TextView(mContext);
        mMDOverlay.setBackgroundResource(R.drawable.indexable_bg_md_overlay);
        ViewCompat.setBackgroundTintList(mMDOverlay, ColorStateList.valueOf(color));
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

    void notifyDataChanged() {
        if (mFuture != null) {
            mFuture.cancel(true);
        }
        mFuture = mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                final ArrayList<EntityWrapper> datas = transform(mIndexableAdapter.getItems());
                if (datas == null) return;

                post(new Runnable() {
                    @Override
                    public void run() {
                        mRealAdapter.addDatas(datas);
                        mIndexBar.setDatas(mShowAllLetter, mRealAdapter.getItems());

                        if (mCenterOverlay == null && mMDOverlay == null) {
                            initCenterOverlay();
                        }
                    }
                });
            }
        });
    }

    /**
     * List<T> -> List<Indexable<T>
     */
    private <T> ArrayList<EntityWrapper<T>> transform(List<T> datas) {
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
                String indexName = mIndexableAdapter.getIndexName(item);
                String pinyin = PinyinUtil.getPingYin(indexName);

                // init EntityWrapper
                if (PinyinUtil.matchingLetter(pinyin)) {
                    entity.setIndex(pinyin.substring(0, 1).toUpperCase());
                    entity.setPinyin(pinyin);
                } else if (PinyinUtil.matchingPolyphone(pinyin)) {
                    entity.setIndex(PinyinUtil.gePolyphoneInitial(pinyin).toUpperCase());
                    entity.setPinyin(PinyinUtil.getPolyphonePinyin(pinyin));
                    String hanzi = PinyinUtil.getPolyphoneHanzi(indexName);
                    entity.setIndexName(hanzi);

                    mIndexableAdapter.setIndexName(item, hanzi);
                } else {
                    entity.setIndex(INDEX_SIGN);
                    entity.setPinyin(pinyin);
                }
                entity.setIndexTitle(entity.getIndex());
                entity.setData(item);
                entity.setOriginalPosition(i);

                String inital = entity.getIndex();

                List<EntityWrapper<T>> list;
                if (!map.containsKey(inital)) {
                    list = new ArrayList<>();
                    list.add(new EntityWrapper<T>(entity.getIndex(), EntityWrapper.TYPE_INDEX));
                    map.put(inital, list);
                } else {
                    list = map.get(inital);
                }

                list.add(entity);
            }

            ArrayList<EntityWrapper<T>> list = new ArrayList<>();
            for (List<EntityWrapper<T>> indexableEntities : map.values()) {
                Collections.sort(indexableEntities, new PinyinComparator<>(mIndexableAdapter));
                list.addAll(indexableEntities);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public interface OnItemContentClickListener<T> {
        void onItemClick(View v, int originalPosition, int currentPosition, T entity);
    }

    public interface OnItemIndexClickListener {
        void onItemClick(View v, int currentPosition, String indexName);
    }
}
