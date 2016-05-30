package me.yokeyword.indexablelistview;

/**
 * Created by YoKeyword on 16/3/20.
 */
public class IndexEntity {
    private String name;
    // 库生成 不用传值
    private String firstSpell;
    // 库生成 不用传值
    private String spell;

    public IndexEntity() {
    }

    public IndexEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstSpell() {
        return firstSpell;
    }

    void setFirstSpell(String firstSpell) {
        this.firstSpell = firstSpell;
    }

    String getSpell() {
        return spell;
    }

    void setSpell(String spell) {
        this.spell = spell;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IndexEntity entity = (IndexEntity) o;

        if (!name.equals(entity.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
