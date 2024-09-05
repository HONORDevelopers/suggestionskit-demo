/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.suggestionkittool.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.hihonor.android.magicx.intelligence.suggestion.Suggestion;
import com.hihonor.android.magicx.intelligence.suggestion.api.AwarenessClient;
import com.hihonor.android.magicx.intelligence.suggestion.callback.CaptureCallback;
import com.hihonor.android.magicx.intelligence.suggestion.callback.InitCallback;
import com.hihonor.android.magicx.intelligence.suggestion.common.config.ResultCode;
import com.hihonor.android.magicx.intelligence.suggestion.common.enumrate.FeatureEnum;
import com.hihonor.brain.kitservice.awareness.BehaviorResponse;
import com.hihonor.demo.suggestionkittool.R;
import com.hihonor.suggestionkittool.util.Logger;
import com.hihonor.suggestionkittool.util.TimeUtils;

/**
 * 运动状态界面
 *
 * @author t00031915
 * @since 2024-7-12
 */
public class SportStatusActivity extends AppCompatActivity {
    private static final String TAG = "SportStatusActivity";

    private static final int RESULT_CODE = 6;

    /**
     * 动态申请健康运动权限
     */
    private static final String[] ACTIVITY_RECOGNITION_PERMISSION = {Manifest.permission.ACTIVITY_RECOGNITION};

    /**
     * 健康运动权限
     */
    private static final String SPORT_STATUS_PERMISSION = "android.permission.ACTIVITY_RECOGNITION";

    /**
     * 显示调用结果
     */
    private TextView tvResult;

    /**
     * 调用结果
     */
    private final StringBuilder stringBuilder = new StringBuilder();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_status);
        setTitle(getString(R.string.kit_sport_status));
        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Button permissionCheck = findViewById(R.id.permission_check);
        Button permissionRequest = findViewById(R.id.permission_request);
        tvResult = findViewById(R.id.tv_result);

        // 运动健身权限检查按钮
        permissionCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasPermission = hasPermission(SportStatusActivity.this);
                Toast.makeText(SportStatusActivity.this, hasPermission ? getString(R.string.kit_sport_status_permission_granted) : getString(R.string.kit_sport_status_permission_forbidden),
                        Toast.LENGTH_LONG).show();
            }
        });

        // 运动健身权限请求按钮
        permissionRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.info(TAG, "permissionRequest click");
                if (hasPermission(SportStatusActivity.this)) {
                    Toast.makeText(SportStatusActivity.this, getString(R.string.kit_sport_status_permission_granted), Toast.LENGTH_LONG).show();
                } else {
                    ActivityCompat.requestPermissions(SportStatusActivity.this,
                            ACTIVITY_RECOGNITION_PERMISSION, RESULT_CODE);
                }
            }
        });

        // 运动状态查询
        Suggestion suggestion = Suggestion.getInstance(getApplicationContext());
        boolean isSupported = suggestion.hasFeature(FeatureEnum.DETECT_MOTION.getValue());
        Logger.info(TAG, "isSupported: " + isSupported);
        Button query = findViewById(R.id.query);
        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBehavior();
            }
        });

        // 初始化运动状态查询
        Button init = findViewById(R.id.init);
        init.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = suggestion.hasFeature(FeatureEnum.DETECT_MOTION.getValue());
                Logger.info(TAG, "current sdk scope flag is " + flag);
                AwarenessClient awarenessClient = suggestion.getAwarenessClient();
                awarenessClient.init(SportStatusActivity.this.getPackageName(), new InitCallback() {
                    @Override
                    public void onResponse(int resCode) {
                        if (resCode == ResultCode.SUGGESTION_SUCCESS_CODE) {
                            showResult(getString(R.string.sport_init_success));
                            Logger.info(TAG, getString(R.string.sport_init_success));
                        } else {
                            showResult(getString(R.string.sport_init_fail));
                            Logger.info(TAG, getString(R.string.sport_init_fail));
                        }
                    }
                });
            }
        });

        // 清除结果日志
        Button clear = findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stringBuilder.setLength(0);
                tvResult.setText("");
            }
        });
    }

    /**
     * 运动状态查询
     */
    private void getBehavior() {
        Suggestion suggestion = Suggestion.getInstance(getApplicationContext());
        // 运动状态查询
        boolean flag = suggestion.hasFeature(FeatureEnum.DETECT_MOTION.getValue());
        Logger.info(TAG, "current sdk scope flag is " + flag);
        AwarenessClient awarenessClient = suggestion.getAwarenessClient();
        awarenessClient.getBehavior(SportStatusActivity.this.getPackageName(), new CaptureCallback() {
            @Override
            public void onResponse(BehaviorResponse resp) {
                int resCode = resp.getResCode();
                if (resCode == ResultCode.SUGGESTION_SUCCESS_CODE) {
                    showResult(getString(R.string.get_sport_status_success));
                    Logger.info(TAG, getString(R.string.get_sport_status_success));
                } else {
                    showResult(getString(R.string.get_sport_status_fail));
                    Logger.info(TAG, getString(R.string.get_sport_status_fail));
                }
            }
        });
    }

    /**
     * 健身运动权限检查
     *
     * @param context context
     * @return boolean true 有运动健身权限 false 没有运动健身权限
     */
    public boolean hasPermission(Context context) {
        PackageManager pm = context.getPackageManager();
        return pm.checkPermission(SPORT_STATUS_PERMISSION, getPackageName()) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 展示结果日志
     *
     * @param info 结果日志信息
     */
    private void showResult(String info) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String time = TimeUtils.timeToFullString(System.currentTimeMillis());
                String result = time + ":" + info;
                stringBuilder.append("\n").append(result);
                tvResult.setText(stringBuilder.toString());
            }
        });
    }
}
