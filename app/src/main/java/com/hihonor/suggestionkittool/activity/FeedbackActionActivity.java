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
import com.hihonor.android.magicx.intelligence.suggestion.model.ActionFeedbackReq;
import com.hihonor.android.magicx.intelligence.suggestion.model.FeatureCheckReq;
import com.hihonor.demo.suggestionkittool.R;
import com.hihonor.suggestionkittool.util.Logger;
import com.hihonor.suggestionkittool.view.SuggestItemView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 反馈服务使用记录页面
 *
 * @author t00031915
 * @since 2024-7-12
 */
public class FeedbackActionActivity extends BaseFeedBackActivity {
    /**
     * 连续反馈间隔毫秒数
     */
    private static final int INIT_CONTINUE_COUNT = 3000;

    /**
     * 连续反馈msg
     */
    private static final int CONTINUE_FEED_MSG = 1;

    private static final String TAG = "FeedbackActionActivity";

    /**
     * 连续反馈handler
     */
    private final MyHandler handler = new MyHandler(this);

    /**
     * Suggestion实例
     */
    private Suggestion suggestion = null;

    /**
     * FeedbackClient实例
     */
    private FeedbackClient feedbackClient = null;

    /**
     * 功能类型
     */
    private SuggestItemView featureName;

    /**
     * 意图类型
     */
    private SuggestItemView intentTypeView;

    /**
     * 操作类型
     */
    private SuggestItemView actionType;

    /**
     * 发生时间
     */
    private SuggestItemView timeStamp;

    /**
     * 连续反馈次数
     */
    private SuggestItemView continueCount;

    /**
     * 连续反馈间隔毫秒数
     */
    private SuggestItemView continuesGap;

    /**
     * 连续反馈按钮
     */
    private Button continueBtn;

    /**
     * 多线程测试按钮
     */
    private Button concurrentBtn;

    @Override
    protected String getTopTitle() {
        return getString(R.string.feedbackAction);
    }

