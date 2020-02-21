package com.github.caiiiycuk.hmv.cv;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.github.caiiiycuk.hmv.BuildConfig;
import com.github.caiiiycuk.hmv.ui.Ui;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.CvBox2D;
import org.bytedeco.opencv.opencv_core.CvContour;
import org.bytedeco.opencv.opencv_core.CvMemStorage;
import org.bytedeco.opencv.opencv_core.CvPoint;
import org.bytedeco.opencv.opencv_core.CvPoint2D32f;
import org.bytedeco.opencv.opencv_core.CvSeq;
import org.bytedeco.opencv.opencv_core.CvSize2D32f;
import org.bytedeco.opencv.opencv_core.IplImage;

import static org.bytedeco.opencv.global.opencv_core.cvBox2D;
import static org.bytedeco.opencv.global.opencv_core.cvClearMemStorage;
import static org.bytedeco.opencv.global.opencv_core.cvCreateImage;
import static org.bytedeco.opencv.global.opencv_core.cvCreateMemStorage;
import static org.bytedeco.opencv.global.opencv_core.cvGetSeqElem;
import static org.bytedeco.opencv.global.opencv_core.cvNot;
import static org.bytedeco.opencv.global.opencv_core.cvPoint;
import static org.bytedeco.opencv.global.opencv_core.cvReleaseImage;
import static org.bytedeco.opencv.global.opencv_core.cvSize;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR5652GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_GRAY2BGR565;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_ADAPTIVE_THRESH_GAUSSIAN_C;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_CLOCKWISE;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_POLY_APPROX_DP;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_RETR_LIST;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_THRESH_BINARY;
import static org.bytedeco.opencv.global.opencv_imgproc.cvAdaptiveThreshold;
import static org.bytedeco.opencv.global.opencv_imgproc.cvApproxPoly;
import static org.bytedeco.opencv.global.opencv_imgproc.cvCanny;
import static org.bytedeco.opencv.global.opencv_imgproc.cvContourArea;
import static org.bytedeco.opencv.global.opencv_imgproc.cvConvexHull2;
import static org.bytedeco.opencv.global.opencv_imgproc.cvCvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.cvDilate;
import static org.bytedeco.opencv.global.opencv_imgproc.cvDrawContours;
import static org.bytedeco.opencv.global.opencv_imgproc.cvFindContours;
import static org.bytedeco.opencv.global.opencv_imgproc.cvMinAreaRect2;
import static org.bytedeco.opencv.global.opencv_imgproc.cvPointPolygonTest;
import static org.bytedeco.opencv.global.opencv_imgproc.cvPyrDown;
import static org.bytedeco.opencv.global.opencv_imgproc.cvPyrUp;
import static org.bytedeco.opencv.helper.opencv_core.CV_RGB;

public class ROICalculator {

    public static int METHOD_SOFT = 0;
    public static int METHOD_HARD = 1;


    private ROICalculator() {
    }

