package com.github.caiiiycuk.hmv.screen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.litho.ClickEvent;
import com.facebook.litho.Column;
import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.Row;
import com.facebook.litho.StateValue;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnAttached;
import com.facebook.litho.annotations.OnCreateInitialState;
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
import com.github.caiiiycuk.hmv.R;
import com.github.caiiiycuk.hmv.activity.Router;
import com.github.caiiiycuk.hmv.cv.ROI;
import com.github.caiiiycuk.hmv.cv.ROICalculator;
import com.github.caiiiycuk.hmv.ui.Ui;
import com.github.caiiiycuk.hmv.ui.widget.FAB;
import com.github.caiiiycuk.hmv.ui.widget.FABSpec;
import com.github.caiiiycuk.hmv.ui.widget.ModalLoading;
import com.github.caiiiycuk.hmv.ui.widget.Title;

import java.util.concurrent.Executor;

@LayoutSpec
public class RoiScreenSpec {

    @OnCreateInitialState
    static void onCreateInitialState(ComponentContext c, StateValue<Boolean> modal) {
        modal.set(true);
    }

    @OnCreateLayout
    static Component onCreateLayout(ComponentContext c,
                                    @Prop Bitmap bitmap,
                                    @State ROI roi,
                                    @State int method,
                                    @State boolean modal) {
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
                .child(FAB.create(c)
                        .align(FABSpec.LEFT)
                        .drawableRes(R.drawable.back)
                        .clickHandler(RoiScreen.onBackClick(c))
                        .build())
                .child(roi == null ? null : FAB.create(c)
                        .align(FABSpec.RIGHT)
                        .drawableRes(R.drawable.done)
                        .clickHandler(RoiScreen.onForwardClick(c))
                        .build())
                .child(roi == null && modal ? ModalLoading.create(c).build() : null)
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

            if (roi == null) {
                Ui.post(() -> {
                    Context context = c.getAndroidContext();
                    Toast.makeText(context,
                            context.getResources().getText(R.string.roi_not_found), Toast.LENGTH_LONG)
                            .show();
                });
                RoiScreen.setModal(c, false);
            } else {
                RoiScreen.setRoi(c, roi);
            }
        });
    }

    @OnEvent(ClickEvent.class)
    static void onBackClick(ComponentContext c, @Prop Router router) {
        router.back();
    }

    @OnEvent(ClickEvent.class)
    static void onForwardClick(ComponentContext c,
                               @State ROI roi,
                               @Prop int offsetX,
                               @Prop int offsetY,
                               @Prop Router router) {
        if (roi == null) {
            return;
        }

        router.openResultActivity((int) (offsetX + roi.left),
                (int) (offsetY + roi.top),
                roi.color,
                roi.markWidth,
                roi.markHeight,
                roi.markAngle);
    }

    @OnUpdateState
    static void setModal(StateValue<Boolean> modal, @Param boolean value) {
        modal.set(value);
    }
}
