package com.yokeyword.sample.city;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.yokeyword.indexablelistview.IndexEntity;
import com.yokeyword.indexablelistview.IndexHeaderEntity;
import com.yokeyword.indexablelistview.IndexableStickyListView;
import com.yokeyword.sample.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 选择城市
 * Created by YoKeyword on 2016/3/20.
 */
public class PickCityActivity extends AppCompatActivity {
    public static final String TAG = PickCityActivity.class.getSimpleName();

    private static final String EXTRA_LARGE = "extra_large";

    private IndexableStickyListView mIndexableStickyListView;
    private SearchView mSearchView;

    private CityAdapter mAdapter;
    private List<CityEntity> mCities = new ArrayList<>();

    private String[] mHotCities = new String[]{"杭州市", "北京市", "上海市", "广州市"};

    public static Intent getCallingIntent(Context context, boolean isLarge) {
        Intent intent = new Intent(context, PickCityActivity.class);
        intent.putExtra(EXTRA_LARGE, isLarge);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_city);

        initView();
    }

    protected void initView() {
        setTitle("选择城市");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mIndexableStickyListView = (IndexableStickyListView) findViewById(R.id.indexListView);
        mSearchView = (SearchView) findViewById(R.id.searchview);

        mAdapter = new CityAdapter(this);
        mIndexableStickyListView.setAdapter(mAdapter);

        // 初始化数据
        List<String> cityStrings = Arrays.asList(getResources().getStringArray(R.array.city_array));
        for (String item : cityStrings) {
            CityEntity cityEntity = new CityEntity();
            cityEntity.setName(item);
            mCities.add(cityEntity);
        }

        if (getIntent().getBooleanExtra(EXTRA_LARGE, false)) {
            mCities.addAll(mCities);
            mCities.addAll(mCities);
            mCities.addAll(mCities);
            mCities.addAll(mCities);
            mCities.addAll(mCities);
            mCities.addAll(mCities);
            mCities.addAll(mCities);
        }

        // 添加定位城市Header
        ArrayList<CityEntity> gpsIndexEntityList = new ArrayList<>();
        final CityEntity gpsEntity = new CityEntity();
        gpsEntity.setName("定位中...");
        gpsIndexEntityList.add(gpsEntity);
        IndexHeaderEntity<CityEntity> gpsHeader = new IndexHeaderEntity<>("定", "GPS自动定位", gpsIndexEntityList);

        // 添加热门城市Header
        IndexHeaderEntity<CityEntity> hotHeader = new IndexHeaderEntity<>();
        ArrayList<CityEntity> hotIndexEntityList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            CityEntity hotEntity = new CityEntity();
            hotEntity.setName(mHotCities[i]);
            hotIndexEntityList.add(hotEntity);
        }

        hotHeader.setHeaderTitle("热门城市");
        hotHeader.setIndex("热");
        hotHeader.setHeaderList(hotIndexEntityList);

        // 绑定数据
        mIndexableStickyListView.bindDatas(mCities, gpsHeader, hotHeader);

        // 搜索
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 委托处理搜索
                mIndexableStickyListView.searchTextChange(newText);
                return true;
            }
        });
        mIndexableStickyListView.setOnItemContentClickListener(new IndexableStickyListView.OnItemContentClickListener() {
            @Override
            public void onItemClick(View v, IndexEntity indexEntity) {
                Toast.makeText(PickCityActivity.this, "选择了" + indexEntity.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        mIndexableStickyListView.setOnItemTitleClickListener(new IndexableStickyListView.OnItemTitleClickListener() {
            @Override
            public void onItemClick(View v, String title) {
                Toast.makeText(PickCityActivity.this, "点击了" + title, Toast.LENGTH_SHORT).show();
            }
        });

        // 模拟定位
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                gpsEntity.setName("杭州市");
                mAdapter.notifyDataSetChanged();
            }
        }, 3000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
