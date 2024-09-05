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
 * 反馈入口总界面FeedBackActivity
 *
 * @author t00031915
 * @since 2024-7-12
 */
public class FeedBackActivity extends AppCompatActivity {
    private static final String TAG = "FeedBackActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        setTitle(getString(R.string.feedback));
        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Button feedbackAction = findViewById(R.id.feedbackAction);
        Button feedbackEvent = findViewById(R.id.feedbackEvent);
        Button feedbackPlan = findViewById(R.id.feedbackPlan);
        Button feedBackOrder = findViewById(R.id.feedback_order);
        Button deleteData = findViewById(R.id.delete_feedback);

        // 反馈服务使用记录点击事件
        feedbackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedBackActivity.this, FeedbackActionActivity.class);
                startActivity(intent);
            }
        });

        // 反馈事件点击事件
        feedbackEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedBackActivity.this, FeedbackEventActivity.class);
                startActivity(intent);
            }
        });

        // 计划反馈点击事件
        feedbackPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedBackActivity.this, FeedbackPlanActivity.class);
                startActivity(intent);
            }
        });

        // 删除数据点击事件
        deleteData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedBackActivity.this, FeedbackDeleteActivity.class);
                startActivity(intent);
            }
        });

        // 反馈订单点击事件
        feedBackOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedBackActivity.this, FeedBackOrderActivity.class);
                startActivity(intent);
            }
        });
    }
}
