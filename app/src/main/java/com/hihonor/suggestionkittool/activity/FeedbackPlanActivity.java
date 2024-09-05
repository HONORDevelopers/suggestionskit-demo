/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.suggestionkittool.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.hihonor.android.magicx.intelligence.suggestion.Suggestion;
import com.hihonor.android.magicx.intelligence.suggestion.api.FeedbackClient;
import com.hihonor.android.magicx.intelligence.suggestion.callback.FeedbackCallback;
import com.hihonor.android.magicx.intelligence.suggestion.model.FeatureCheckReq;
import com.hihonor.android.magicx.intelligence.suggestion.model.PlanFeedbackReq;
import com.hihonor.demo.suggestionkittool.R;
import com.hihonor.suggestionkittool.util.Logger;
import com.hihonor.suggestionkittool.view.PlanItemList;
import com.hihonor.suggestionkittool.view.SuggestItemView;

/**
 * 计划反馈界面
 *
 * @author t00031915
 * @since 2024-7-12
 */
public class FeedbackPlanActivity extends BaseFeedBackActivity {
    /**
     * 置信度初始值
     */
    private static final String INIT_VALUE = "50";

    /**
     * 连续反馈msg
     */
    private static final int CONTINUE_FEED_MSG = 1;

    /**
     * 连续反馈间隔毫秒数
     */
    private static final int INIT_RECURRENT_TYPE = 3000;

    /**
     * 连续反馈次数
     */
    private static final int INIT_CONTINUE_COUNT = 3;

    /**
     * 反馈计划次数
     */
    private static final int INIT_RECURRENT_GAP = 10;

    /**
     * 最大反馈计划次数
     */
    private static final int MAX_TIMES = 50;

    private static final String TAG = "FeedbackPlanActivity";

    /**
     * 计划失效时间距离当前时间的间隔
     */
    private static final long TWO_MONTH = 60L * 24L * 3600000L;

    /**
     * 连续反馈的handler
     */
    private final MyHandler handler = new MyHandler(this);

    /**
     * 功能名称
     */
    private SuggestItemView featureName;

    /**
     * 意图类型
     */
    private SuggestItemView intentType;

    /**
     * 置信度
     */
    private SuggestItemView confidence;

    /**
     * 创建时间
     */
    private SuggestItemView createTime;

    /**
     * 计划名称
     */
    private SuggestItemView planName;

    /**
     * 重复频率
     */
    private SuggestItemView recurrentFrequency;

    /**
     * 计划生效时间
     */
    private SuggestItemView recurrentBegin;

    /**
     * 计划失效时间
     */
    private SuggestItemView recurrentEnd;

    /**
     * 每周的周几
     */
    private PlanItemList daysOfTheWeek;

    /**
     * 每月的几号
     */
    private PlanItemList daysOfTheMonth;

    /**
     * 每年的几月几号
     */
    private PlanItemList daysOfTheYear;

    /**
     * 指定决定日期
     */
    private PlanItemList specifiedDay;

    /**
     * 开始时间
     */
    private PlanItemList beginTime;

    /**
     * 结束时间
     */
    private PlanItemList endTime;

    /**
     * 多次反馈计划次数
     */
    private SuggestItemView feedbackTimes;

    /**
     * 连续反馈次数
     */
    private SuggestItemView continueCount;

    /**
     * 连续反馈间隔
     */
    private SuggestItemView continuesGap;

    /**
     * 连续反馈按钮
     */
    private Button continueBtn;

    /**
     * FeedbackClient实例
     */
    private FeedbackClient feedbackClient = null;
    /**
     * Suggestion实例
     */
    private Suggestion suggestion = null;

    /**
     * 每年的几月几号数据项
     */
    private List<PlanFeedbackReq.DateInfo> yearList;

    /**
     * 指定决定日期数据项
     */
    private List<Long> specifiedList;

    /**
     * 计划开始时间数据项
     */
    private PlanFeedbackReq.TimeInfo beginTimeInfo;

    /**
     * 计划结束时间数据项
     */
    private PlanFeedbackReq.TimeInfo endTimeInfo;

    /**
     * 一周的数据，"周一", "周二", "周三", "周四", "周五", "周六", "周日"，用于弹窗显示
     */
    private final String[] daysOfWeek = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    /**
     * 一个月的数据，一月、二月、三月、......，用于弹窗显示
     */
    private String[] daysOfMonth;

