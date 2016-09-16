package com.ty.phoneguardian.Activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.ty.phoneguardian.R;

public class BaseCacheClearActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_cache_clear);

        //1.生成选项卡1(tag 选项卡的唯一标识)
        TabHost.TabSpec tab1 = getTabHost().newTabSpec("clear_cache").setIndicator("缓存清理");
        //2.生成选项卡2(tag 选项卡的唯一标识)
        TabHost.TabSpec tab2 = getTabHost().newTabSpec("sd_clear").setIndicator("sd卡清理");
        //3.告知选中选项卡的操作
        tab1.setContent(new Intent(this, CacheClearActivity.class));
        tab2.setContent(new Intent(this, SDCacheClearActivity.class));

        //4.将此两个选项卡维护到 host (选项卡的宿主)中去
        getTabHost().addTab(tab1);
        getTabHost().addTab(tab2);
    }
}
