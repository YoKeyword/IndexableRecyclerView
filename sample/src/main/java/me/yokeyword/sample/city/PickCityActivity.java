package me.yokeyword.sample.city;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.yokeyword.indexablerv.SimpleHeaderAdapter;
import me.yokeyword.indexablerv.IndexableAdapter;
import me.yokeyword.indexablerv.IndexableLayout;
import me.yokeyword.sample.R;

/**
 * 选择城市
 * Created by YoKey on 16/10/7.
 */
public class PickCityActivity extends AppCompatActivity {
    private CityAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_city);
        getSupportActionBar().setTitle("选择城市");
        IndexableLayout indexableLayout = (IndexableLayout) findViewById(R.id.indexableLayout);

        // setAdapter
        mAdapter = new CityAdapter(this);
        indexableLayout.setAdapter(mAdapter);
        // set Datas
        mAdapter.setDatas(initDatas());

        // set Listener
        mAdapter.setOnItemContentClickListener(new IndexableAdapter.OnItemContentClickListener<CityEntity>() {
            @Override
            public void onItemClick(View v, int originalPosition, int currentPosition, CityEntity entity) {
                Toast.makeText(PickCityActivity.this, "选中:" + entity.getName() + "  当前位置:" + currentPosition + "  原始所在数组位置:" + originalPosition, Toast.LENGTH_SHORT).show();
            }
        });

        mAdapter.setOnItemTitleClickListener(new IndexableAdapter.OnItemTitleClickListener() {
            @Override
            public void onItemClick(View v, int currentPosition, String indexTitle) {
                Toast.makeText(PickCityActivity.this, "选中:" + indexTitle + "  当前位置:" + currentPosition, Toast.LENGTH_SHORT).show();
            }
        });

        // 添加 HeaderView DefaultHeaderAdapter接收一个IndexableAdapter, 使其布局以及点击事件和IndexableAdapter一致
        // 如果想自定义布局,点击事件, 可传入 new IndexableHeaderAdapter
        indexableLayout.addHeaderAdapter(new SimpleHeaderAdapter<>(mAdapter, "热", "热门城市", iniyHotCityDatas()));
        indexableLayout.addHeaderAdapter(new SimpleHeaderAdapter<>(mAdapter, "定", "当前城市", iniyGPSCityDatas()));
    }

    private List<CityEntity> initDatas() {
        List<CityEntity> list = new ArrayList<>();
        List<String> cityStrings = Arrays.asList(getResources().getStringArray(R.array.city_array));
        for (String item : cityStrings) {
            CityEntity cityEntity = new CityEntity();
            cityEntity.setName(item);
            list.add(cityEntity);
        }
        return list;
    }

    private List<CityEntity> iniyHotCityDatas() {
        List<CityEntity> list = new ArrayList<>();
        list.add(new CityEntity("杭州市"));
        list.add(new CityEntity("北京市"));
        list.add(new CityEntity("上海市"));
        list.add(new CityEntity("广州市"));
        return list;
    }

    private List<CityEntity> iniyGPSCityDatas() {
        List<CityEntity> list = new ArrayList<>();
        list.add(new CityEntity("杭州市"));
        return list;
    }
}