    /**
     * 一周中哪些天已经选中，用于标识弹窗中已经选中的数据
     */
    private final boolean[] selectedWeekDays = new boolean[daysOfWeek.length];

    /**
     * 一个月中哪些天已经选中，用于标识弹窗中已经选中的数据
     */
    private boolean[] selectedMonthDays;

    /**
     * 每周的周几数据
     */
    protected final List<Integer> selectedWeekDaysList = new ArrayList<>();
    /**
     * 每月的几号
     */
    protected final List<Integer> selectedMonthDaysList = new ArrayList<>();

    /**
     * 每年的几月几号文案数据
     */
    protected final List<String> selectedDatesOfYearList = new ArrayList<>();

    /**
     * 指定决定日期文案数据
     */
    protected final List<String> specifiedDaysList = new ArrayList<>();
    /**
     * 连续反馈handler
     *
     * @since 2024-7-12
     */
    private static class MyHandler extends Handler {
        private final WeakReference<FeedbackPlanActivity> mActivity;

        private MyHandler(FeedbackPlanActivity feedbackPlanActivity) {
            super();
            this.mActivity = new WeakReference<FeedbackPlanActivity>(feedbackPlanActivity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (mActivity.get() == null) {
                return;
            }

            FeedbackPlanActivity activity = mActivity.get();
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        suggestion = Suggestion.getInstance(this.getApplicationContext());
        init();
    }

    @Override
    protected String getTopTitle() {
        return getString(R.string.feedbackPlan);
    }

    @Override
    protected int loadViewId() {
        return R.layout.activity_feedback_plan;
    }

    /**
     * 初始化
     */
    private void init() {
        // 名称
        featureName = findViewById(R.id.feature_name);
        initFeatureName();

        // 意图类型
        intentType = findViewById(R.id.intent_type);
        initIntentType();

        // 置信度
        confidence = findViewById(R.id.confidence);
        initConfidence();

        // 创建时间
        createTime = findViewById(R.id.create_time);
        initCreateTime();

        // 初始化时间默认值
        initDayTime();

        // 初始化参数控件
        initData();

        // HASFEATURE接口测试
        Button hasFeature = findViewById(R.id.has_feature);
        hasFeature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasFeatureTest();
            }
        });

