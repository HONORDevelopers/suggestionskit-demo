/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.suggestionkittool.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.hihonor.demo.suggestionkittool.R;

/**
 * 订单反馈页面的自定义view
 *
 * @author t00031915
 * @since 2024-07-9
 */
public class SuggestItemView extends LinearLayout {

    private TextView titleTv;

    private TextView valueTv;
    private TextView description;
    private TextView notNull;

    public SuggestItemView(Context context) {
        super(context);
        initView(context);
    }

    public SuggestItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SuggestItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.suggest_item_view, this);
        titleTv = findViewById(R.id.title);
        valueTv = findViewById(R.id.value);
        description = findViewById(R.id.description);
        notNull = findViewById(R.id.not_null);
    }

    public void setDescription(String descriptionValue) {
        description.setVisibility(View.VISIBLE);
        description.setText(descriptionValue);
    }


    public void setNotNull() {
        notNull.setVisibility(View.VISIBLE);
    }

    public void setTitle(String title) {
        titleTv.setText(title);
    }

    public String getTitle() {
        return titleTv.getText().toString();
    }

    public void setValue(String value) {
        valueTv.setText(value);
    }

    public String getValue() {
        return valueTv.getText().toString();
    }
}
