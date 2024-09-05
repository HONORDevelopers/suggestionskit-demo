/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.suggestionkittool.activity;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import com.hihonor.android.magicx.intelligence.suggestion.model.FeatureCheckReq;
import com.hihonor.android.magicx.intelligence.suggestion.model.OrderFeedbackReq;
import com.hihonor.demo.suggestionkittool.R;
import com.hihonor.suggestionkittool.util.FileUtils;
import com.hihonor.suggestionkittool.util.Logger;
import com.hihonor.suggestionkittool.view.SuggestItemView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * 反馈订单界面
 *
 * @author t00031915
 * @since 2024-7-12
 */
public class FeedBackOrderActivity extends BaseFeedBackActivity {
    private static final String TAG = "FeedBackOrderActivity";

    /**
     * 连续反馈msg
     */
    private static final int CONTINUE_FEED_MSG = 1;

    /**
     * 意图类型
     */
    private SuggestItemView intentTypeItemView;

    /**
     * 订单编号
     */
    private SuggestItemView orderNo;

    /**
     * 订单状态
     */
    private SuggestItemView statusView;

    /**
     * 订单未完成原因
     */
    private SuggestItemView failedReason;

    /**
     * 订单状态描述
     */
    private SuggestItemView statusDesc;

    /**
     * 订单创建时间
     */
    private SuggestItemView orderCreateTime;

    /**
     * 订单开始时间
     */
    private SuggestItemView startTime;

    /**
     * 订单结束时间
     */
    private SuggestItemView endTime;

    /**
     * 订单数据
     */
    private SuggestItemView orderData;

    /**
     * 功能名称
     */
    private SuggestItemView featureName;

    /**
     * 连续反馈次数
     */
    private SuggestItemView continuousCount;

    /**
     * 多次反馈次数
     */
    private SuggestItemView feedbackTimes;

    /**
     * 连续反馈间隔
     */
    private SuggestItemView continuousGap;

