package com.yokeyword.indexablelistview;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * Created by YoKeyword on 16/3/25.
 */
public class SearchLayout extends FrameLayout {
    private ProgressBar mSearchProgressBar;
    private TextView mTvTip;

    public SearchLayout(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(Color.WHITE);

        mSearchProgressBar = new ProgressBar(context);
        int searchSize = IndexBar.dp2px(context, 20);
        LayoutParams paramsSerach = new LayoutParams(searchSize, searchSize);
        paramsSerach.gravity = Gravity.CENTER_HORIZONTAL;
        paramsSerach.topMargin = searchSize;

        mTvTip = new TextView(context);
        LayoutParams paramsTip = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        paramsTip.gravity = Gravity.CENTER_HORIZONTAL;
        paramsTip.topMargin = searchSize;
        mTvTip.setText(R.string.no_results);
        mTvTip.setTextSize(12f);
        mTvTip.setTextColor(Color.GRAY);

        addView(mTvTip, paramsTip);
        addView(mSearchProgressBar, paramsSerach);

        setVisibility(GONE);
    }

    void showProgress() {
        if (getVisibility() != VISIBLE) {
            setVisibility(VISIBLE);
        }
        if (mSearchProgressBar.getVisibility() != VISIBLE) {
            mSearchProgressBar.setVisibility(VISIBLE);
        }
        if (mTvTip.getVisibility() == VISIBLE) {
            mTvTip.setVisibility(INVISIBLE);
        }
    }

    void showTip() {
        if (getVisibility() != VISIBLE) {
            setVisibility(VISIBLE);
        }

        if (mSearchProgressBar.getVisibility() == VISIBLE) {
            mSearchProgressBar.setVisibility(INVISIBLE);
        }

        if (mTvTip.getVisibility() != VISIBLE) {
            mTvTip.setVisibility(VISIBLE);
        }
    }

    void hide() {
        if (getVisibility() == VISIBLE) {
            setVisibility(GONE);
        }
    }

    boolean isProgressVisible() {
        if (getVisibility() != VISIBLE) {
            return false;
        }
        return mSearchProgressBar.getVisibility() == VISIBLE;
    }
}
