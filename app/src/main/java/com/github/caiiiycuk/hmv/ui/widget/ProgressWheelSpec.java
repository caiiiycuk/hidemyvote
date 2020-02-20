package com.github.caiiiycuk.hmv.ui.widget;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.facebook.litho.ComponentContext;
import com.facebook.litho.ComponentLayout;
import com.facebook.litho.Size;
import com.facebook.litho.annotations.MountSpec;
import com.facebook.litho.annotations.OnCreateMountContent;
import com.facebook.litho.annotations.OnMeasure;
import com.facebook.litho.annotations.OnMount;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.annotations.PropDefault;
import com.pnikosis.materialishprogress.ProgressWheel;

@MountSpec(isPureRender = true)
public class ProgressWheelSpec {

    @PropDefault
    static final boolean linearProgress = false;

    @PropDefault
    static final boolean spin = true;

    @OnCreateMountContent
    static ProgressWheel onCreateMountContent(@NonNull Context c) {
        ProgressWheel progressWheel = new ProgressWheel(c);
        progressWheel.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        return progressWheel;
    }

    @OnMount
    static void onMount(@NonNull ComponentContext c,
                        @NonNull ProgressWheel progressWheel,
                        @Prop int radiusPx,
                        @Prop(optional = true) boolean linearProgress,
                        @Prop(optional = true) boolean spin) {
        progressWheel.setLinearProgress(linearProgress);
        progressWheel.setCircleRadius(radiusPx);
        if (spin) {
            progressWheel.spin();
        }
    }

    @OnMeasure
    static void onMeasure(
            ComponentContext context,
            ComponentLayout layout,
            int widthSpec,
            int heightSpec,
            Size size,
            @Prop int radiusPx) {
        size.width = radiusPx;
        size.height = radiusPx;
    }
}