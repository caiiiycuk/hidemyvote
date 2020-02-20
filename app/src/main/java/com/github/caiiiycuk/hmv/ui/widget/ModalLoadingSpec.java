package com.github.caiiiycuk.hmv.ui.widget;

import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.Row;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.yoga.YogaAlign;
import com.facebook.yoga.YogaEdge;
import com.facebook.yoga.YogaJustify;
import com.facebook.yoga.YogaPositionType;
import com.github.caiiiycuk.hmv.R;
import com.github.caiiiycuk.hmv.ui.Ui;

@LayoutSpec
public class ModalLoadingSpec {
    @OnCreateLayout
    static Component onCreateLayout(ComponentContext c) {
        return Row.create(c)
                .backgroundRes(R.color.modalBackground)
                .positionType(YogaPositionType.ABSOLUTE)
                .positionPx(YogaEdge.ALL, 0)
                .justifyContent(YogaJustify.CENTER)
                .alignItems(YogaAlign.CENTER)
                .child(ProgressWheel.create(c)
                        .radiusPx(Ui.getPx(R.dimen.modal_loader_size))
                        .build())
                .build();
    }
}