    @Override
    protected int loadViewId() {
        return R.layout.activity_feedback_action;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        suggestion = Suggestion.getInstance(this.getApplicationContext());
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        // 名称
        featureName = findViewById(R.id.feature_name);
        initFeatureName();

        // 意图类型
        intentTypeView = findViewById(R.id.intent_type);
        initIntentType();

        // 操作类型
        actionType = findViewById(R.id.actionType);
        actionType();

        // 发生时间
        timeStamp = findViewById(R.id.timeStamp);
        timeStamp();

        // 连续反馈次数
        continueCount = findViewById(R.id.continuous_count);
        intiContinueCount();

        // 连续反馈间隔秒数
        continuesGap = findViewById(R.id.continuous_gap);
        initContinueGap();

        // HASFEATURE接口测试
        Button hasFeature = findViewById(R.id.has_feature);
        hasFeature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasFeatureTest();
            }
        });

        // 反馈服务使用记录
        Button feedBackEvent = findViewById(R.id.feedbackAction_btn);
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

        // 多线程测试
        concurrentBtn = findViewById(R.id.concurrent_btn);
        concurrentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                concurrentFeedback();
            }
        });
    }

    /**
     * FeatureName
     */
    private void initFeatureName() {
        featureName.setTitle(getResources().getString(R.string.feature_name));
        featureName.setValue("FEEDBACK_ACTION");
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
        intentTypeView.setValue("1020700001");
        intentTypeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(intentTypeView);
            }
        });
    }

    /**
     * 操作类型
     */
    private void actionType() {
        actionType.setTitle(getResources().getString(R.string.awareness_kit_feedback_action_actionType));
        actionType.setValue("1");
        actionType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(actionType);
            }
        });
    }

    /**
     * 发生时间
     */
    private void timeStamp() {
        timeStamp.setTitle(getResources().getString(R.string.awareness_kit_feedback_action_timeStamp));
        timeStamp.setValue(String.valueOf(System.currentTimeMillis()));
        timeStamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDayPickerDialog(new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(String time, int hour, int minute) {
                        timeStamp.setValue(time);
                    }
                });
            }
        });
    }

    /**
     * 连续反馈次数
     */
    private void intiContinueCount() {
        continueCount.setTitle(getResources().getString(R.string.suggestion_continuous_count));
        continueCount.setValue(String.valueOf(1));
        continueCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(continueCount);
            }
        });
    }

    /**
     * 连续反馈间隔毫秒数
     */
    private void initContinueGap() {
        continuesGap.setTitle(getResources().getString(R.string.feedback_continuous_gap));
        continuesGap.setValue(String.valueOf(INIT_CONTINUE_COUNT));
        continuesGap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(continuesGap);
            }
        });
    }

    /**
     * 展示弹窗
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

    /**
     * hasFeature接口
     */
    private void hasFeatureTest() {
        FeatureCheckReq featureCheckReq = new FeatureCheckReq();
        featureCheckReq.setFeatureName("FEEDBACK_ACTION");
        featureCheckReq.setPackageName(getPackageName());
        if (suggestion.hasFeature(featureCheckReq)) {
            Logger.info(TAG, "支持此特性");
            showResult("支持此特性");
        } else {
            Logger.info(TAG, "不支持此特性");
            showResult("不支持此特性");
        }
    }

    /**
     * 反馈事件点击处理逻辑
     */
    private void feedBack() {
        getFeedbackClient();

        if (feedbackClient == null) {
            Logger.error(TAG, "interface is not instantiated");
            showResult("interface is not instantiated");
            return;
        }
        Logger.info(TAG, "feedbackAction begin.");

        ActionFeedbackReq actionFeedbackReq = new ActionFeedbackReq();
        actionFeedbackReq.setIntentType(intentTypeView.getValue());
        actionFeedbackReq.setPackageName(getPackageName());
        ActionFeedbackReq.ActionFeedbackData actionFeedbackData = new ActionFeedbackReq.ActionFeedbackData();
        actionFeedbackData.setActionType(actionType.getValue());
        actionFeedbackData.setCreateTime(Long.parseLong(timeStamp.getValue()));
        List<ActionFeedbackReq.ActionFeedbackData> actionFeedbackDataList = new ArrayList<>();
        actionFeedbackDataList.add(actionFeedbackData);
        actionFeedbackReq.setActionFeedbackDatas(actionFeedbackDataList);

        boolean isFeedbackOk = feedbackClient.feedbackAction(actionFeedbackReq, new FeedbackCallback() {
            @Override
            public void onResult(int resultCode) {
                Logger.info(TAG, "resultCode = " + resultCode);
                String msg = getFeedBackResultMsg(resultCode);
                showResult(msg);
                Logger.info(TAG, msg);
            }
        });
        if (!isFeedbackOk) {
            showResult(getResources().getString(R.string.suggestion_feedback_failed));
            Logger.info(TAG, getResources().getString(R.string.suggestion_feedback_failed));
        }
        Logger.info(TAG, "feedbackEvent end.");
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
     * 多线程调用测试
     */
    private void concurrentFeedback() {
        try {
            ExecutorService threadPool = new ThreadPoolExecutor(16, 32,
                    2, TimeUnit.MINUTES, new ArrayBlockingQueue<>(256),
                    new ThreadPoolExecutor.AbortPolicy());
            for (int index = 0; index < 200; index++) {
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        Logger.info(TAG, "concurrentTest");
                        feedBack();
                        try {
                            Thread.sleep(12000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (RejectedExecutionException exception) {
            Logger.error(TAG, "rejectedExecutionException in concurrentFeedback");
        } catch (Exception exception) {
            Logger.error(TAG, "exception in concurrentFeedback");
        }
    }

    /**
     * handler
     *
     * @since 2022-10-09
     */
    private static class MyHandler extends Handler {
        private final WeakReference<FeedbackActionActivity> mActivity;

        private MyHandler(FeedbackActionActivity feedbackActionActivity) {
            super();
            this.mActivity = new WeakReference<FeedbackActionActivity>(feedbackActionActivity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (mActivity.get() == null) {
                return;
            }

            FeedbackActionActivity activity = mActivity.get();
            activity.feedBack();
            int sum = Integer.parseInt(activity.continueCount.getValue());
            int gap = Integer.parseInt(activity.continuesGap.getValue());
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
}
