/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.suggestionkittool.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.hihonor.android.magicx.intelligence.suggestion.Suggestion;
import com.hihonor.android.magicx.intelligence.suggestion.api.FeedbackClient;
import com.hihonor.android.magicx.intelligence.suggestion.callback.FeedbackCallback;
import com.hihonor.android.magicx.intelligence.suggestion.common.config.ResultCode;
import com.hihonor.demo.suggestionkittool.R;
import com.hihonor.suggestionkittool.util.Logger;

/**
 * 删除数据页面
 *
 * @author t00031915
 * @since 2024-07-16
 */
public class FeedbackDeleteActivity extends BaseFeedBackActivity {
    private static final String TAG = "FeedbackDeleteActivity";

    /**
     * FeedbackClient实例
     */
    private FeedbackClient feedbackClient = null;

    /**
     * Suggestion实例
     */
    private Suggestion suggestion = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        suggestion = Suggestion.getInstance(this.getApplicationContext());

        // 删除数据
        Button deleteData = findViewById(R.id.delete_feedback);
        deleteData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData();
            }
        });
    }

    @Override
    protected String getTopTitle() {
        return getString(R.string.delete_feedback);
    }

    @Override
    protected int loadViewId() {
        return R.layout.activity_feedback_delete;
    }

    /**
     * 删除数据
     */
    private void deleteData() {
        getFeedbackClient();
        boolean isFeedbackOk = feedbackClient.deleteFeedbackAll(getPackageName(), new FeedbackCallback() {
            @Override
            public void onResult(int resultCode) {
                Logger.info(TAG, "resultCode = " + resultCode);
                String result;
                switch (resultCode) {
                    // 成功
                    case ResultCode.SUGGESTION_SUCCESS_CODE:
                        result = getResources().getString(R.string.delete_feedback_success);
                        break;
                    // 参数校验不通过
                    case ResultCode.SUGGESTION_PARAM_ERROR_CODE:
                        result = getResources().getString(R.string.suggestion_feedback_param_error);
                        break;
                    // 接口流控，短时间内调用次数太频繁
                    case ResultCode.SUGGESTION_FLOW_CODE:
                        result = getResources().getString(R.string.suggestion_feedback_flow_error);
                        break;
                    // 权限校验失败
                    case ResultCode.PERMISSION_VERIFICATION_FAIL:
                        result = getResources().getString(R.string.suggestion_feedback_verification_error);
                        break;
                    // 服务内部异常
                    case ResultCode.SUGGESTION_SERVICE_ERROE_CODE:
                        result = getResources().getString(R.string.suggestion_feedback_service_error);
                        break;
                    // 未授权
                    case ResultCode.SUGGESTION_UNAUTHORIZED_CODE:
                        result = getResources().getString(R.string.suggestion_feedback_unauthorized_error);
                        break;
                    // 初始化活动监测服务失败
                    case ResultCode.MOTION_INITIALIZATION_FAIL:
                        result = getResources().getString(R.string.suggestion_feedback_motion_initialization_error);
                        break;
                    default:
                        result = getResources().getString(R.string.delete_feedback_failed);
                        break;
                }
                showResult(result);
                Logger.info(TAG, result);
            }
        });
        if (!isFeedbackOk) {
            showResult(getResources().getString(R.string.delete_feedback_failed));
            Logger.info(TAG, getResources().getString(R.string.delete_feedback_failed));
        }
    }

    /**
     * 获取数据反馈接口
     */
    private void getFeedbackClient() {
        feedbackClient = suggestion.getFeedbackClient();
        if (feedbackClient != null) {
            Logger.info(TAG, getString(R.string.feedback_interface_success));
        } else {
            Logger.info(TAG, getString(R.string.feedback_interface_failed));
        }
    }
}
