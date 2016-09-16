package com.ty.phoneguardian.SetNavigation;

import android.content.Intent;
import android.os.Bundle;

import com.ty.phoneguardian.Activity.BaseNavigationActivity;
import com.ty.phoneguardian.R;

public class NavigationFirstActivity extends BaseNavigationActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_first);

    }

    @Override
    public void sendToNextPage() {
        Intent intent = new Intent(this, NavigationSecondActivity.class);
        startActivity(intent);
        finish();

        //开启平移动画
        overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
    }

    @Override
    public void sendToPrePage() {

    }
}
