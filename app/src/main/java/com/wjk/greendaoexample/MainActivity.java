package com.wjk.greendaoexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.wjk.greendaoexample.datamodel.Area;
import com.wjk.greendaoexample.datamodel.DaoSession;
import com.wjk.greendaoexample.util.GreenDaoHelper;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView textview;
    private DaoSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textview=(TextView)findViewById(R.id.textview);

        session = GreenDaoHelper.getDaoSession(this);
        session.getAreaDao().deleteAll();//清空所有记录

        //添加记录
        Area area = new Area("01","北京");
        Area area1 = new Area("02","天津");
        session.getAreaDao().insert(area);
        session.getAreaDao().insert(area1);

        //查询记录
        StringBuilder stringBuilder = new StringBuilder();
        List<Area> areas = session.getAreaDao().loadAll();
        for (int i = 0,n = areas.size();i<n;++i){
            stringBuilder.append("地区编码：").append(areas.get(i).getAreaCode())
                    .append("，地区名称：").append(areas.get(i).getAreaName()).append("\n");
        }

        textview.setText(stringBuilder);
    }
}
