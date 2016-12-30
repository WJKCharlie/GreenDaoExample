package com.wjk.greendaoexample.datamodel;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by JKWANG-PC on 2016/12/30.
 */

@Entity
public class Product {
    @Id
    private String Id;
    private String Name;
    @Generated(hash = 536689391)
    public Product(String Id, String Name) {
        this.Id = Id;
        this.Name = Name;
    }
    @Generated(hash = 1890278724)
    public Product() {
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
}
