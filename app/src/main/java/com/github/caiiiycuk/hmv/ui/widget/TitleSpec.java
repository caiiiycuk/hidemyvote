package com.github.caiiiycuk.hmv.ui.widget;


import androidx.annotation.StringRes;

import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.Row;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.widget.Text;
import com.facebook.yoga.YogaAlign;
import com.facebook.yoga.YogaJustify;
import com.github.caiiiycuk.hmv.R;

@LayoutSpec
public class TitleSpec {

    @OnCreateLayout
    static Component onCreateLayout(ComponentContext c,
                                    @Prop @StringRes int textRes) {
        return Row.create(c)
                .alignItems(YogaAlign.CENTER)
                .justifyContent(YogaJustify.CENTER)
                .heightRes(R.dimen.title_height)
                .backgroundRes(R.color.colorPrimaryDark)
                .child(Text.create(c)
                        .textRes(textRes)
                        .textSizeRes(R.dimen.title_text_size)
                        .textColorRes(R.color.neutralTextColor)
                        .build())
                .build();
    }
}
