/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.suggestionkittool.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hihonor.demo.suggestionkittool.R;

/**
 * 主界面的MainActivity
 *
 * @author t00031915
 * @since 2024-7-12
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Button feedback = findViewById(R.id.feedback);
        Button sportStatus = findViewById(R.id.sport_status);

        // 反馈页面
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FeedBackActivity.class);
                startActivity(intent);
            }
        });

        // 运动状态页面
        sportStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SportStatusActivity.class);
                startActivity(intent);
            }
        });
    }
}
