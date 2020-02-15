package com.github.caiiiycuk.ruvote.screen;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.facebook.litho.ClickEvent;
import com.facebook.litho.Column;
import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.Row;
import com.facebook.litho.StateValue;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnAttached;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.annotations.OnDetached;
import com.facebook.litho.annotations.OnEvent;
import com.facebook.litho.annotations.OnUpdateState;
import com.facebook.litho.annotations.Param;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.annotations.State;
import com.facebook.litho.widget.Image;
import com.facebook.litho.widget.Text;
import com.facebook.yoga.YogaAlign;
import com.facebook.yoga.YogaEdge;
import com.facebook.yoga.YogaJustify;
import com.facebook.yoga.YogaPositionType;
import com.github.caiiiycuk.ruvote.R;
import com.github.caiiiycuk.ruvote.cv.ROIRenderer;
import com.github.caiiiycuk.ruvote.ui.Ui;
import com.github.caiiiycuk.ruvote.ui.widget.ProgressWheel;
import com.github.caiiiycuk.ruvote.ui.widget.Title;

import java.util.concurrent.Executor;

@LayoutSpec
public class RoiScreenSpec {

    @OnCreateLayout
    static Component onCreateLayout(ComponentContext c,
                                    @Prop Bitmap roiBitmap,
                                    @State Bitmap roiPolygon,
                                    @State int method) {
        return Column.create(c)
                .child(Title.create(c)
                        .textRes(R.string.select_method)
                        .build())
                .child(Row.create(c)
                        .justifyContent(YogaJustify.SPACE_EVENLY)
                        .alignItems(YogaAlign.CENTER)
                        .backgroundRes(R.color.colorPrimary)
                        .paddingRes(YogaEdge.HORIZONTAL, R.dimen.ident)
                        .paddingRes(YogaEdge.VERTICAL, R.dimen.identHalf)
                        .child(Text.create(c)
                                .paddingRes(YogaEdge.ALL, R.dimen.border)
                                .backgroundRes(method == ROIRenderer.METHOD_SOFT ? R.color.colorSelected : R.color.colorPrimary)
                                .textSizeRes(R.dimen.method_text_size)
                                .textColorRes(R.color.neutralTextColor)
                                .textRes(R.string.method_soft)
                                .clickHandler(RoiScreen.onMethodClick(c, ROIRenderer.METHOD_SOFT))
                                .build())
                        .child(Text.create(c)
                                .paddingRes(YogaEdge.ALL, R.dimen.border)
                                .backgroundRes(method == ROIRenderer.METHOD_HARD ? R.color.colorSelected : R.color.colorPrimary)
                                .textSizeRes(R.dimen.method_text_size)
                                .textColorRes(R.color.neutralTextColor)
                                .textRes(R.string.method_hard)
                                .clickHandler(RoiScreen.onMethodClick(c, ROIRenderer.METHOD_HARD))
                                .build())
                        .build())
                .child(Image.create(c)
                        .flexGrow(1)
                        .scaleType(ImageView.ScaleType.FIT_CENTER)
                        .drawable(new BitmapDrawable(c.getResources(),
                                roiPolygon == null || roiPolygon.isRecycled() ? roiBitmap : roiPolygon))
                        .build())
                .child(roiPolygon == null || roiPolygon.isRecycled() ? Row.create(c)
                        .backgroundRes(R.color.modalBackground)
                        .positionType(YogaPositionType.ABSOLUTE)
                        .positionPx(YogaEdge.ALL, 0)
                        .justifyContent(YogaJustify.CENTER)
                        .alignItems(YogaAlign.CENTER)
                        .child(ProgressWheel.create(c)
                                .radiusPx(Ui.getPx(R.dimen.modal_loader_size))
                                .build())
                        .build() : null)
                .build();
    }

    @OnAttached
    static void onAttached(ComponentContext c,
                           @Prop Executor executor,
                           @Prop Bitmap roiBitmap,
                           @State int method) {
        updateRoi(c, executor, roiBitmap, method);
    }

    @OnDetached
    static void onDetached(ComponentContext c, @State Bitmap royPolygon) {
        if (royPolygon != null) {
            royPolygon.recycle();
        }
    }

    @OnUpdateState
    static void setRoiPolygon(StateValue<Bitmap> roiPolygon, @Param Bitmap newBitmap) {
        if (roiPolygon.get() != null) {
            roiPolygon.get().recycle();
        }

        roiPolygon.set(newBitmap);
    }

    @OnUpdateState
    static void setMethod(StateValue<Integer> method,
                          @Param int newMethod,
                          @Param Executor pExecutor,
                          @Param Bitmap pRoiBitmap,
                          @Param ComponentContext pContext) {
        if (method.get() == newMethod) {
            return;
        }

        method.set(newMethod);
        updateRoi(pContext, pExecutor, pRoiBitmap, newMethod);
    }

    @OnEvent(ClickEvent.class)
    static void onMethodClick(ComponentContext c,
                              @Prop Executor executor,
                              @Prop Bitmap roiBitmap,
                              @Param int newMethod) {
        RoiScreen.setMethod(c, newMethod, executor, roiBitmap, c);
    }

    private static void updateRoi(ComponentContext c, Executor executor, Bitmap roiBitmap, int method) {
        executor.execute(() -> {
            Bitmap roiPolygon = ROIRenderer.renderRoi(roiBitmap, method);
            RoiScreen.setRoiPolygon(c, roiPolygon);
        });
    }
}