        // 反馈计划
        Button feedBackPlan = findViewById(R.id.feedbackPlan_btn);
        feedBackPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedBack();
            }
        });

        // 多次反馈计划
        Button feedBackPlanMany = findViewById(R.id.feedbackPlan_btn_many);
        feedBackPlanMany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedbackManyTimes();
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
     * 初始化时间默认参数
     */
    private void initDayTime() {
        // 每周的周几默认数据
        selectedWeekDaysList.add(1);
        selectedWeekDaysList.add(2);
        selectedWeekDaysList.add(3);

        // 默认是否选中
        selectedWeekDays[0] = true;
        selectedWeekDays[1] = true;
        selectedWeekDays[2] = true;

        // 可供选择的月数据，每月的几号
        daysOfMonth = new String[31];
        for (int i = 0; i < 31; i++) {
            daysOfMonth[i] = i + 1 + "号";
        }
        // 每月的几号默认选中
        selectedMonthDays = new boolean[daysOfMonth.length];
        selectedMonthDays[0] = true;
        selectedMonthDays[1] = true;
        selectedMonthDays[2] = true;

        // 每月的几号默认数据
        selectedMonthDaysList.add(1);
        selectedMonthDaysList.add(2);
        selectedMonthDaysList.add(3);

        // 每年的几月几号默认数据
        selectedDatesOfYearList.add("07-08");
        selectedDatesOfYearList.add("07-09");
        selectedDatesOfYearList.add("07-10");

        // 指定决定日期默认数据
        specifiedDaysList.add("2024-07-08");
        specifiedDaysList.add("2024-07-09");
        specifiedDaysList.add("2024-07-10");
    }

    /**
     * 初始化参数控件
     */
    private void initData() {
        // 计划名称
        planName = findViewById(R.id.plan_name);
        initPlanName();

        // 重复频率
        recurrentFrequency = findViewById(R.id.recurrent_frequency);
        initRecurrentFrequency();

        // 计划生效时间
        recurrentBegin = findViewById(R.id.recurrent_begin);
        initRecurrentBegin();

        // 计划失效时间
        recurrentEnd = findViewById(R.id.recurrent_end);
        initRecurrentEnd();

        // 每周的周几
        daysOfTheWeek = findViewById(R.id.days_of_the_week);
        initDaysOfTheWeek();

        // 每月的几号
        daysOfTheMonth = findViewById(R.id.days_of_the_month);
        initDaysOfTheMonth();

        // 每年的几月几号
        daysOfTheYear = findViewById(R.id.days_of_the_year);
        initDaysOfTheYear();

        // 特别指定的几号
        specifiedDay = findViewById(R.id.specified_day);
        initSpecifiedDay();

        // 开始时间
        beginTime = findViewById(R.id.begin_time);
        initBeginTime();

        // 结束时间
        endTime = findViewById(R.id.end_time);
        initEndTime();

        // 多次反馈计划次数
        feedbackTimes = findViewById(R.id.feedbackPlan_times);
        initFeedbackTimes();

        // 连续反馈次数
        continueCount = findViewById(R.id.continuous_count);
        intiContinueCount();

        // 连续反馈间隔秒数
        continuesGap = findViewById(R.id.continuous_gap);
        initContinueGap();
    }

    /**
     * 功能名称
     */
    private void initFeatureName() {
        featureName.setTitle(getResources().getString(R.string.feature_name));
        featureName.setValue("FEEDBACK_PLAN");
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
        intentType.setTitle(getResources().getString(R.string.suggestion_kit_feedback_action_intentType));
        intentType.setValue("1030300001");
        intentType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(intentType);
            }
        });
    }

    /**
     * 置信度
     */
    private void initConfidence() {
        confidence.setTitle(getResources().getString(R.string.suggestion_confidence));
        confidence.setValue(INIT_VALUE);
        confidence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(confidence);
            }
        });
    }

    /**
     * 创建时间
     */
    private void initCreateTime() {
        createTime.setTitle(getResources().getString(R.string.suggestion_create_time));
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
     * 计划名称
     */
    private void initPlanName() {
        planName.setTitle(getResources().getString(R.string.suggestion_plan_name));
        planName.setValue("计划");
        planName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(planName);
            }
        });
    }

    /**
     * 重复频率
     */
    private void initRecurrentFrequency() {
        recurrentFrequency.setTitle(getResources().getString(R.string.suggestion_recurrent_frequency));
        recurrentFrequency.setValue("2");
        recurrentFrequency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(recurrentFrequency);
            }
        });
    }

    /**
     * 计划生效时间
     */
    private void initRecurrentBegin() {
        recurrentBegin.setTitle(getResources().getString(R.string.suggestion_recurrent_begin));
        recurrentBegin.setValue(String.valueOf(System.currentTimeMillis()));
        recurrentBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDayPickerDialog(new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(String time, int hour, int minute) {
                        recurrentBegin.setValue(time);
                    }
                });
            }
        });
    }

    /**
     * 计划失效时间
     */
    private void initRecurrentEnd() {
        recurrentEnd.setTitle(getResources().getString(R.string.suggestion_recurrent_end));
        long timeStamp = System.currentTimeMillis() + TWO_MONTH;
        Logger.info(TAG, "timeStamp: " + timeStamp);
        recurrentEnd.setValue(String.valueOf(timeStamp));
        recurrentEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDayPickerDialog(new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(String time, int hour, int minute) {
                        recurrentEnd.setValue(time);
                    }
                });
            }
        });
    }

    /**
     * 每周的周几
     */
    private void initDaysOfTheWeek() {
        daysOfTheWeek.setValueTitle(getResources().getString(R.string.days_of_the_week));
        daysOfTheWeek.setValueLineLong(selectedWeekDaysList);

        daysOfTheWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDaysOfWeekPickerDialog(new OnDaySelectListener() {
                    @Override
                    public void onDaySelect(List<Integer> selectedDaysList) {
                        daysOfTheWeek.setValueLineLong(selectedDaysList);
                    }
                });
            }
        });
    }

    /**
     * 每月的几号
     */
    private void initDaysOfTheMonth() {
        daysOfTheMonth.setValueTitle(getResources().getString(R.string.days_of_the_month));
        daysOfTheMonth.setValueLineLong(selectedMonthDaysList);
        daysOfTheMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDaysOfMonthPickerDialog(new OnDaySelectListener() {
                    @Override
                    public void onDaySelect(List<Integer> selectedDaysList) {
                        daysOfTheMonth.setValueLineLong(selectedDaysList);
                    }
                });
            }
        });
    }

    /**
     * 每年的几月几号
     */
    private void initDaysOfTheYear() {
        daysOfTheYear.setValueTitle(getResources().getString(R.string.days_of_the_year));
        daysOfTheYear.setValueLineString(selectedDatesOfYearList);
        daysOfTheYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMonthsOfYearPickerDialog();
            }
        });
    }

    /**
     * 指定决定日期
     */
    private void initSpecifiedDay() {
        specifiedDay.setValueTitle(getResources().getString(R.string.specified_day));
        specifiedDay.setValueLineString(specifiedDaysList);
        specifiedDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDaysOfYearPickerDialog();
            }
        });
    }

    /**
     * 计划开始时间
     */
    private void initBeginTime() {
        beginTimeInfo = new PlanFeedbackReq.TimeInfo();
        beginTime.setValueTitle(getResources().getString(R.string.suggestion_begin_time));
        beginTime.setValueRight("1:20");
        beginTime.setValueLongVisible(false);
        beginTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(String time, int hour, int minute) {
                        beginTime.setValueRight(hour + ":" + minute);
                        beginTimeInfo.setHour(hour);
                        beginTimeInfo.setMinute(minute);
                    }
                });
            }
        });
    }

    /**
     * 计划结束时间
     */
    private void initEndTime() {
        endTimeInfo = new PlanFeedbackReq.TimeInfo();
        endTime.setValueTitle(getResources().getString(R.string.suggestion_end_time));
        endTime.setValueRight("3:20");
        endTime.setValueLongVisible(false);
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(String time, int hour, int minute) {
                        endTime.setValueRight(hour + ":" + minute);
                        endTimeInfo.setHour(hour);
                        endTimeInfo.setMinute(minute);
                    }
                });
            }
        });
    }

    /**
     * 反馈计划次数
     */
    private void initFeedbackTimes() {
        feedbackTimes.setTitle(getResources().getString(R.string.feedback_plan_times));
        feedbackTimes.setValue(String.valueOf(INIT_RECURRENT_GAP));
        feedbackTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(feedbackTimes);
            }
        });
    }

    /**
     * 连续反馈次数
     */
    private void intiContinueCount() {
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
     * 连续反馈间隔毫秒数
     */
    private void initContinueGap() {
        continuesGap.setTitle(getResources().getString(R.string.feedback_continuous_gap));
        continuesGap.setValue(String.valueOf(INIT_RECURRENT_TYPE));
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
     * @param suggestItemView SuggestItemView
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
                if (suggestItemView.equals(feedbackTimes)
                        && Integer.parseInt(editText.getText().toString()) > MAX_TIMES) {
                    Toast.makeText(getApplicationContext(), R.string.time_max, Toast.LENGTH_LONG).show();
                } else {
                    suggestItemView.setValue(editText.getText().toString());
                    alertDialog.dismiss();
                }
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
        featureCheckReq.setFeatureName("FEEDBACK_PLAN");
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
            Log.e(TAG, "feedbackClient is null");
            return;
        }

        initListData();

        PlanFeedbackReq planFeedbackReq = feedbackData();
        boolean isFeedbackOk = feedbackClient.feedbackPrediction(planFeedbackReq, new FeedbackCallback() {
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
    }

    /**
     * 构造PlanFeedbackReq数据项
     *
     * @return PlanFeedbackReq数据项
     */
    private PlanFeedbackReq feedbackData() {
        PlanFeedbackReq planFeedbackReq = new PlanFeedbackReq();
        try {
            planFeedbackReq.setIntentType(intentType.getValue());
            planFeedbackReq.setPackageName(getPackageName());
            planFeedbackReq.setConfidence(Integer.parseInt(confidence.getValue()));
            planFeedbackReq.setCreateTime(Long.parseLong(createTime.getValue()));
            PlanFeedbackReq.PlanFeedbackData planFeedbackData = new PlanFeedbackReq.PlanFeedbackData();
            planFeedbackData.setPlanName(planName.getValue());
            planFeedbackData.setRecurrentFrequency(Integer.parseInt(recurrentFrequency.getValue()));
            planFeedbackData.setRecurrentBegin(Long.parseLong(recurrentBegin.getValue()));
            planFeedbackData.setRecurrentEnd(Long.parseLong(recurrentEnd.getValue()));
            planFeedbackData.setDaysOfTheWeek(selectedWeekDaysList);
            planFeedbackData.setDaysOfTheMonth(selectedMonthDaysList);
            planFeedbackData.setDaysOfTheYear(yearList);
            planFeedbackData.setSpecifiedDays(specifiedList);
            planFeedbackData.setBeginTime(beginTimeInfo);
            planFeedbackData.setEndTime(endTimeInfo);
            List<PlanFeedbackReq.PlanFeedbackData> feedbackDataList = new ArrayList<>();
            feedbackDataList.add(planFeedbackData);
            planFeedbackReq.setPlanFeedbackDatas(feedbackDataList);
        } catch (NumberFormatException exception){
            Logger.error(TAG, "参数格式不对");
            Toast.makeText(this, "参数格式不对", Toast.LENGTH_SHORT).show();
        }
        return planFeedbackReq;
    }

    /**
     * 多次反馈
     */
    private void feedbackManyTimes() {
        getFeedbackClient();
        if (feedbackClient == null) {
            Logger.error(TAG, "feedbackClient is null");
            return;
        }

        initListData();

        try {
            PlanFeedbackReq planFeedbackReq = new PlanFeedbackReq();
            planFeedbackReq.setIntentType(intentType.getValue());
            planFeedbackReq.setPackageName(getPackageName());
            planFeedbackReq.setConfidence(Integer.parseInt(confidence.getValue()));
            planFeedbackReq.setCreateTime(Long.parseLong(createTime.getValue()));
            List<PlanFeedbackReq.PlanFeedbackData> feedbackDataList = new ArrayList<>();
            int feedbackPlanTimes = Integer.parseInt(feedbackTimes.getValue());

            for (int index = 0; index < feedbackPlanTimes; index++) {
                PlanFeedbackReq.PlanFeedbackData planFeedbackData = new PlanFeedbackReq.PlanFeedbackData();
                planFeedbackData.setPlanName("plan" + (index + 1));
                planFeedbackData.setRecurrentFrequency(Integer.parseInt(recurrentFrequency.getValue()));
                planFeedbackData.setRecurrentBegin(Long.parseLong(recurrentBegin.getValue()));
                planFeedbackData.setRecurrentEnd(Long.parseLong(recurrentEnd.getValue()));
                planFeedbackData.setDaysOfTheWeek(selectedWeekDaysList);
                planFeedbackData.setDaysOfTheMonth(selectedMonthDaysList);
                planFeedbackData.setDaysOfTheYear(yearList);
                planFeedbackData.setSpecifiedDays(specifiedList);
                planFeedbackData.setBeginTime(beginTimeInfo);
                planFeedbackData.setEndTime(endTimeInfo);
                feedbackDataList.add(planFeedbackData);
            }
            planFeedbackReq.setPlanFeedbackDatas(feedbackDataList);

            feedbackDataList(planFeedbackReq);
        } catch (NumberFormatException exception) {
            Logger.error(TAG, "参数格式不对");
            Toast.makeText(this, "参数格式不对", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 反馈
     *
     * @param planFeedbackReq 数据项
     */
    private void feedbackDataList(PlanFeedbackReq planFeedbackReq) {
        boolean isFeedbackOk = feedbackClient.feedbackPrediction(planFeedbackReq, new FeedbackCallback() {
            @Override
            public void onResult(int resultCode) {
                Log.i(TAG, "resultCode = " + resultCode);
                String msg = getFeedBackResultMsg(resultCode);
                showResult(msg);
                Log.i(TAG, msg);
            }
        });
        if (!isFeedbackOk) {
            Log.i(TAG, getResources().getString(R.string.suggestion_feedback_failed));
            showResult(getResources().getString(R.string.suggestion_feedback_failed));
        }
    }

    /**
     * 设置时间参数
     */
    private void initListData() {
        // 每年的几月几号
        yearList = new ArrayList<>();
        for (String date : selectedDatesOfYearList) {
            String[] info = date.split("-");
            PlanFeedbackReq.DateInfo dateInfo = new PlanFeedbackReq.DateInfo();
            dateInfo.setMonthOfTheYear(Integer.parseInt(info[0]));
            dateInfo.setDaysOfTheMonth(Integer.parseInt(info[1]));
            yearList.add(dateInfo);
        }
        Logger.info(TAG, "yearList: " + yearList);

        // 指定决定日期
        specifiedList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (String date : specifiedDaysList) {
            String[] info = date.split("-");
            calendar.set(Integer.parseInt(info[0]), Integer.parseInt(info[1]), Integer.parseInt(info[2]));
            specifiedList.add(calendar.getTimeInMillis());
        }
        Log.i(TAG, "feedbackPlan begin.");
    }

    /**
     * 选择每周的几号，选择周几的弹窗
     *
     * @param onDaySelectListener Listener
     */
    private void showDaysOfWeekPickerDialog(OnDaySelectListener onDaySelectListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.days_of_the_week));

        builder.setMultiChoiceItems(daysOfWeek, selectedWeekDays, (dialog, which, isChecked) -> {
            if (isChecked) {
                selectedWeekDaysList.add(which + 1);
            } else {
                selectedWeekDaysList.remove(Integer.valueOf(which + 1));
            }
        });

        builder.setPositiveButton("确定", (dialog, which) -> {
            onDaySelectListener.onDaySelect(selectedWeekDaysList);
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 选择每月的几号，展示一个月中的日期选择弹窗
     *
     * @param onDaySelectListener Listener
     */
    private void showDaysOfMonthPickerDialog(OnDaySelectListener onDaySelectListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.days_of_the_month));

        builder.setMultiChoiceItems(daysOfMonth, selectedMonthDays, (dialog, which, isChecked) -> {
            if (isChecked) {
                selectedMonthDaysList.add(which + 1);
            } else {
                selectedMonthDaysList.remove(Integer.valueOf(which + 1));
            }
        });

        builder.setPositiveButton("确定", (dialog, which) -> {
            onDaySelectListener.onDaySelect(selectedMonthDaysList);
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 选择每年的几月几号，展示月份与日期选择弹窗
     */
    private void showMonthsOfYearPickerDialog() {
        // 创建自定义布局
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_date_picker, null);
        DatePicker datePicker = dialogView.findViewById(R.id.datePicker);

        // 隐藏年份选择器，只显示月份和日期
        datePicker.findViewById(getResources().getIdentifier("year", "id", "android")).setVisibility(View.GONE);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.days_of_the_year)).setView(dialogView);

        builder.setPositiveButton("添加", (dialog, which) -> {
            int month = datePicker.getMonth() + 1;
            int day = datePicker.getDayOfMonth();
            String selectedDate = String.format(Locale.CANADA, "%02d-%02d", month, day);
            if (selectedDatesOfYearList.size() >= 20) {
                Toast.makeText(this, "最多只能选择20个", Toast.LENGTH_SHORT).show();
            } else if (!selectedDatesOfYearList.contains(selectedDate)) {
                selectedDatesOfYearList.add(selectedDate);
            }
            // 展示选择的时间
            daysOfTheYear.setValueLineString(selectedDatesOfYearList);
        });

        builder.setNegativeButton("移除", (dialog, which) -> {
            int month = datePicker.getMonth() + 1;
            int day = datePicker.getDayOfMonth();
            String selectedDate = String.format(Locale.CANADA, "%02d-%02d", month, day);
            selectedDatesOfYearList.remove(selectedDate);
            daysOfTheYear.setValueLineString(selectedDatesOfYearList);
        });
        builder.create().show();
    }

    /**
     * 指定决定日期，展示年份月份与日期选择弹窗
     */
    private void showDaysOfYearPickerDialog() {
        // 创建自定义布局
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_date_picker, null);
        DatePicker datePicker = dialogView.findViewById(R.id.datePicker);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.specified_day)).setView(dialogView);

        builder.setPositiveButton("添加", (dialog, which) -> {
            int year = datePicker.getYear();
            int month = datePicker.getMonth() + 1;
            int day = datePicker.getDayOfMonth();
            String selectedDate = String.format(Locale.CANADA, "%02d-%02d-%02d", year, month, day);
            if (specifiedDaysList.size() >= 20) {
                Toast.makeText(this, "最多只能选择20个", Toast.LENGTH_SHORT).show();
            } else if (!specifiedDaysList.contains(selectedDate)) {
                specifiedDaysList.add(selectedDate);
            }
            // 展示选择的时间
            specifiedDay.setValueLineString(specifiedDaysList);
        });

        builder.setNegativeButton("移除", (dialog, which) -> {
            int year = datePicker.getYear();
            int month = datePicker.getMonth() + 1;
            int day = datePicker.getDayOfMonth();
            String selectedDate = String.format(Locale.CANADA, "%02d-%02d-%02d", year, month, day);
            specifiedDaysList.remove(selectedDate);
            specifiedDay.setValueLineString(specifiedDaysList);
        });
        builder.create().show();
    }
}
