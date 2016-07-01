package me.yokeyword.indexablelistview;

/**
 * Created by YoKeyword on 16/3/20.
 */
public abstract class IndexEntity {
    // 库生成 不用传值
    private String firstSpell;
    // 库生成 不用传值
    private String spell;

    public IndexEntity() {
    }

    public abstract String getName();

    public abstract void setName(String name);

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

        if (!getName().equals(entity.getName())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}
