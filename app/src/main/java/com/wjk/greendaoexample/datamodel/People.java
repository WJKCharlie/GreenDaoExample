package com.wjk.greendaoexample.datamodel;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by JKWANG-PC on 2016/12/30.
 */
@Entity
public class People {
    @Id
    private String Id;
    private String Name;
    private String Sex;
    @Generated(hash = 89649623)
    public People(String Id, String Name, String Sex) {
        this.Id = Id;
        this.Name = Name;
        this.Sex = Sex;
    }
    @Generated(hash = 1406030881)
    public People() {
    }
    public String getId() {
        return this.Id;
    }
    public void setId(String Id) {
        this.Id = Id;
    }
    public String getName() {
        return this.Name;
    }
    public void setName(String Name) {
        this.Name = Name;
    }
    public String getSex() {
        return this.Sex;
    }
    public void setSex(String Sex) {
        this.Sex = Sex;
    }
}
