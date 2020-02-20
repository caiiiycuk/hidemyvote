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
import com.facebook.litho.VisibilityChangedEvent;
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
import com.github.caiiiycuk.ruvote.activity.Router;
import com.github.caiiiycuk.ruvote.cv.ROI;
import com.github.caiiiycuk.ruvote.cv.ROICalculator;
import com.github.caiiiycuk.ruvote.ui.Ui;
import com.github.caiiiycuk.ruvote.ui.widget.ProgressWheel;
import com.github.caiiiycuk.ruvote.ui.widget.Title;

import java.util.concurrent.Executor;

@LayoutSpec
public class RoiScreenSpec {

    @OnCreateLayout
    static Component onCreateLayout(ComponentContext c,
                                    @Prop Bitmap bitmap,
                                    @State ROI roi,
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
                                .backgroundRes(method == ROICalculator.METHOD_SOFT ? R.color.colorSelected : R.color.colorPrimary)
                                .textSizeRes(R.dimen.method_text_size)
                                .textColorRes(R.color.neutralTextColor)
                                .textRes(R.string.method_soft)
                                .clickHandler(RoiScreen.onMethodClick(c, ROICalculator.METHOD_SOFT))
                                .build())
                        .child(Text.create(c)
                                .paddingRes(YogaEdge.ALL, R.dimen.border)
                                .backgroundRes(method == ROICalculator.METHOD_HARD ? R.color.colorSelected : R.color.colorPrimary)
                                .textSizeRes(R.dimen.method_text_size)
                                .textColorRes(R.color.neutralTextColor)
                                .textRes(R.string.method_hard)
                                .clickHandler(RoiScreen.onMethodClick(c, ROICalculator.METHOD_HARD))
                                .build())
                        .build())
                .child(Image.create(c)
                        .flexGrow(1)
                        .scaleType(ImageView.ScaleType.FIT_CENTER)
                        .drawable(new BitmapDrawable(c.getResources(),
                                roi == null ? bitmap : roi.bitmap))
                        .build())
                .child(roi == null ? Row.create(c)
                        .backgroundRes(R.color.modalBackground)
                        .positionType(YogaPositionType.ABSOLUTE)
                        .positionPx(YogaEdge.ALL, 0)
                        .justifyContent(YogaJustify.CENTER)
                        .alignItems(YogaAlign.CENTER)
                        .child(ProgressWheel.create(c)
                                .radiusPx(Ui.getPx(R.dimen.modal_loader_size))
                                .build())
                        .build() : null)
                .child(Image.create(c)
                        .background(Ui.circle(R.color.colorPrimaryDark))
                        .paddingRes(YogaEdge.ALL, R.dimen.ident)
                        .drawableRes(android.R.drawable.ic_media_ff)
                        .widthRes(R.dimen.icon_size)
                        .aspectRatio(1.0f)
                        .scaleType(ImageView.ScaleType.FIT_CENTER)
                        .positionType(YogaPositionType.ABSOLUTE)
                        .positionPercent(YogaEdge.RIGHT, 10)
                        .positionPercent(YogaEdge.BOTTOM, 10)
                        .clickHandler(RoiScreen.onForwardClick(c))
                        .build())
                .build();
    }

    @OnAttached
    static void onAttached(ComponentContext c,
                           @Prop Executor executor,
                           @Prop Bitmap bitmap,
                           @State int method) {
        updateRoi(c, executor, bitmap, method);
    }

    @OnDetached
    static void onDetached(ComponentContext c, @State ROI roi) {
        if (roi != null) {
            Ui.post(roi::recycle);
        }
    }

    @OnUpdateState
    static void setRoi(StateValue<ROI> roi,
                       @Param ROI newRoi) {
        ROI prevRoi = roi.get();
        roi.set(newRoi);

        if (prevRoi != null) {
            Ui.post(prevRoi::recycle);
        }
    }

    @OnUpdateState
    static void setMethod(StateValue<Integer> method,
                          @Param int newMethod,
                          @Param Executor pExecutor,
                          @Param Bitmap pBitmap,
                          @Param ComponentContext pContext) {
        if (method.get() == newMethod) {
            return;
        }

        method.set(newMethod);
        updateRoi(pContext, pExecutor, pBitmap, newMethod);
    }

    @OnEvent(ClickEvent.class)
    static void onMethodClick(ComponentContext c,
                              @Prop Executor executor,
                              @Prop Bitmap bitmap,
                              @Param int newMethod) {
        RoiScreen.setMethod(c, newMethod, executor, bitmap, c);
    }

    private static void updateRoi(ComponentContext c, Executor executor, Bitmap bitmap, int method) {
        executor.execute(() -> {
            ROI roi = ROICalculator.calculate(bitmap, method);
            RoiScreen.setRoi(c, roi);
        });
    }

    @OnEvent(ClickEvent.class)
    static void onForwardClick(ComponentContext c,
                               @State ROI roi,
                               @Prop int offsetX,
                               @Prop int offsetY,
                               @Prop Router router) {
        router.openResultActivity((int) (offsetX + roi.left),
                (int) (offsetY + roi.top),
                roi.color,
                roi.roiMark);
    }
}
