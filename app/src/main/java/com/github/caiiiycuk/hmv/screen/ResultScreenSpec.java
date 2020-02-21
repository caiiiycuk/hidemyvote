package com.github.caiiiycuk.hmv.screen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

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
import com.facebook.yoga.YogaAlign;
import com.facebook.yoga.YogaEdge;
import com.facebook.yoga.YogaJustify;
import com.github.caiiiycuk.hmv.R;
import com.github.caiiiycuk.hmv.activity.Router;
import com.github.caiiiycuk.hmv.ui.Ui;
import com.github.caiiiycuk.hmv.ui.widget.ColorCircle;
import com.github.caiiiycuk.hmv.ui.widget.FAB;
import com.github.caiiiycuk.hmv.ui.widget.FABSpec;
import com.github.caiiiycuk.hmv.ui.widget.ModalLoading;
import com.github.caiiiycuk.hmv.ui.widget.Title;

import java.io.File;
import java.io.FileOutputStream;

@LayoutSpec
public class ResultScreenSpec {

    @OnCreateInitialState
    static void onCreateInitialState(ComponentContext c,
                                     StateValue<Integer> tintColor) {
        tintColor.set(R.color.tintColor_5);
    }

    @OnCreateLayout
    static Component onCreateLayout(ComponentContext c,
                                    @Prop Bitmap bitmap,
                                    @State @ColorRes int tintColor,
                                    @State Bitmap result) {
        return Column.create(c)
                .child(Title.create(c)
                        .textRes(R.string.save_result)
                        .build())
                .child(Row.create(c)
                        .heightRes(R.dimen.title_height)
                        .justifyContent(YogaJustify.SPACE_EVENLY)
                        .alignItems(YogaAlign.CENTER)
                        .backgroundRes(R.color.colorPrimary)
                        .paddingRes(YogaEdge.HORIZONTAL, R.dimen.ident)
                        .paddingRes(YogaEdge.VERTICAL, R.dimen.identHalf)
                        .child(ColorCircle.create(c)
                                .colorRes(R.color.tintColor_1)
                                .clickHandler(ResultScreen.onColorClick(c, R.color.tintColor_1))
                                .active(tintColor == R.color.tintColor_1)
                                .build())
                        .child(ColorCircle.create(c)
                                .colorRes(R.color.tintColor_2)
                                .clickHandler(ResultScreen.onColorClick(c, R.color.tintColor_2))
                                .active(tintColor == R.color.tintColor_2)
                                .build())
                        .child(ColorCircle.create(c)
                                .colorRes(R.color.tintColor_3)
                                .clickHandler(ResultScreen.onColorClick(c, R.color.tintColor_3))
                                .active(tintColor == R.color.tintColor_3)
                                .build())
                        .child(ColorCircle.create(c)
                                .colorRes(R.color.tintColor_4)
                                .clickHandler(ResultScreen.onColorClick(c, R.color.tintColor_4))
                                .active(tintColor == R.color.tintColor_4)
                                .build())
                        .child(ColorCircle.create(c)
                                .colorRes(R.color.tintColor_5)
                                .clickHandler(ResultScreen.onColorClick(c, R.color.tintColor_5))
                                .active(tintColor == R.color.tintColor_5)
                                .build())
                        .child(ColorCircle.create(c)
                                .colorRes(R.color.tintColor_6)
                                .clickHandler(ResultScreen.onColorClick(c, R.color.tintColor_6))
                                .active(tintColor == R.color.tintColor_6)
                                .build())
                        .build())
                .child(Image.create(c)
                        .flexGrow(1)
                        .scaleType(ImageView.ScaleType.FIT_CENTER)
                        .drawable(new BitmapDrawable(c.getResources(), result != null ? result : bitmap))
                        .build())
                .child(FAB.create(c)
                        .align(FABSpec.LEFT)
                        .drawableRes(R.drawable.back)
                        .clickHandler(ResultScreen.onBackClick(c))
                        .build())
                .child(FAB.create(c)
                        .align(FABSpec.RIGHT)
                        .drawableRes(R.drawable.share)
                        .clickHandler(ResultScreen.onShare(c))
                        .build())
                .child(result == null ? ModalLoading.create(c).build() : null)
                .build();
    }

    @OnAttached
    static void onAttached(ComponentContext c,
                           @Prop Bitmap bitmap,
                           @Prop Bitmap roiMark,
                           @Prop float x,
                           @Prop float y,
                           @State @ColorRes int tintColor) {
        ResultScreen.updateResult(c, bitmap, roiMark, x, y, tintColor);
    }

    @OnDetached
    static void onDetached(ComponentContext c, @State Bitmap result) {
        if (result != null) {
            Ui.post(result::recycle);
        }
    }

    @OnUpdateState
    static void updateResult(StateValue<Bitmap> result,
                             StateValue<Integer> tintColor,
                             @Param Bitmap pBitmap,
                             @Param Bitmap pRoiMark,
                             @Param float pX,
                             @Param float pY,
                             @Param @ColorRes int pTintColor) {
        Bitmap prevResult = result.get();

        Bitmap resultBitmap = pBitmap.copy(pBitmap.getConfig(), true);
        Canvas canvas = new Canvas(resultBitmap);
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(Ui.getColor(pTintColor), PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(pRoiMark, pX, pY, paint);
        result.set(resultBitmap);
        tintColor.set(pTintColor);

        if (prevResult != null) {
            Ui.post(prevResult::recycle);
        }
    }

    @OnEvent(ClickEvent.class)
    static void onShare(ComponentContext c,
                        @State Bitmap result) {
        if (result == null) {
            return;
        }

        Context context = c.getAndroidContext();
        File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File targetFile = new File(picturesDir, "IMG_" + System.currentTimeMillis() + ".jpg");
        try {
            FileOutputStream out = new FileOutputStream(targetFile);
            result.compress(Bitmap.CompressFormat.JPEG, 65, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            Ui.post(() -> Toast.makeText(context, "Unable to save file: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            return;
        }
        Uri uri = FileProvider.getUriForFile(context,
                context.getApplicationContext().getPackageName() + ".provider",
                targetFile);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/jpg");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        CharSequence share = c.getAndroidContext().getResources().getText(R.string.share);
        ContextCompat.startActivity(context,
                Intent.createChooser(intent,  share), null);
    }

    @OnEvent(ClickEvent.class)
    static void onBackClick(ComponentContext c, @Prop Router router) {
        router.back();
    }

    @OnEvent(ClickEvent.class)
    static void onColorClick(ComponentContext c,
                             @Prop Bitmap bitmap,
                             @Prop Bitmap roiMark,
                             @Prop float x,
                             @Prop float y,
                             @Param @ColorRes int pTintColor) {
        ResultScreen.updateResult(c, bitmap, roiMark, x, y, pTintColor);
    }
}
