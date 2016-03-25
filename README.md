# IndexableStickyListView
IndexableListView + Sticky ; Supporting Chinese and English.

**非常简单的实现：选择城市、搜索城市 ， 选择联系人、搜索联系人功能 等需要字母索引的功能**

# Demo演示
<img src="/gif/demo_city.gif" width="320px"/>
<img src="/gif/demo_contact.gif" width="320px"/>

# 特性
1、根据数据源，自动绘制字母索引Bar，以及字母Header

2、字母Header是粘性的（Sticky）

3、ListView中的Item，可以完全自由定制、拓展

4、一句代码实现搜索过滤功能，搜索框自己定制

5、绑定数据源、搜索等功能，都是异步的，通过HandlerThread优化实现

6、提供2种悬浮提示View，常规居中 以及 MD风格的右侧气泡

# 如何使用
### gradle
项目下app的build.gradle中依赖：
````xml
compile 'me.yokeyword:indexablestickylistview:0.1.0'
````

### 使用
````xml
<com.yokeyword.indexablelistview.IndexableStickyListView
    android:id="@+id/listView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:indexListView_type_overlay="right"                    // 悬浮提示类型： 居中 or 右侧跟随手指
    app:indexListView_rightOverlayColor="@color/colorAccent"  // 悬浮类型为右侧时，设置气泡的颜色
    app:indexBar_textSize="13sp"                              // 字母索引Bar：字体大小
    app:indexBar_selected_textColor="@color/colorAccent"     // 字母索引Bar：选中状态下的字体颜色
    app:indexBar_textColor="@android:color/darker_gray"/>    // 字母索引Bar：正常状态下的字体颜色
````

下面以选择城市功能为例：

1、实体类继承IndexEntity（IndexEntity里有个name属性以及自动生成的首字母fistSpell属性，如果属性够用，可直接使用该实体类）
````java
public class CityEntity extends IndexEntity {
}
````

2、Adapter继承IndexAdapter<T extends IndexEntity>
````java
public class CityAdapter extends IndexBarAdapter<CityEntity> {
    private Context mContext;

    public CityAdapter(Context context) {
        mContext = context;
    }

    @Override
    protected TextView onCreateTitleViewHolder(ViewGroup parent) {
        // 创建 Sticky字母的Header布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_tv_title_city, parent, false);
        return (TextView) view.findViewById(R.id.tv_title);
    }

    @Override
    protected ViewHolder onCreateViewHolder(ViewGroup parent) {
        // 创建 Item布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_city, parent, false);
        return new CityViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, CityEntity cityEntity) {
        // 为Item布局绑定数据
        CityViewHolder cityViewHolder = (CityViewHolder) holder;
        cityViewHolder.tvCity.setText(cityEntity.getName());
    }

    // ViewHolder需要继承IndexBarAdapter.ViewHolder
    class CityViewHolder extends IndexBarAdapter.ViewHolder {
        TextView tvCity;
        public CityViewHolder(View view) {
            super(view);
            tvCity = (TextView) view.findViewById(R.id.tv_name);
        }
    }
}
````

3、在Activity中的onCreate或者 Fragment中的onCreateView中绑定：
````java
CityAdapter mAdapter = new CityAdapter(this);
mIndexableStickyListView.setAdapter(mAdapter);

List<CityEntity> mCities;
...此处初始化mCities数据
// 绑定数据
mIndexableStickyListView.bindDatas(mCities);

// item点击事件监听
mIndexableStickyListView.setOnItemContentClickListener(new IndexableStickyListView.OnItemContentClickListener() {
  @Override
  public void onItemClick(View v, IndexEntity indexEntity) {
    CityEntity item = (CityEntity)indexEntity;
    ...
  }
});
````

4、搜索：在SearchView的setOnQueryTextListener或者EditTexit的addTextChangedListener中TextChange系列的方法中：
````java
// 委托处理搜索
mIndexableStickyListView.searchTextChange(newText);
````

5、拓展：

添加 数据源HeaderEntity:("选择城市"图中的 GPS、热门城市)
````java
// 添加定位城市Header
ArrayList<CityEntity> gpsIndexEntityList = new ArrayList<>();
CityEntity gpsEntity = new CityEntity();
gpsEntity.setName("定位中...");
gpsIndexEntityList.add(gpsEntity);
IndexHeaderEntity<CityEntity> gpsHeader = new IndexHeaderEntity<>("定", "GPS自动定位", gpsIndexEntityList);
// 绑定数据 bindDatas第二个参数是个IndexHeaderEntity的数组,可以添加任意多HeaderEntity
mIndexableStickyListView.bindDatas(mCities,gpsHeader);
````

为ListView添加HeaderView: (“选择联系人”图中的Header)
````java
mIndexableStickyListView.addHeaderView(headerView);
````

