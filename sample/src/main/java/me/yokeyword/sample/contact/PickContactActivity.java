package me.yokeyword.sample.contact;

import android.graphics.Color;
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
import me.yokeyword.indexablerv.IndexableHeaderAdapter;
import me.yokeyword.indexablerv.IndexableLayout;
import me.yokeyword.sample.R;

/**
 * Created by YoKey on 16/10/8.
 */
public class PickContactActivity extends AppCompatActivity {
    private ContactAdapter mAdapter;
    private HeaderAdapter mHeaderAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_contact);
        getSupportActionBar().setTitle("联系人");
        IndexableLayout indexableLayout = (IndexableLayout) findViewById(R.id.indexableLayout);

        // setAdapter
        mAdapter = new ContactAdapter(this);
        indexableLayout.setAdapter(mAdapter);
        // set Datas
        mAdapter.setDatas(initDatas());
        // set Material Design OverlayView
        indexableLayout.setOverlayStyle_MaterialDesign(Color.RED);

        // set Listener
        mAdapter.setOnItemContentClickListener(new IndexableAdapter.OnItemContentClickListener<UserEntity>() {
            @Override
            public void onItemClick(View v, int originalPosition, int currentPosition, UserEntity entity) {
                Toast.makeText(PickContactActivity.this, "选中:" + entity.getNick() + "  当前位置:" + currentPosition + "  原始所在数组位置:" + originalPosition, Toast.LENGTH_SHORT).show();
            }
        });

        mAdapter.setOnItemIndexClickListener(new IndexableAdapter.OnItemIndexClickListener() {
            @Override
            public void onItemClick(View v, int currentPosition, String indexTitle) {
                Toast.makeText(PickContactActivity.this, "选中:" + indexTitle + "  当前位置:" + currentPosition, Toast.LENGTH_SHORT).show();
            }
        });

        mHeaderAdapter = new HeaderAdapter("↑", initMenuDatas());
        indexableLayout.addHeaderAdapter(mHeaderAdapter);

        mHeaderAdapter.setOnItemHeaderClickListener(new IndexableHeaderAdapter.OnItemHeaderClickListener<MenuEntity>() {
            @Override
            public void onItemClick(View v, int currentPosition, MenuEntity entity) {
                Toast.makeText(PickContactActivity.this, "Menu: " + entity.getMenuTitle() + "  当前位置: " + currentPosition, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 自定义的Header布局
     */
    class HeaderAdapter extends IndexableHeaderAdapter<MenuEntity> {
        private static final int TYPE = 1;

        public HeaderAdapter(String index, List<MenuEntity> datas) {
            super(index, datas);
        }

        public HeaderAdapter(String index, String indexTitle, List<MenuEntity> datas) {
            super(index, indexTitle, datas);
        }

        @Override
        public int getItemViewType() {
            return TYPE;
        }

        @Override
        public RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent) {
            return new VH(LayoutInflater.from(PickContactActivity.this).inflate(R.layout.item_contact_header, parent, false));
        }

        @Override
        public void onBindContentViewHolder(RecyclerView.ViewHolder holder, MenuEntity entity) {
            VH vh = (VH) holder;
            vh.tv.setText(entity.getMenuTitle());
        }

        private class VH extends RecyclerView.ViewHolder {
            private TextView tv;

            public VH(View itemView) {
                super(itemView);
                tv = (TextView) itemView.findViewById(R.id.tv_title);
            }
        }
    }

    private List<UserEntity> initDatas() {
        List<UserEntity> list = new ArrayList<>();
        // 初始化数据
        List<String> contactStrings = Arrays.asList(getResources().getStringArray(R.array.contact_array));
        List<String> mobileStrings = Arrays.asList(getResources().getStringArray(R.array.mobile_array));
        for (int i = 0; i < contactStrings.size(); i++) {
            UserEntity contactEntity = new UserEntity(contactStrings.get(i), mobileStrings.get(i));
            list.add(contactEntity);
        }
        return list;
    }

    private List<MenuEntity> initMenuDatas() {
        List<MenuEntity> list = new ArrayList<>();
        list.add(new MenuEntity("新的朋友"));
        list.add(new MenuEntity("群聊"));
        list.add(new MenuEntity("标签"));
        list.add(new MenuEntity("公众号"));
        return list;
    }
}
