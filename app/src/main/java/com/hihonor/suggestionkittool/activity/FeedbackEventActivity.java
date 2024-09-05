/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.suggestionkittool.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.hihonor.android.magicx.intelligence.suggestion.Suggestion;
import com.hihonor.android.magicx.intelligence.suggestion.api.FeedbackClient;
import com.hihonor.android.magicx.intelligence.suggestion.callback.FeedbackCallback;
import com.hihonor.android.magicx.intelligence.suggestion.model.EventFeedbackReq;
import com.hihonor.android.magicx.intelligence.suggestion.model.FeatureCheckReq;
import com.hihonor.demo.suggestionkittool.R;
import com.hihonor.suggestionkittool.util.Logger;
import com.hihonor.suggestionkittool.view.SuggestItemView;

import java.lang.ref.WeakReference;

/**
 * 反馈事件界面
 *
 * @author t00031915
 * @since 2024-7-12
 */
public class FeedbackEventActivity extends BaseFeedBackActivity {
    /**
     * 默认事件失效时间与生效时间的间隔
     */
    private static final int MILL_SECOND_30000 = 30000;

    /**
     * 连续反馈间隔毫秒数
     */
    private static final int INIT_CONTINUE_GAP = 3000;

    /**
     * 连续反馈次数
     */
    private static final int INIT_CONTINUE_COUNT = 3;

    /**
     * 时间默认编号
     */
    private static final String INIT_VALUE = "1";

    /**
     * 连续反馈msg
     */
    private static final int CONTINUE_FEED_MSG = 1;

    private static final String TAG = "FeedbackEventActivity";

    /**
     * Suggestion实例
     */
    private Suggestion suggestion = null;

    /**
     * FeedbackClient实例
     */
    private FeedbackClient feedbackClient = null;

    /**
     * 功能名称
     */
    private SuggestItemView featureName;

    /**
     * 意图类型
     */
    private SuggestItemView intentTypeView;

    /**
     * 事件编号
     */
    private SuggestItemView eventOrder;

    /**
     * 事件状态
     */
    private SuggestItemView eventStatus;

    /**
     * 发生时间
     */
    private SuggestItemView createTime;

    /**
     * 事件生效时间
     */
    private SuggestItemView beginTime;

    /**
     * 事件失效时间
     */
    private SuggestItemView endTime;

    /**
     * 连续反馈次数
     */
    private SuggestItemView continueCount;

    /**
     * 连续反馈间隔
     */
    private SuggestItemView continuousGap;

    /**
     * 连续反馈按钮
     */
    private Button continueBtn;

    /**
     * 连续反馈handler
     */
    private final MyHandler handler = new MyHandler(this);
    /**
     * 连续反馈handler
     *
     * @since 2024-7-12
     */
    private static class MyHandler extends Handler {
        private final WeakReference<FeedbackEventActivity> mActivity;

        private MyHandler(FeedbackEventActivity feedbackEventActivity) {
            super();
            this.mActivity = new WeakReference<FeedbackEventActivity>(feedbackEventActivity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (mActivity.get() == null) {
                return;
            }

            FeedbackEventActivity activity = mActivity.get();
            activity.feedBack();
            int sum = Integer.parseInt(activity.continueCount.getValue());
            int gap = Integer.parseInt(activity.continuousGap.getValue());
            int count = msg.arg1;
            Logger.info(TAG, "连续反馈->count : " + count + "  gap : " + gap + " sum :" + sum);
            if (count < sum) {
                Message message = Message.obtain();
                message.what = CONTINUE_FEED_MSG;
                count++;
                message.arg1 = count;
                sendMessageDelayed(message, gap);
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        suggestion = Suggestion.getInstance(this.getApplicationContext());
        initViews();
        initButton();
    }

    @Override
    protected String getTopTitle() {
        return getString(R.string.feedbackEvent);
    }

    @Override
    protected int loadViewId() {
        return R.layout.activity_feedback_event;
    }

    /**
     * 初始化方法
     */
    private void initViews() {
        // 名称
        featureName = findViewById(R.id.feature_name);
        initFeatureName();

        // 意图类型
        intentTypeView = findViewById(R.id.intent_type);
        initIntentType();

        // 事件编号
        eventOrder = findViewById(R.id.event_order);
        initEventOrder();

        // 事件状态
        eventStatus = findViewById(R.id.event_status);
        initEventStatus();

        // 发生时间
        createTime = findViewById(R.id.create_time);
        initCreateTime();

        // 事件生效时间
        beginTime = findViewById(R.id.begin_time);
        initBeginTime();

        // 事件失效时间
        endTime = findViewById(R.id.end_time);
        initEndTime();

        // 连续反馈次数
        continueCount = findViewById(R.id.continuous_count);
        initContinueCount();
    }

    /**
     * 初始化按钮以及点击事件
     */
    private void initButton() {
        // hasFeature接口按钮
        Button hasFeature = findViewById(R.id.has_feature);
        hasFeature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasFeatureTest();
            }
        });

        // 连续反馈间隔秒数
        continuousGap = findViewById(R.id.continuous_gap);
        initContinueGap();