    @NonNull
    @WorkerThread
    public static ROI calculate(Bitmap bitmap, int method) {
        float[] roiPoint = new float[]{bitmap.getWidth() / 2, bitmap.getHeight() / 2};
        float roiEpsilon = bitmap.getWidth() * 0.05f;
        float roiMinArea = (float) ((bitmap.getWidth() * bitmap.getHeight()) * Math.pow(0.05f, 2));
        float roiMaxArea = (float) (bitmap.getWidth() * bitmap.getHeight() * Math.pow(0.5f, 2));

        CvMemStorage storage = cvCreateMemStorage(0);

        IplImage grayImage = convert2Gray(bitmap);
        IplImage outImage = cvCreateImage(grayImage.cvSize(), 8, 2);

        removeNoiseUsingPyrSampler(grayImage);
        applyThresholdUsing(grayImage, method);
        cvCvtColor(grayImage, outImage, COLOR_GRAY2BGR565);

        CvSeq contours = new CvSeq();
        cvFindContours(grayImage, storage, contours,
                Loader.sizeof(CvContour.class), CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE,
                cvPoint(0, 0));

        CvSeq selected = null;
        double selectedDistance = -1;
        while (contours != null && !contours.isNull()) {
            CvSeq polygon = cvApproxPoly(contours, Loader.sizeof(CvContour.class), storage, CV_POLY_APPROX_DP,
                    roiEpsilon, 0);
            polygon = cvConvexHull2(polygon, storage, CV_CLOCKWISE, 1);

            double area = cvContourArea(polygon);
            if (area < roiMinArea || area > roiMaxArea) {
                contours = contours.h_next();
                continue;
            }

            double distance = Math.abs(cvPointPolygonTest(polygon, roiPoint, 1));
            if (selected == null || distance < selectedDistance) {
                selected = polygon;
                selectedDistance = distance;
            }

            if (BuildConfig.DEBUG && false) {
                cvDrawContours(outImage, polygon, CV_RGB(0, 200, 128),
                        CV_RGB(255, 255, 255), 0, 2, 4);
            }

            contours = contours.h_next();
        }

        CvBox2D roiBox;
        int color = 0;
        if (selected != null) {
            cvDrawContours(outImage, selected, CV_RGB(0, 255, 0),
                    CV_RGB(255, 255, 255), 0, 4, 4);
            float avgColor = 0;
            for (int i = 0; i < selected.total(); ++i) {
                CvPoint point = new CvPoint(cvGetSeqElem(selected, 1));
                float pixelColor = bitmap.getPixel(point.x(), point.y());
                avgColor += pixelColor / selected.total();
            }
            color = (int) avgColor;
            roiBox = cvMinAreaRect2(selected);
        } else {
            roiBox = cvBox2D();
        }

        cvNot(outImage, outImage);

        CvPoint2D32f center = roiBox.center();
        CvSize2D32f size = roiBox.size();

        float x = center.x();
        float y = center.y();
        float width = size.width();
        float height = size.height();
        int angle = ((int) roiBox.angle() % 90);
        angle = angle < 0 ? angle + 90 : angle;
        float left = x - width / 2;
        float top = y - height / 2;
        Bitmap outBitmap = convert2Bitmap(outImage);
        cvReleaseImage(outImage);
        cvReleaseImage(grayImage);
        cvClearMemStorage(storage);

        Bitmap roiMark = Ui.createMark((int) width, (int) height, angle);
        Canvas canvas = new Canvas(outBitmap);
        canvas.drawBitmap(roiMark, left, top, new Paint());

        return new ROI(outBitmap, roiMark, left, top, color);
    }

    private static IplImage convert2Gray(Bitmap bitmap) {
        AndroidFrameConverter androidFrameConverter = new AndroidFrameConverter();
        OpenCVFrameConverter.ToIplImage iplConverter = new OpenCVFrameConverter.ToIplImage();
        Frame frame = androidFrameConverter.convert(bitmap);

        IplImage src = iplConverter.convert(frame);
        IplImage gray = cvCreateImage(src.cvSize(), 8, 1);
        cvCvtColor(src, gray, COLOR_BGR5652GRAY);
//        @caiiiyck: I think that androidFrameConverter used native bytes of bitmap,
//        and trying to release it results in native crash
//        cvReleaseImage(src);

        return gray;
    }

    private static void removeNoiseUsingPyrSampler(IplImage gray) {
        IplImage sampler = cvCreateImage(cvSize(gray.width() / 2, gray.height() / 2), 8, 1);
        cvPyrDown(gray, sampler, 7);
        cvPyrUp(sampler, gray, 7);
        cvReleaseImage(sampler);
    }

    private static void applyThresholdUsing(IplImage gray, int method) {
        if (method == METHOD_SOFT) {
            cvAdaptiveThreshold(gray, gray, 255, CV_ADAPTIVE_THRESH_GAUSSIAN_C, CV_THRESH_BINARY, 11, 2);
            cvCanny(gray, gray, 0, 50, 5);
            cvDilate(gray, gray, null, 1);
        } else {
            cvDilate(gray, gray, null, 5);
            cvAdaptiveThreshold(gray, gray, 255, CV_ADAPTIVE_THRESH_GAUSSIAN_C, CV_THRESH_BINARY, 11, 2);
            cvCanny(gray, gray, 0, 50, 5);
            cvDilate(gray, gray, null, 1);
        }
    }

    private static Bitmap convert2Bitmap(IplImage outImage) {
        AndroidFrameConverter androidFrameConverter = new AndroidFrameConverter();
        OpenCVFrameConverter.ToIplImage iplConverter = new OpenCVFrameConverter.ToIplImage();
        Frame outFrame = iplConverter.convert(outImage);
        return androidFrameConverter.convert(outFrame);
    }
}
