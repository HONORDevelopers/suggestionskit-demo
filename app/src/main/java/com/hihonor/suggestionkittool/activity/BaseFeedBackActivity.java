/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.suggestionkittool.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hihonor.android.magicx.intelligence.suggestion.common.config.ResultCode;
import com.hihonor.demo.suggestionkittool.R;
import com.hihonor.suggestionkittool.util.TimeUtils;

import java.util.Calendar;
import java.util.List;

/**
 * 反馈基类
 *
 * @author t00031915
 * @since 2024-7-12
 */
public abstract class BaseFeedBackActivity extends AppCompatActivity {
    /**
     * 清除日志按钮
     */
    private Button clearLogBtn;

    /**
     * 展示结果
     */
    private TextView tvResult;

    /**
     * 接口调用结果StringBuilder
     */
    private StringBuilder stringBuilder;

    /**
     * 日期
     */
    private Calendar calendar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_feedback);
        FrameLayout container = findViewById(R.id.container);
        int id = loadViewId();
        View view = LayoutInflater.from(this).inflate(id, null, false);
        container.addView(view);

        stringBuilder = new StringBuilder();
        clearLogBtn = findViewById(R.id.clear_log);
        tvResult = findViewById(R.id.tv_result);

        // 标题栏
        setTitle(getTopTitle());

        // 清除日志按钮
        clearLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stringBuilder.setLength(0);
                tvResult.setText("");
            }
        });
    }

    /**
     * 获取子页面的顶部标题
     *
     * @return 标题
     */
    protected abstract String getTopTitle();

    /**
     * 获取子类的布局id
     *
     * @return 子类的布局id
     */
    protected abstract int loadViewId();

    /**
     * 展示结果
     *
     * @param info 展示信息
     */
    protected void showResult(String info) {
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

    /**
     * 获取状态码对应的描述
     *
     * @param resultCode 状态码
     * @return 状态码对应的描述
     */
    protected String getFeedBackResultMsg(int resultCode) {
        String result = "";
        switch (resultCode) {
            // 成功
            case ResultCode.SUGGESTION_SUCCESS_CODE:
                result = getResources().getString(R.string.suggestion_feedback_success);
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
                result = getResources().getString(R.string.suggestion_feedback_failed);
                break;
        }
        return result;
    }

    /**
     * 展示时间选择弹窗
     *
     * @param onTimeSelectListener 时间选择回调
     */
    protected void showDayPickerDialog(OnTimeSelectListener onTimeSelectListener) {
        calendar = Calendar.getInstance();

        // 显示日期选择对话框
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // 显示时间选择对话框
                    showTimePickerDialog(onTimeSelectListener);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    protected void showTimePickerDialog(OnTimeSelectListener onTimeSelectListener) {
        if (calendar == null) {
            calendar = Calendar.getInstance();
        }
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, 0);

                    long timestamp = calendar.getTimeInMillis();
                    onTimeSelectListener.onTimeSelect(String.valueOf(timestamp), hourOfDay, minute);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);

        timePickerDialog.show();
    }

    /**
     * 时间选择回调
     */
    public interface OnTimeSelectListener {
        void onTimeSelect(String time, int hour, int minute);
    }

    /**
     * 选择日期回调
     */
    public interface OnDaySelectListener {
        void onDaySelect(List<Integer> selectedDaysList);
    }
}
