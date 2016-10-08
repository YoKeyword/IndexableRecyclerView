package me.yokeyword.sample.city;

/**
 * Created by YoKey on 16/10/7.
 */
public class CityEntity {
    private long id;
    private String name;

    public CityEntity() {
    }

    public CityEntity(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