    /**
     * 订单数据文件路径
     */
    private String orderDataPath;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        suggestion = Suggestion.getInstance(this.getApplicationContext());
        File fileDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (fileDir != null) {
            orderDataPath = fileDir.getPath();
        }
        initViews();
    }

    @Override
    protected String getTopTitle() {
        return getString(R.string.kit_feedback_order);
    }

    @Override
    protected int loadViewId() {
        return R.layout.activity_feedback_order;
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
     * 连续反馈handler
     */
    private static class MyHandler extends Handler {
        private final WeakReference<FeedBackOrderActivity> mActivity;

        private MyHandler(FeedBackOrderActivity feedBackOrderActivity) {
            super();
            this.mActivity = new WeakReference<>(feedBackOrderActivity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (mActivity.get() == null) {
                return;
            }

            FeedBackOrderActivity activity = mActivity.get();
            activity.transferOrderData(1);
            int sum = Integer.parseInt(activity.continuousCount.getValue());
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

    /**
     * hasFeature接口
     */
    private void hasFeatureTest() {
        FeatureCheckReq featureCheckReq = new FeatureCheckReq();
        featureCheckReq.setFeatureName("FEEDBACK_ORDER");
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
     * 初始化view
     */
    private void initViews() {
        featureName = findViewById(R.id.feature_name);
        initTitleAndValue(featureName, getResources().getString(R.string.feature_name), "FEEDBACK_ORDER");

        // hasFeature接口
        Button hasFeature = findViewById(R.id.has_feature);
        hasFeature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasFeatureTest();
            }
        });

        // 意图类型
        intentTypeItemView = findViewById(R.id.intent_type);
        intentTypeItemView.setNotNull();
        initTitleAndValue(intentTypeItemView, "意图类型", "1030400001");

        // 订单号
        orderNo = findViewById(R.id.orderNo);
        orderNo.setDescription("字段说明:最大长度64");
        orderNo.setNotNull();
        initTitleAndValue(orderNo, "订单编号", "orderNo" + System.currentTimeMillis());

        // 订单状态
        statusView = findViewById(R.id.status);
        statusView.setNotNull();
        initTitleAndValue(statusView, "订单状态", "1");

        // 订单未完成原因
        failedReason = findViewById(R.id.reason);
        initTitleAndValue(failedReason, "订单未完成原因", "1");

        // 订单状态描述
        statusDesc = findViewById(R.id.status_desc);
        initTitleAndValue(statusDesc, "订单状态描述", "1");

        // 订单数据
        orderData = findViewById(R.id.order_data);
        initTitleAndValue(orderData, "订单数据", "");
        if (orderDataPath != null) {
            orderData.setDescription("请在路径" + orderDataPath + "下存入订单内容文件，并输入文件名");
        } else {
            orderData.setDescription("找不到外部目录");
        }

        // 订单创建时间
        orderCreateTime = findViewById(R.id.orderCreateTime);
        orderCreateTime.setNotNull();
        initTime(orderCreateTime, "订单创建时间", String.valueOf(System.currentTimeMillis()));

        // 订单开始时间
        startTime = findViewById(R.id.startTime);
        startTime.setNotNull();
        initTime(startTime, "订单开始时间", String.valueOf(System.currentTimeMillis()));

        // 订单结束时间
        endTime = findViewById(R.id.endTime);
        endTime.setNotNull();
        initTime(endTime, "订单结束时间", String.valueOf(System.currentTimeMillis() + 600 * 1000));

        // 反馈
        Button feedBackOrder = findViewById(R.id.feedback_order);
        feedBackOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transferOrderData(1);
            }
        });

        // 多次反馈计划次数
        feedbackTimes = findViewById(R.id.feedBackOrderTimes);
        initTitleAndValue(feedbackTimes, "多次反馈订单", "3");

        // 多次反馈计划次数
        continuousCount = findViewById(R.id.continuous_count);
        initTitleAndValue(continuousCount, "连续反馈次数", "3");

        // 多次反馈计划次数
        continuousGap = findViewById(R.id.continuous_gap);
        initTitleAndValue(continuousGap, "连续反馈间隔毫秒数", "3000");

        // 多次反馈
        Button feedBackOrderBtnMany = findViewById(R.id.feedbackOrder_btn_many);
        feedBackOrderBtnMany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    transferOrderData(Integer.parseInt(feedbackTimes.getValue()));
                } catch (NumberFormatException e) {
                    Logger.info(TAG, "feedBackOrderBtnMany NumberFormatException");
                }
            }
        });
        // 连续反馈
        Button continueBtn = findViewById(R.id.continue_btn);
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
     * 数据处理并反馈
     *
     * @param times 次数
     */
    private void transferOrderData(int times) {
        Logger.info(TAG, "feedBackOrder Times : " + times);
        OrderFeedbackReq orderFeedbackReq = new OrderFeedbackReq();
        try {
            for (int i = 0; i < times; i++) {
                orderFeedbackReq.setIntentType(intentTypeItemView.getValue());
                orderFeedbackReq.setPackageName(getPackageName());
                orderFeedbackReq.setOrderNo(orderNo.getValue());
                orderFeedbackReq.setStatusCode(Integer.parseInt(statusView.getValue()));
                if (!TextUtils.isEmpty(failedReason.getValue())) {
                    orderFeedbackReq.setFailReason(Integer.parseInt(failedReason.getValue()));
                }
                orderFeedbackReq.setStatusDesc(statusDesc.getValue());
                orderFeedbackReq.setCreateTime(orderCreateTime.getValue());
                orderFeedbackReq.setStartTime(startTime.getValue());
                orderFeedbackReq.setEndTime(endTime.getValue());
                orderFeedbackReq.setOrderDetail(readData(orderData.getValue()));

                getFeedbackClient();
                if (feedbackClient == null) {
                    Logger.error(TAG, "interface is not instantiated");
                    return;
                }
                boolean isFeedbackOk = feedbackClient.feedbackOrder(orderFeedbackReq, new FeedbackCallback() {
                    @Override
                    public void onResult(int i) {
                        Logger.info(TAG, "feedBackOrder result is : " + i);
                        String msg = getFeedBackResultMsg(i);
                        showResult(msg);
                        Logger.info(TAG, msg);
                    }
                });
                if (!isFeedbackOk) {
                    showResult(getString(R.string.suggestion_feedback_failed));
                    Logger.info(TAG, getString(R.string.suggestion_feedback_failed));
                }
            }
        } catch (NumberFormatException e) {
            Logger.error(TAG, "numberFormatException error " + e.getMessage());
        } catch (Exception e) {
            Logger.error(TAG, "transfer error " + e.getMessage());
        }
    }

    /**
     * 初始化时间选择控件
     *
     * @param view 控件
     * @param title 名称
     * @param value 默认初始值
     */
    private void initTime(SuggestItemView view, String title, String value) {
        view.setTitle(title);
        view.setValue(value);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDayPickerDialog(new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(String time, int hour, int minute) {
                        view.setValue(time);
                    }
                });
            }
        });
    }

    /**
     * 初始化普通控件
     *
     * @param view 控件
     * @param title 名称
     * @param value 默认初始值
     */
    private void initTitleAndValue(SuggestItemView view, String title, String value) {
        view.setTitle(title);
        view.setValue(value);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(view);
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
        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(view).create();
        TextView title = view.findViewById(R.id.title);
        title.setText(getString(R.string.suggestion_please_input_one, suggestItemView.getTitle()));
        EditText editText = view.findViewById(R.id.et);
        editText.setText(suggestItemView.getValue());
        Button confirm = view.findViewById(R.id.btn_confirm);
        Button cancel = view.findViewById(R.id.btn_cancel);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                suggestItemView.setValue(editText.getText().toString());
                alertDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    /**
     * 解析订单数据文件
     *
     * @param fileName 文件名
     * @return json数据
     */
    private JSONObject readData(String fileName) {
        if (orderDataPath == null) {
            return null;
        }
        Logger.info(TAG, "readData," + fileName + " :fileName");
        if (TextUtils.isEmpty(fileName)) {
            Logger.error(TAG, "onError:文件名不能为空");
            return null;
        }
        File file = new File(orderDataPath, fileName);
        if (!file.exists()) {
            Logger.error(TAG, "文件 " + file.getPath() + " 不存在");
            return null;
        }
        String content = FileUtils.readFile(file);
        if (TextUtils.isEmpty(content)) {
            Logger.error(TAG, "文件读取失败:" + file.getPath());
            return null;
        }
        try {
            return new JSONObject(content);
        } catch (JSONException e) {
            Logger.error(TAG, "文件内容解析失败");
            return null;
        }
    }
}
