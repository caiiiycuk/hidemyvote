package com.github.caiiiycuk.ruvote.cv;

import android.graphics.Bitmap;

import androidx.annotation.WorkerThread;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.CvContour;
import org.bytedeco.opencv.opencv_core.CvMemStorage;
import org.bytedeco.opencv.opencv_core.CvPoint;
import org.bytedeco.opencv.opencv_core.CvSeq;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.bytedeco.opencv.opencv_core.Mat;

import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_core.cvClearMemStorage;
import static org.bytedeco.opencv.global.opencv_core.cvCloneImage;
import static org.bytedeco.opencv.global.opencv_core.cvCopy;
import static org.bytedeco.opencv.global.opencv_core.cvCreateImage;
import static org.bytedeco.opencv.global.opencv_core.cvCreateMemStorage;
import static org.bytedeco.opencv.global.opencv_core.cvFlip;
import static org.bytedeco.opencv.global.opencv_core.cvGetSeqElem;
import static org.bytedeco.opencv.global.opencv_core.cvNot;
import static org.bytedeco.opencv.global.opencv_core.cvPoint;
import static org.bytedeco.opencv.global.opencv_core.cvReleaseImage;
import static org.bytedeco.opencv.global.opencv_core.cvScalar;
import static org.bytedeco.opencv.global.opencv_core.bitwise_not;
import static org.bytedeco.opencv.global.opencv_core.cvSize;
import static org.bytedeco.opencv.global.opencv_core.cvXor;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_GRAY2BGR565;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_AA;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_ADAPTIVE_THRESH_GAUSSIAN_C;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_CLOCKWISE;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_POLY_APPROX_DP;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_RETR_LIST;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_RGB2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR5652GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_RGBA2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_THRESH_BINARY;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_THRESH_BINARY_INV;
import static org.bytedeco.opencv.global.opencv_imgproc.cvAdaptiveThreshold;
import static org.bytedeco.opencv.global.opencv_imgproc.cvApproxPoly;
import static org.bytedeco.opencv.global.opencv_imgproc.cvCanny;
import static org.bytedeco.opencv.global.opencv_imgproc.cvCheckContourConvexity;
import static org.bytedeco.opencv.global.opencv_imgproc.cvContourArea;
import static org.bytedeco.opencv.global.opencv_imgproc.cvContourPerimeter;
import static org.bytedeco.opencv.global.opencv_imgproc.cvConvexHull2;
import static org.bytedeco.opencv.global.opencv_imgproc.cvCvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.cvDilate;
import static org.bytedeco.opencv.global.opencv_imgproc.cvDrawContours;
import static org.bytedeco.opencv.global.opencv_imgproc.cvFillConvexPoly;
import static org.bytedeco.opencv.global.opencv_imgproc.cvFindContours;
import static org.bytedeco.opencv.global.opencv_imgproc.cvPointPolygonTest;
import static org.bytedeco.opencv.global.opencv_imgproc.cvPolyLine;
import static org.bytedeco.opencv.global.opencv_imgproc.cvPyrDown;
import static org.bytedeco.opencv.global.opencv_imgproc.cvPyrUp;
import static org.bytedeco.opencv.global.opencv_imgproc.cvThreshold;
import static org.bytedeco.opencv.global.opencv_imgproc.pointPolygonTest;
import static org.bytedeco.opencv.helper.opencv_core.CV_RGB;
import static org.bytedeco.opencv.helper.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.opencv.helper.opencv_imgcodecs.cvSaveImage;
import static org.opencv.core.Core.split;

public class ROIRenderer {

    public static int METHOD_SOFT = 0;
    public static int METHOD_HARD = 1;


    private ROIRenderer() {
    }

    @WorkerThread
    public static Bitmap renderRoi(Bitmap bitmap, int method) {
        AndroidFrameConverter androidFrameConverter = new AndroidFrameConverter();
        OpenCVFrameConverter.ToIplImage iplConverter = new OpenCVFrameConverter.ToIplImage();

        float[] roiPoint = new float[] {bitmap.getWidth() / 2, bitmap.getHeight() / 2};
        float roiEpsilon = bitmap.getWidth() * 0.05f;
        float roiMinArea = (float) ((bitmap.getWidth() * bitmap.getHeight()) * Math.pow(0.05f, 2));
        float roiMaxArea = (float) (bitmap.getWidth() * bitmap.getHeight() * Math.pow(0.5f, 2));


        CvMemStorage storage = cvCreateMemStorage(0);
        Frame frame = androidFrameConverter.convert(bitmap);
        IplImage src = iplConverter.convert(frame);
        IplImage gray = cvCreateImage(src.cvSize(), 8, 1);
        IplImage sampler = cvCreateImage(cvSize(gray.width() / 2, gray.height() / 2), 8, 1);
        IplImage polygons = cvCreateImage(src.cvSize(), 8, 2);

        cvCvtColor(src, gray, COLOR_BGR5652GRAY);
        cvPyrDown(gray, sampler, 7);
        cvPyrUp(sampler, gray, 7);

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

        cvCvtColor(gray, polygons, COLOR_GRAY2BGR565);

        CvSeq contours = new CvSeq();
        cvFindContours(gray, storage, contours,
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

            cvDrawContours(polygons, polygon, CV_RGB(0, 200, 128),
                    CV_RGB(255, 255, 255), 0, 2, 4);

            contours = contours.h_next();
        }

        if (selected != null) {
            cvDrawContours(polygons, selected, CV_RGB(0, 255, 0),
                    CV_RGB(255, 255, 255), 0, 4, 4);
        }

        cvNot(polygons, polygons);
        Frame outFrame = iplConverter.convert(polygons);
        Bitmap outBitmap = androidFrameConverter.convert(outFrame);

        cvReleaseImage(src);
        cvReleaseImage(sampler);
        cvReleaseImage(gray);
        cvReleaseImage(polygons);
        cvClearMemStorage(storage);

        return outBitmap;
    }
}
