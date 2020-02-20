package com.github.caiiiycuk.hmv.screen;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.facebook.litho.ClickEvent;
import com.facebook.litho.Column;
import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.StateValue;
import com.facebook.litho.TouchEvent;
import com.facebook.litho.VisibilityChangedEvent;
import com.facebook.litho.annotations.FromEvent;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.annotations.OnEvent;
import com.facebook.litho.annotations.OnUpdateState;
import com.facebook.litho.annotations.Param;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.annotations.State;
import com.facebook.litho.widget.Image;
import com.github.caiiiycuk.hmv.R;
import com.github.caiiiycuk.hmv.activity.Router;
import com.github.caiiiycuk.hmv.ui.Ui;
import com.github.caiiiycuk.hmv.ui.widget.FAB;
import com.github.caiiiycuk.hmv.ui.widget.FABSpec;
import com.github.caiiiycuk.hmv.ui.widget.Title;

@LayoutSpec
public class SelectionScreenSpec {

    @OnCreateLayout
    static Component onCreateLayout(ComponentContext c,
                                    @Prop Bitmap bitmap,
                                    @State int width,
                                    @State int height) {
        return Column.create(c)
                .child(Title.create(c)
                        .textRes(R.string.tap_on_vote_box)
                        .build())
                .child(Image.create(c)
                        .flexGrow(1)
                        .scaleType(ImageView.ScaleType.FIT_CENTER)
                        .drawable(new BitmapDrawable(c.getResources(), bitmap))
                        .touchHandler(SelectionScreen.onClick(c))
                        .visibilityChangedHandler(SelectionScreen.onComponentVisibilityChanged(c))
                        .build())
                .child(FAB.create(c)
                        .drawableRes(R.drawable.back)
                        .align(FABSpec.LEFT)
                        .clickHandler(SelectionScreen.onBackClick(c))
                        .build())
                .build();
    }

    @OnEvent(TouchEvent.class)
    static boolean onClick(ComponentContext c,
                           @FromEvent MotionEvent motionEvent,
                           @State int width, @State int height,
                           @Prop Bitmap bitmap,
                           @Prop Router router) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            float x = motionEvent.getAxisValue(MotionEvent.AXIS_X);
            float y = motionEvent.getAxisValue(MotionEvent.AXIS_Y);
            Point bitmapPoint = Ui.mapFitCenterImagePointToBitmapPoint(x, y, width, height, bitmap);
            router.openRoiActivity(bitmapPoint.x, bitmapPoint.y);
        }

        return true;
    }

    @OnEvent(VisibilityChangedEvent.class)
    static void onComponentVisibilityChanged(
            ComponentContext c,
            @FromEvent int visibleHeight,
            @FromEvent int visibleWidth) {
        SelectionScreen.setSize(c, visibleWidth, visibleHeight);
    }

    @OnUpdateState
    static void setSize(StateValue<Integer> width, StateValue<Integer> height,
                        @Param int newWidth, @Param int newHeight) {
        if (newWidth != width.get() || newHeight != height.get()) {
            width.set(newWidth);
            height.set(newHeight);
        }
    }

    @OnEvent(ClickEvent.class)
    static void onBackClick(ComponentContext c, @Prop Router router) {
        router.back();
    }

}
