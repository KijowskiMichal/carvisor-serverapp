package com.inz.carvisor.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity
public class Setting {

    @Id
    @GeneratedValue
    int id;

    String nameOfSetting;
    Integer value;

    public Setting() {
        super();
    }

    public Setting(String nameOfSetting, Integer value) {
        this.nameOfSetting = nameOfSetting;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameOfSetting() {
        return nameOfSetting;
    }

    public void setNameOfSetting(String nameOfSetting) {
        this.nameOfSetting = nameOfSetting;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Setting{" +
                "id=" + id +
                ", nameOfSetting='" + nameOfSetting + '\'' +
                ", value=" + value +
                '}';
    }
}
