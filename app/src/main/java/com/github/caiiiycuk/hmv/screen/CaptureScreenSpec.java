package com.github.caiiiycuk.hmv.screen;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import com.facebook.litho.ClickEvent;
import com.facebook.litho.Column;
import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.EventHandler;
import com.facebook.litho.Row;
import com.facebook.litho.StateValue;
import com.facebook.litho.Transition;
import com.facebook.litho.animation.AnimatedProperties;
import com.facebook.litho.annotations.FromEvent;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.annotations.OnCreateTransition;
import com.facebook.litho.annotations.OnEvent;
import com.facebook.litho.annotations.OnUpdateState;
import com.facebook.litho.annotations.State;
import com.facebook.litho.widget.Image;
import com.facebook.yoga.YogaEdge;
import com.facebook.yoga.YogaPositionType;
import com.github.caiiiycuk.hmv.R;
import com.github.caiiiycuk.hmv.ui.Ui;
import com.github.caiiiycuk.hmv.ui.widget.FAB;
import com.github.caiiiycuk.hmv.ui.widget.FABSpec;
import com.github.caiiiycuk.hmv.ui.widget.Title;

@LayoutSpec(events = ClickEvent.class)
public class CaptureScreenSpec {

    @OnCreateLayout
    static Component onCreateLayout(ComponentContext c,
                                    @State boolean hidden) {
        return Column.create(c)
                .child(Title.create(c)
                        .textRes(R.string.capture_title)
                        .build())
                .child(FAB.create(c)
                        .align(FABSpec.RIGHT)
                        .scale(hidden ? 0.0f : 1.0f)
                        .transitionKey("fabScale")
                        .drawableRes(R.drawable.camera)
                        .clickHandler(CaptureScreen.onFabClick(c))
                        .build())
                .child(hidden ? Row.create(c)
                        .backgroundColor(Color.argb(30, 0, 0, 0))
                        .positionType(YogaPositionType.ABSOLUTE)
                        .positionPx(YogaEdge.ALL, 0)
                        .build() : null)
                .build();
    }

    @OnCreateTransition
    static Transition onCreateTransition(ComponentContext c) {
        return Transition.create("fabScale")
                .animate(AnimatedProperties.SCALE);
    }

    @OnUpdateState
    static void setFabHidden(StateValue<Boolean> hidden) {
        hidden.set(true);
    }

    @OnEvent(ClickEvent.class)
    static void onFabClick(ComponentContext c, @FromEvent View view) {
        CaptureScreen.setFabHidden(c);

        EventHandler handler = CaptureScreen.getClickEventHandler(c);
        if (handler != null) {
            CaptureScreen.dispatchClickEvent(handler, view);
        }
    }

}
