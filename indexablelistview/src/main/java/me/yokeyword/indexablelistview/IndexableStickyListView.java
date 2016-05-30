package me.yokeyword.indexablelistview;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.yokeyword.indexablelistview.help.PinyinUtil;

/**
 * Created by YoKeyword on 16/3/21.
 */
public class IndexableStickyListView extends FrameLayout implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {
    private static final int MSG_BIND_DATA = 1;
    private OnItemTitleClickListener mOnTitleListener;
    private OnItemContentClickListener mOnContentListener;

    private int mBarTextColor, mBarSelectedTextColor, mRightOverlayColor;
    private float mBarTextSize;
    private int mTypeOverlay;

    private ArrayList<View> mAddHeaderViewList;

    private ListView mListView;
    private IndexBar mIndexBar;
    private SearchLayout mSearchLayout;
    private ProgressBar mProgressBar;
    private TextView mTvOverlay;
    private AppCompatTextView mTvRightOverlay;

    private Context mContext;
    private IndexableAdapter mAdapter;

    private List<IndexEntity> mItems;
    private IndexHeaderEntity[] mHeaderEntities;

    private int mCurrentScrollItemPosition, mCurrentScrollItemTop;
    private int mTitleHeight;
    private SparseArray<String> mTitleMap;
    private TextView mStickView;

    private HandlerThread mBindDataHandlerThread;
    private Handler mBindDataHandler;

    public IndexableStickyListView(Context context) {
        super(context);
        init(context, null);
    }

