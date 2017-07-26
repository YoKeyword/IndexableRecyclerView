# IndexableRecyclerView
A RecyclerView with indexable, sticky and many other features.

**轻松实现：选择城市，选择联系人等需要索引的功能**

> 替代之前的IndexableStickyListView(移至该[分支](https://github.com/YoKeyword/IndexableRecyclerView/tree/listview)),进行大幅度重构，性能优化，更易使用的API，更易拓展的HeaderView/FooterView等等！
重构历程可以看这篇文章：[［设计模式］记一次开源库的重构历程](http://www.jianshu.com/p/2ee8706c346b)

# Demo演示
<img src="/gif/demo_city.gif" width="320px"/>
<img src="/gif/demo_contact.gif" width="320px"/>

# 特性
1、根据数据源，自动**排序生成**字母索引Bar(非字母开头,索引为"#",另可自由定制)，以及HeaderTitle

2、非常自由的 添加各种HeaderView／FooterView，包括自定义索引，HeaderTitle，各种View等等

3、HeaderTitle是粘性的（Sticky）

4、UI自由定制、拓展；提供2种悬浮提示View，常规居中 以及 MD风格的右侧气泡

5、绑定数据源，通过单线程的线程池优化，不怕重复绑定数据

6、使用[TinyPinyin](https://github.com/promeG/TinyPinyin)代替Pinyin4j.jar库，体积更小，拼音转化速度提升4倍！

# 更新日志
1.3.0
* 多音字借助TinyPinyin处理
* 可以自定义排序方式

1.2.4
* Fix 数据变动时，StickTitle不及时更新问题；增加2处安全校验

1.2.0
* 支持GridLayoutManager! (感谢[guodongAndroid](https://github.com/guodongAndroid))

1.0.7
* 默认不再显示左侧的悬浮气泡
* 默认排序方式改为快速排序，提供一个MODE_NONE的排序方式

1.0.5
为HeaderView/FooterView添加:
* `indexableLayout.removeHeaderAdapter();`
* `headerAdapter.addData()`
* `headerAdapter.removeData()`

# 如何使用
### gradle
项目下app的build.gradle中依赖：
````xml
 compile 'me.yokeyword:indexablerecyclerview:1.3.0'
 compile 'com.android.support:appcompat-v7:你使用的版本号'
 compile 'com.android.support:recyclerview-v7:你使用的版本号'
````

### Xml
````xml
 <me.yokeyword.indexablerv.IndexableLayout
     ...
     app:indexBar_layout_width="24dp"           // IndexBar：width
     app:indexBar_background="#08000000"        // IndexBar：background
     app:indexBar_textColor="#000000"           // IndexBar：textColor
     app:indexBar_selectedTextColor="#f33737"   // IndexBar：isSelected textColor
     app:indexBar_textSize="14sp"               // IndexBar：textSize
     app:indexBar_textSpace="6dp" />            // IndexBar：text lineSpace
````

### 3步集成
**1、实体类实现IndexableEntity**
````java
public class CityEntity implements IndexableEntity {
    ...
    private String name;
    private String pinyin;
    
    @Override
    public String getFieldIndexBy() {
        return name;  // return 你需要根据该属性排序的field
    }

    @Override
    public void setFieldIndexBy(String indexByField) {
        this.name = indexByField; // 同上
    }

    @Override
    public void setFieldPinyinIndexBy(String pinyin) {
        this.pinyin = pinyin; // 保存排序field的拼音,在执行比如搜索等功能时有用 （若不需要，空实现该方法即可）
    }
}
````

**2、继承IndexAdapter**
````java
public class CityAdapter extends IndexableAdapter<CityEntity> {
    @Override
    public RecyclerView.ViewHolder onCreateTitleViewHolder(ViewGroup parent) {
        // 创建 TitleItem 布局
    }

    @Override
    public RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent) {
        // 创建 内容Item 布局
    }

    @Override
    public void onBindTitleViewHolder(RecyclerView.ViewHolder holder, String indexTitle) {
         // 填充 TitleItem 布局
    }

    @Override
    public void onBindContentViewHolder(RecyclerView.ViewHolder holder, CityEntity entity) {
        // 填充 内容Item 布局
    }
}
````

**3、绑定视图和数据**
````java
// 支持LinearLayoutManager, GridLayoutManager
indexableLayout.setLayoutManager(LayoutManager);

CityAdapter adapter = new CityAdapter(this);
indexableLayout.setAdapter(adapter);
// 排序过程是异步的 另有setDatas(mDatas,IndexCallback callback)  // callback在datas异步排序结束后回调
adapter.setDatas(mDatas);
// 另有setOnItemTitleClickListener(listener)，点击TitleItem点击事件以及LongClick
adapter.setOnItemContentClickListener(listener);
````

# 拓展
### 1、设置 索引悬浮提示框 风格
````java
// 前者Material Design风格右侧气泡 ， 后者 居中 IOS风格气泡
indexableLayout.setOverlayStyle_MaterialDesign(int Color) & setOverlayStyle_Center()
````

### 2、多音字处理

多音字处理得益于使用了[TinyPinyin](https://github.com/promeG/TinyPinyin)，可以如下设置：
````java
// 添加中文城市词典
Pinyin.init(Pinyin.newConfig().with(CnCityDict.getInstance());

// 添加自定义词典
Pinyin.init(Pinyin.newConfig()
            .with(new PinyinMapDict() {
                @Override
                public Map<String, String[]> mapping() {
                    HashMap<String, String[]> map = new HashMap<String, String[]>();
                    map.put("重庆",  new String[]{"CHONG", "QING"});
                    return map;
                }
            }));
````

### 3、添加自定义HeaderView，FooterView
````java
indexableLayout.addHeaderAdapter(IndexableHeaderAdapter adapter)    // 添加HeaderView
indexableLayout.addFooterAdapter(IndexableFooterAdapter adapter)    // 添加FooterView

// 3个参数分别对应:IndexBar的索引,HeaderTitle,传入的Header数据源,此处泛型T可以是任何实体类,不需要和主Adapter类型一致
// 如果不想显示某块视图，则传入null即可： 比如不想显示 HeaderTitle， 则indexTitle传入null；
IndexableHeaderAdapter<T>(String index, String indexTitle, List<T> datas){
    // 需要实现3个方法：
    public abstract int getItemViewType(); // 每个HeaderView的type应当不同
    public abstract RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent);
    public abstract void onBindContentViewHolder(RecyclerView.ViewHolder holder, T entity);
}

// 如果想添加的HeaderView，和主Adapter的布局完全一致，则可以使用:
new SimpleHeaderAdapter(IndexableAdapter<T> adapter, String index, String indexTitle, List<T> datas);
````

### 4、更改排序规则
默认根据**全拼音排序**，可根据需求更改为**按首字母排序**:
````java
// 设置排序规则： 
// MODE_FAST:按首字母排序（默认）；MODE_ALL_LETTERS:全字母比较，效率较低； MODE_NONE:字母模块内不排序，效率最高
indexableLayout.setCompareMode(@CompareMode int mode);

// 自定义排序规则
indexableLayout.setComparator(yourComparator);
````

> 更多细节使用，参见Demo

## 致谢

[TinyPinyin](https://github.com/promeG/TinyPinyin)

