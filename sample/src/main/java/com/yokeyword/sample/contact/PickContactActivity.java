package com.yokeyword.sample.contact;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.yokeyword.indexablelistview.IndexEntity;
import com.yokeyword.indexablelistview.IndexableStickyListView;
import com.yokeyword.sample.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by YoKeyword on 16/3/24.
 */
public class PickContactActivity extends AppCompatActivity {
    private SearchView mSearchView;
    private IndexableStickyListView mIndexableStickyListView;

    private List<ContactEntity> mContacts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_contact);

        initView();
    }

    private void initView() {
        setTitle("选择联系人");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mSearchView = (SearchView) findViewById(R.id.searchview);
        mIndexableStickyListView = (IndexableStickyListView) findViewById(R.id.indexListView);

        View headerView = getLayoutInflater().inflate(R.layout.header_view, null);
        mIndexableStickyListView.addHeaderView(headerView);

        ContactAdapter adapter = new ContactAdapter(this);
        mIndexableStickyListView.setAdapter(adapter);

        // 初始化数据
        List<String> contactStrings = Arrays.asList(getResources().getStringArray(R.array.contact_array));
        List<String> mobileStrings = Arrays.asList(getResources().getStringArray(R.array.mobile_array));
        for (int i = 0; i < contactStrings.size(); i++) {
            ContactEntity contactEntity = new ContactEntity(contactStrings.get(i), mobileStrings.get(i));
            mContacts.add(contactEntity);
        }

        mIndexableStickyListView.bindDatas(mContacts);

        mIndexableStickyListView.setOnItemContentClickListener(new IndexableStickyListView.OnItemContentClickListener() {
            @Override
            public void onItemClick(View v, IndexEntity indexEntity) {
                ContactEntity contactEntity = (ContactEntity) indexEntity;
                Toast.makeText(PickContactActivity.this, "选择了" + contactEntity.getName() + " 手机号:" + contactEntity.getMobile(), Toast.LENGTH_SHORT).show();
            }
        });

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mIndexableStickyListView.searchTextChange(newText);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
