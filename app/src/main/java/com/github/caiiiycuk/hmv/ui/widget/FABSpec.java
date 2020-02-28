package com.github.caiiiycuk.hmv.ui.widget;

import android.widget.ImageView;

import androidx.annotation.DrawableRes;

import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.Row;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.widget.Image;
import com.facebook.yoga.YogaEdge;
import com.facebook.yoga.YogaPositionType;
import com.github.caiiiycuk.hmv.R;
import com.github.caiiiycuk.hmv.ui.Ui;

@LayoutSpec
public class FABSpec {

    public static final int LEFT = 0;
    public static final int RIGHT = 1;

    @OnCreateLayout
    static Component onCreateLayout(ComponentContext c,
                                    @Prop @DrawableRes int drawableRes,
                                    @Prop int align,
                                    @Prop(optional=true) boolean stickToTop) {
        Row.Builder builder = Row.create(c)
                .child(Image.create(c)
                        .background(Ui.circle(R.color.colorPrimaryDark))
                        .paddingRes(YogaEdge.ALL, R.dimen.ident)
                        .drawableRes(drawableRes)
                        .widthRes(R.dimen.icon_size)
                        .aspectRatio(1.0f)
                        .scaleType(ImageView.ScaleType.FIT_CENTER)
                        .build())
                .positionType(YogaPositionType.ABSOLUTE)
                .positionPercent(align == LEFT ? YogaEdge.LEFT : YogaEdge.RIGHT, 5);

        if (stickToTop) {
            builder.positionPx(YogaEdge.TOP, Ui.getPx(R.dimen.title_height) +
                    Ui.getPx(R.dimen.tint_height) + Ui.getPx(R.dimen.ident));
        } else {
            builder.positionPx(YogaEdge.BOTTOM, Ui.getPx(R.dimen.ident) * 2);
        }

        return builder.build();
    }
}
