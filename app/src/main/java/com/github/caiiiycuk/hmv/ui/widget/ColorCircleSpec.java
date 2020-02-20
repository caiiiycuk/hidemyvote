package com.github.caiiiycuk.hmv.ui.widget;

import androidx.annotation.ColorRes;

import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.Row;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.annotations.Prop;
import com.facebook.yoga.YogaAlign;
import com.facebook.yoga.YogaEdge;
import com.facebook.yoga.YogaPositionType;
import com.github.caiiiycuk.hmv.R;
import com.github.caiiiycuk.hmv.ui.Ui;

@LayoutSpec
public class ColorCircleSpec {
    @OnCreateLayout
    static Component onCreateLayout(ComponentContext c,
                                    @Prop @ColorRes int colorRes,
                                    @Prop boolean active) {
        return Row.create(c)
                .alignContent(YogaAlign.CENTER)
                .alignItems(YogaAlign.CENTER)
                .aspectRatio(1.0f)
                .heightPercent(100)
                .background(active ? Ui.circle(R.color.neutralTextColor) : Ui.circle(colorRes))
                .child(active ? Row.create(c)
                        .background(Ui.circle(colorRes))
                        .positionType(YogaPositionType.ABSOLUTE)
                        .positionRes(YogaEdge.ALL, R.dimen.border)
                        .build() : null)
                .build();
    }
}
