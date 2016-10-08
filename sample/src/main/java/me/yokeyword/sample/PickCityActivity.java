package me.yokeyword.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.yokeyword.indexablerv.IndexableAdapter;
import me.yokeyword.indexablerv.IndexableLayout;

/**
 * 选择城市
 * Created by YoKey on 16/10/7.
 */
public class PickCityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_city);

        IndexableLayout indexableLayout = (IndexableLayout) findViewById(R.id.indexableLayout);
        indexableLayout.setAdapter(adapter);
        adapter.setDatas(initDatas());

        indexableLayout.setOnItemContentClickListener(new IndexableLayout.OnItemContentClickListener<CityEntity>() {
            @Override
            public void onItemClick(View v, int originalPosition, int currentPosition, CityEntity entity) {
                Toast.makeText(PickCityActivity.this, "选中:" + entity.getName() + "  当前位置:" + currentPosition + "  原始所在数组位置:" + originalPosition, Toast.LENGTH_SHORT).show();
            }
        });

        indexableLayout.setOnItemIndexClickListener(new IndexableLayout.OnItemIndexClickListener() {
            @Override
            public void onItemClick(View v, int currentPosition, String indexName) {
                Toast.makeText(PickCityActivity.this, "选中:" + indexName + "  当前位置:" + currentPosition, Toast.LENGTH_SHORT).show();
            }
        });

        indexableLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.setDatas(initDatas());
            }
        }, 4000);

        indexableLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.setDatas(initDatas());
            }
        }, 4001);

        indexableLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.setDatas(initDatas());
            }
        }, 4002);


        indexableLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.setDatas(initDatas());
            }
        }, 4003);

        indexableLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.setDatas(initDatas());
            }
        }, 4004);
    }

    private IndexableAdapter<CityEntity> adapter = new IndexableAdapter<CityEntity>() {
        @Override
        public String getIndexName(CityEntity data) {
            return data.getName();
        }

        @Override
        public void setIndexName(CityEntity data, String indexName) {
            data.setName(indexName);
        }

        @Override
        public RecyclerView.ViewHolder onCreateIndexView(ViewGroup parent) {
            View view = LayoutInflater.from(PickCityActivity.this).inflate(R.layout.item_index_city, parent, false);
            return new IndexVH(view);
        }

        @Override
        public RecyclerView.ViewHolder onCreateContentView(ViewGroup parent) {
            View view = LayoutInflater.from(PickCityActivity.this).inflate(R.layout.item_city, parent, false);
            return new ContentVH(view);
        }

        @Override
        public void onBindIndexViewHolder(RecyclerView.ViewHolder holder, String indexName) {
            IndexVH vh = (IndexVH) holder;
            vh.tv.setText(indexName);
        }

        @Override
        public void onBindContentViewHolder(RecyclerView.ViewHolder holder, CityEntity entity) {
            ContentVH vh = (ContentVH) holder;
            vh.tv.setText(entity.getName());
        }

        class IndexVH extends RecyclerView.ViewHolder {
            TextView tv;

            public IndexVH(View itemView) {
                super(itemView);
                tv = (TextView) itemView.findViewById(R.id.tv_index);
            }
        }

        class ContentVH extends RecyclerView.ViewHolder {
            TextView tv;

            public ContentVH(View itemView) {
                super(itemView);
                tv = (TextView) itemView.findViewById(R.id.tv_name);
            }
        }
    };

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
}