    public IndexableStickyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public IndexableStickyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IndexableStickyListView);
            mBarTextColor = a.getColor(R.styleable.IndexableStickyListView_indexBar_textColor, getResources().getColor(R.color.default_indexBar_textcolor));
            mBarTextSize = a.getDimension(R.styleable.IndexableStickyListView_indexBar_textSize, getResources().getDimension(R.dimen.default_indexBar_textSize));
            mBarSelectedTextColor = a.getColor(R.styleable.IndexableStickyListView_indexBar_selected_textColor, getResources().getColor(R.color.dafault_indexBar_selected_textColor));
            mRightOverlayColor = a.getColor(R.styleable.IndexableStickyListView_indexListView_rightOverlayColor, getResources().getColor(R.color.default_indexListView_rightOverlayColor));
            mTypeOverlay = a.getInt(R.styleable.IndexableStickyListView_indexListView_type_overlay, 0);
            a.recycle();
        }


        if (mContext instanceof Activity) {
            ((Activity) mContext).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }

        mListView = new ListView(context);
        mListView.setVerticalScrollBarEnabled(false);
        mListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mListView.setDivider(null);
        addView(mListView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        mIndexBar = new IndexBar(context, mBarTextColor, mBarSelectedTextColor, mBarTextSize);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.RIGHT;
        params.topMargin = IndexBar.dp2px(context, 16);
        params.bottomMargin = params.topMargin;
        addView(mIndexBar, params);
        if (mTypeOverlay == 1) {
            showCenterOverlayView(true);
        } else if (mTypeOverlay == 2) {
            showRightOverlayView(true, mRightOverlayColor);
        }

        mSearchLayout = new SearchLayout(context);
        LayoutParams paramsLayout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mSearchLayout, paramsLayout);
        mSearchLayout.setVisibility(GONE);

        mProgressBar = new ProgressBar(context);
        int size = IndexBar.dp2px(context, 42);
        LayoutParams paramsBar = new LayoutParams(size, size);
        paramsBar.gravity = Gravity.CENTER;
        addView(mProgressBar, paramsBar);
        mProgressBar.setVisibility(INVISIBLE);

        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);

        mIndexBar.setOnIndexSelectedListener(new IndexBar.OnIndexTitleSelectedListener() {
            @Override
            public void onSelection(int position, String indexTitle) {
                if (mStickView != null) {
                    if (!mStickView.getText().toString().equals(indexTitle)) {
                        mStickView.setText(indexTitle);
                    }
                    if (mStickView.getY() != 0) {
                        mStickView.setY(0);
                    }
                }
            }
        });
    }

    public void addHeaderView(View view) {
        if (mListView == null) return;
        if (mAddHeaderViewList == null) {
            mAddHeaderViewList = new ArrayList<>();
        }
        mListView.addHeaderView(view);

        mAddHeaderViewList.add(view);
    }

    public void removeHeaderView(View view) {
        if (mListView == null || mAddHeaderViewList == null || !mAddHeaderViewList.contains(view)) {
            return;
        }
        mListView.removeHeaderView(view);

        mAddHeaderViewList.remove(view);
    }

    public int getHeaderViewsCount() {
        if (mListView == null) return 0;
        return mListView.getHeaderViewsCount();
    }

    public ArrayList<View> getHeaderViews() {
        return mAddHeaderViewList;
    }

    /**
     * @return IndexListView的ListView
     */
    public ListView getListView() {
        return mListView;
    }

    /**
     * @return IndexListView中心位置的 悬浮TextView
     */
    public TextView getCenterOverlayTextView() {
        return mTvOverlay;
    }

    public IndexBar getIndexBar() {
        return mIndexBar;
    }

    /**
     * @param show 是否显示IndexListView右侧位置的 悬浮TextView
     */
    public void showRightOverlayView(boolean show, int color) {
        if (show) {
            if (mTvRightOverlay == null) {
                initRightOverlayTextView(color);
                addView(mTvRightOverlay);
                mTvRightOverlay.invalidate();
                mIndexBar.showTouchOverlayView(mTvRightOverlay);
            }
        } else {
            if (mTvRightOverlay != null) {
                removeView(mTvRightOverlay);
                mIndexBar.showTouchOverlayView(null);
            }
        }
    }

    /**
     * @param show 是否显示IndexListView中心位置的 悬浮TextView
     */
    public void showCenterOverlayView(boolean show) {
        if (show) {
            if (mTvOverlay == null) {
                initOverlayTextView();
            }
            addView(mTvOverlay);
            mIndexBar.setOverlayView(mTvOverlay);
        } else {
            if (mTvOverlay != null) {
                removeView(mTvOverlay);
            }
            mIndexBar.setOverlayView(null);
        }
    }

    private void initOverlayTextView() {
        mTvOverlay = new TextView(mContext);
        mTvOverlay.setBackgroundResource(R.drawable.bg_translucent_4dp);
        mTvOverlay.setTextColor(Color.WHITE);
        mTvOverlay.setTextSize(40);
        mTvOverlay.setGravity(Gravity.CENTER);
        int size = IndexBar.dp2px(mContext, 70);
        LayoutParams params = new LayoutParams(size, size);
        params.gravity = Gravity.CENTER;
        mTvOverlay.setLayoutParams(params);
        mTvOverlay.setVisibility(INVISIBLE);
    }

    private void initRightOverlayTextView(int color) {
        mTvRightOverlay = new AppCompatTextView(mContext);
        mTvRightOverlay.setBackgroundResource(R.drawable.bg_right_overlay);
        mTvRightOverlay.setSupportBackgroundTintList(ColorStateList.valueOf(color));
        mTvRightOverlay.setTextColor(Color.WHITE);
        mTvRightOverlay.setTextSize(38);
        mTvRightOverlay.setGravity(Gravity.CENTER);
        int size = IndexBar.dp2px(mContext, 72);
        LayoutParams params = new LayoutParams(size, size);
        params.rightMargin = IndexBar.dp2px(mContext, 33);
        params.gravity = Gravity.RIGHT;
        mTvRightOverlay.setLayoutParams(params);
        mTvRightOverlay.setVisibility(INVISIBLE);
    }

    private static class BindDatasHanlder extends Handler {
        private final WeakReference<IndexableStickyListView> mIndexListView;

        public BindDatasHanlder(Looper looper, IndexableStickyListView indexListView) {
            super(looper);
            mIndexListView = new WeakReference<>(indexListView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final IndexableStickyListView indexListView = mIndexListView.get();

            indexListView.mAdapter.setNeedShutdown(false);
            indexListView.mAdapter.setDatas(indexListView.mItems, indexListView.mHeaderEntities);
            if (indexListView.mAdapter.isNeedShutdown()) return;
            ((Activity) indexListView.mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    indexListView.updateListView();
                }
            });
        }
    }

    /**
     * 绑定数据
     *
     * @param items          继承IndexEntity的List
     * @param headerEntities IndexListView自定义Header 如添加定位,热门城市等
     * @param <T>            继承IndexEntity
     */
    public <T extends IndexEntity> void bindDatas(final List<T> items, final IndexHeaderEntity... headerEntities) {
        mItems = new ArrayList<>();
        mHeaderEntities = new IndexHeaderEntity[headerEntities.length];

        mItems.addAll(items);
        for (int i = 0; i < headerEntities.length; i++) {
            mHeaderEntities[i] = headerEntities[i];
        }

        if (mAdapter == null) {
            return;
        }

        mProgressBar.setVisibility(VISIBLE);

        if (mContext instanceof Activity) {
            mAdapter.setNeedShutdown(true);

            if (mBindDataHandlerThread == null) {
                mBindDataHandlerThread = new HandlerThread("BindData_Thread");
                mBindDataHandlerThread.start();
                mBindDataHandler = new BindDatasHanlder(mBindDataHandlerThread.getLooper(), this);
            }
            mBindDataHandler.sendEmptyMessage(MSG_BIND_DATA);
        } else {
            mAdapter.setDatas(mItems, headerEntities);
            updateListView();
        }
    }


    /**
     * 搜索过滤处理
     *
     * @param newText 变化后的内容
     */
    public void searchTextChange(final String newText) {
        mIndexBar.searchTextChange(newText);
    }

    /**
     * 为IndexListView设置Adapter
     */
    public <T extends IndexEntity> void setAdapter(IndexableAdapter<T> adapter) {
        mAdapter = adapter;
        mAdapter.setParent(this);
        mListView.setAdapter(adapter);

        if (mItems != null) {
            bindDatas(mItems, mHeaderEntities);
        }
    }

    /**
     * 设置 IndexListView 标题头 部分的点击事件
     *
     * @param listener
     */
    public void setOnItemTitleClickListener(OnItemTitleClickListener listener) {
        mOnTitleListener = listener;
    }

    /**
     * 设置 IndexListView 内容item 部分的点击事件
     *
     * @param listener
     */
    public void setOnItemContentClickListener(OnItemContentClickListener listener) {
        mOnContentListener = listener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position < mListView.getHeaderViewsCount()) return;
        Object object = mAdapter.getItem(position - mListView.getHeaderViewsCount());
        if (mOnTitleListener != null && object instanceof String) {
            String title = (String) object;
            mOnTitleListener.onItemClick(view, title);
        } else if (mOnContentListener != null && object instanceof IndexEntity) {
            IndexEntity indexEntity = (IndexEntity) object;
            mOnContentListener.onItemClick(view, indexEntity);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mIndexBar.onListViewScrollStateChanged(scrollState);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mIndexBar.onListViewScroll(firstVisibleItem);

        if (mTitleHeight == 0 || mTitleMap == null) return;

        if (firstVisibleItem < mListView.getHeaderViewsCount()) {
            if (mStickView.getVisibility() == VISIBLE) {
                mStickView.setVisibility(INVISIBLE);
            }
            return;
        } else if (firstVisibleItem == mListView.getHeaderViewsCount()) {
            if (mStickView.getVisibility() != VISIBLE) {
                if (mAdapter == null || !mAdapter.isFilter()) {
                    mStickView.setVisibility(VISIBLE);
                }
            }
        }

        if (firstVisibleItem > mCurrentScrollItemPosition) {    // 向下
            mCurrentScrollItemPosition = firstVisibleItem;
            processStick(firstVisibleItem, totalItemCount);
        } else if (firstVisibleItem < mCurrentScrollItemPosition) { // 向上
            mCurrentScrollItemPosition = firstVisibleItem;
            processStick(firstVisibleItem, totalItemCount);
        } else {
            View firstView = mListView.getChildAt(0);
            if (firstView == null) return;

            int top = firstView.getTop();
            if (top < mCurrentScrollItemTop) {    // 向下
                mCurrentScrollItemTop = top;
                processStick(firstVisibleItem, totalItemCount);
            } else {  // 向上
                mCurrentScrollItemTop = top;
                processStick(firstVisibleItem, totalItemCount);
            }
        }
    }

    private void processStick(int firstVisibleItem, int totalItemCount) {
        if (firstVisibleItem < totalItemCount - 1 && mTitleMap.get(firstVisibleItem - mListView.getHeaderViewsCount() + 1) != null) {
            int nextTop = mListView.getChildAt(1).getTop();

            if (nextTop <= mTitleHeight) {
                if (mStickView.getVisibility() != VISIBLE) {
                    if (mAdapter == null || !mAdapter.isFilter()) {
                        mStickView.setVisibility(VISIBLE);
                    }
                }
                mStickView.setTranslationY(nextTop - mTitleHeight);
            }
        }
    }

    public interface OnItemContentClickListener {
        void onItemClick(View v, IndexEntity indexEntity);
    }

    public interface OnItemTitleClickListener {
        void onItemClick(View v, String title);
    }

    /**
     * 更新相关View数据
     */
    private void updateListView() {
        mListView.post(new Runnable() {
            @Override
            public void run() {
                final TextView titleTextView = mAdapter.getTitleTextView();
                titleTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        mTitleHeight = titleTextView.getHeight();
                    }
                });
            }
        });

        mTitleMap = mAdapter.getTitleMap();
        if (mStickView == null) {
            if (mTitleMap.size() > 0) {
                View view = mAdapter.getView(mTitleMap.keyAt(0), null, mListView);
                mStickView = (TextView) view;
                addView(mStickView, 1);

                mStickView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnTitleListener != null) {
                            mOnTitleListener.onItemClick(v, mStickView.getText().toString());
                        }
                    }
                });

                if (mListView.getHeaderViewsCount() > 0) {
                    mStickView.setVisibility(INVISIBLE);
                }
            }

            mIndexBar.setOnSearchResultListener(new IndexBar.OnSearchResultListener() {
                @Override
                public void onStart() {
                    if (!mSearchLayout.isProgressVisible()) {
                        if (mContext instanceof Activity) {
                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mSearchLayout.showProgress();
                                }
                            });
                        }
                    }
                }

                @Override
                public void onResult(boolean isSearch, int dataSize) {
                    if (mAdapter == null) return;

                    if (!isSearch || dataSize > 0) {
                        mSearchLayout.hide();
                    } else {
                        mSearchLayout.showTip();
                    }

                    mListView.setSelection(1);
                    if (mAdapter.isFilter()) {
                        if (mAddHeaderViewList != null) {
                            for (View view : mAddHeaderViewList) {
                                if (view.getHeight() != 0) {
                                    view.setTag(view.getHeight());
                                    view.getLayoutParams().height = 1;
                                }
                            }
                        }
                        if (mStickView != null && mStickView.getVisibility() == VISIBLE) {
                            mStickView.setVisibility(INVISIBLE);
                        }
                    } else {
                        if (mAddHeaderViewList != null) {
                            for (View view : mAddHeaderViewList) {
                                view.getLayoutParams().height = (int) view.getTag();
                            }
                        }
                        if (mStickView != null && mStickView.getVisibility() != VISIBLE) {
                            mStickView.setVisibility(VISIBLE);
                        }
                    }
                    mListView.smoothScrollToPosition(0);
                }
            });
        } else {
            if (mTitleMap.size() == 0) {
                removeView(mStickView);
                mStickView = null;
            } else {
                String title = mAdapter.getItemTitle(mListView.getFirstVisiblePosition());
                mStickView.setText(title);
            }
        }

        mProgressBar.setVisibility(GONE);

        mAdapter.notifyDataSetChanged();
        mIndexBar.setListView(mListView);
        mIndexBar.postInvalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mBindDataHandlerThread != null) {
            mBindDataHandlerThread.quit();
        }
        super.onDetachedFromWindow();
    }
}
