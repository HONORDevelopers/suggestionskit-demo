/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.suggestionkittool.view;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.hihonor.demo.suggestionkittool.R;

/**
 * 计划反馈界面的自定义view
 *
 * @author t00031915
 * @since 2024-07-9
 */
public class PlanItemList extends LinearLayout {
    private TextView valueTitle;

    private TextView valueRight;

    private TextView valueLine;

    private final String space = "\u0020\u0020";

    /**
     * 构造方法PlanItemList
     *
     * @param context
     */
    public PlanItemList(Context context) {
        super(context);
        initView(context);
    }

    /**
     * 构造方法PlanItemList
     *
     * @param context
     * @param attrs
     */
    public PlanItemList(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    /**
     * 构造方法PlanItemList
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public PlanItemList(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.plan_item_list, this);
        valueTitle = findViewById(R.id.title);
        valueRight = findViewById(R.id.value);
        valueLine = findViewById(R.id.value_long);
    }

    public void setValueTitle(String title) {
        valueTitle.setText(title);
    }

    public void setValueRight(String valueRight) {
        this.valueRight.setText(valueRight);
    }

    public void setValueLineLong(List<Integer> values) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            builder.append(values.get(i));
            if (i != values.size() - 1) {
                builder.append(space);
            }
        }
        this.valueLine.setText(builder.toString());
    }

    public void setValueLineString(List<String> values) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            builder.append(values.get(i));
            if (i != values.size() - 1) {
                builder.append(space);
            }
        }
        this.valueLine.setText(builder.toString());
    }

    public void setValueLongVisible(boolean isVisible) {
        this.valueLine.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
}
