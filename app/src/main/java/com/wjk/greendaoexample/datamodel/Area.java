package com.wjk.greendaoexample.datamodel;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by JKWANG-PC on 2016/12/29.
 */

@Entity
public class Area {
    @Id
    private String AreaCode;
    private String AreaName;
    @Generated(hash = 262290694)
    public Area(String AreaCode, String AreaName) {
        this.AreaCode = AreaCode;
        this.AreaName = AreaName;
    }
    @Generated(hash = 179626505)
    public Area() {
    }
    public String getAreaCode() {
        return this.AreaCode;
    }
    public void setAreaCode(String AreaCode) {
        this.AreaCode = AreaCode;
    }
    public String getAreaName() {
        return this.AreaName;
    }
    public void setAreaName(String AreaName) {
        this.AreaName = AreaName;
    }
}