        // 反馈事件
        Button feedBackEvent = findViewById(R.id.feedbackEvent_btn);
        feedBackEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedBack();
            }
        });

        // 连续反馈
        continueBtn = findViewById(R.id.continue_btn);
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = Message.obtain();
                message.what = CONTINUE_FEED_MSG;
                message.arg1 = 1;
                handler.sendMessage(message);
            }
        });
    }

    /**
     * 连续反馈
     */
    private void initContinueGap() {
        continuousGap.setTitle(getResources().getString(R.string.feedback_continuous_gap));
        continuousGap.setValue(String.valueOf(INIT_CONTINUE_GAP));
        continuousGap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(continuousGap);
            }
        });
    }

    /**
     * 连续反馈次数
     */
    private void initContinueCount() {
        continueCount.setTitle(getResources().getString(R.string.suggestion_continuous_count));
        continueCount.setValue(String.valueOf(INIT_CONTINUE_COUNT));
        continueCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(continueCount);
            }
        });
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

    /**
     * hasFeature接口
     */
    private void hasFeatureTest() {
        FeatureCheckReq featureCheckReq = new FeatureCheckReq();
        featureCheckReq.setFeatureName("FEEDBACK_EVENT");
        featureCheckReq.setPackageName(getPackageName());
        if (suggestion.hasFeature(featureCheckReq)) {
            Logger.info(TAG, "支持此特性");
            showResult("支持此特性");
        } else {
            showResult("不支持此特性");
            Logger.info(TAG, "不支持此特性");
        }
    }

    /**
     * 反馈事件点击处理逻辑
     */
    private void feedBack() {
        getFeedbackClient();

        if (feedbackClient == null) {
            Logger.error(TAG, "反馈接口未实例化");
            return;
        }
        Logger.info(TAG, "feedbackEvent begin.");
        EventFeedbackReq eventFeedbackReq = new EventFeedbackReq();
        eventFeedbackReq.setIntentType(intentTypeView.getValue());
        eventFeedbackReq.setPackageName(getPackageName());
        eventFeedbackReq.setCreateTime(Long.parseLong(createTime.getValue()));
        eventFeedbackReq.setBeginTime(Long.parseLong(beginTime.getValue()));
        eventFeedbackReq.setEndTime(Long.parseLong(endTime.getValue()));
        eventFeedbackReq.setEventStatus(eventStatus.getValue());
        boolean isFeedbackOk = feedbackClient.feedbackEvent(eventFeedbackReq, new FeedbackCallback() {
            @Override
            public void onResult(int resultCode) {
                Logger.info(TAG, "resultCode = " + resultCode);
                String msg = getFeedBackResultMsg(resultCode);
                showResult(msg);
                Logger.info(TAG, msg);
            }
        });
        if (!isFeedbackOk) {
            Logger.info(TAG, getResources().getString(R.string.suggestion_feedback_failed));
            showResult(getResources().getString(R.string.suggestion_feedback_failed));
        }
        Logger.info(TAG, "feedbackEvent end.");
    }

    /**
     * 事件失效时间
     */
    private void initEndTime() {
        endTime.setTitle(getResources().getString(R.string.suggestion_feedback_event_endTime));
        endTime.setValue(String.valueOf(System.currentTimeMillis() + MILL_SECOND_30000));
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDayPickerDialog(new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(String time, int hour, int minute) {
                        endTime.setValue(time);
                    }
                });
            }
        });
    }

    /**
     * 事件发生时间
     */
    private void initCreateTime() {
        createTime.setTitle(getResources().getString(R.string.awareness_kit_feedback_action_timeStamp));
        createTime.setValue(String.valueOf(System.currentTimeMillis()));
        createTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDayPickerDialog(new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(String time, int hour, int minute) {
                        createTime.setValue(time);
                    }
                });
            }
        });
    }

    /**
     * 事件生效时间
     */
    private void initBeginTime() {
        beginTime.setTitle(getResources().getString(R.string.suggestion_feedback_event_beginTime));
        beginTime.setValue(String.valueOf(System.currentTimeMillis()));
        beginTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDayPickerDialog(new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(String time, int hour, int minute) {
                        beginTime.setValue(time);
                    }
                });
            }
        });
    }

    /**
     * 事件编号
     */
    private void initEventOrder() {
        eventOrder.setTitle(getResources().getString(R.string.suggestion_feedback_event_num));
        eventOrder.setValue(INIT_VALUE);
        eventOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(eventOrder);
            }
        });
    }

    /**
     * 事件状态
     */
    private void initEventStatus() {
        eventStatus.setTitle(getResources().getString(R.string.suggestion_feedback_event_eventStatus));
        eventStatus.setValue(INIT_VALUE);
        eventStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(eventStatus);
            }
        });
    }

    /**
     * 功能名称
     */
    private void initFeatureName() {
        featureName.setTitle(getResources().getString(R.string.feature_name));
        featureName.setValue("FEEDBACK_EVENT");
        featureName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(featureName);
            }
        });
    }

    /**
     * 意图类型
     */
    private void initIntentType() {
        intentTypeView.setTitle(getResources().getString(R.string.suggestion_kit_feedback_action_intentType));
        intentTypeView.setValue("1020700002");
        intentTypeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(intentTypeView);
            }
        });
    }

    /**
     * 展示对话框
     *
     * @param suggestItemView view
     */
    private void showDialog(SuggestItemView suggestItemView) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_view, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();
        TextView title = view.findViewById(R.id.title);
        title.setText(getString(R.string.suggestion_please_input_one, suggestItemView.getTitle()));
        EditText et = view.findViewById(R.id.et);
        et.setText(suggestItemView.getValue());
        Button confirm = view.findViewById(R.id.btn_confirm);
        Button cancel = view.findViewById(R.id.btn_cancel);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                suggestItemView.setValue(et.getText().toString());
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
