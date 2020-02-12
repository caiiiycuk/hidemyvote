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

import static org.bytedeco.opencv.global.opencv_core.cvClearMemStorage;
import static org.bytedeco.opencv.global.opencv_core.cvCloneImage;
import static org.bytedeco.opencv.global.opencv_core.cvCopy;
import static org.bytedeco.opencv.global.opencv_core.cvCreateImage;
import static org.bytedeco.opencv.global.opencv_core.cvCreateMemStorage;
import static org.bytedeco.opencv.global.opencv_core.cvGetSeqElem;
import static org.bytedeco.opencv.global.opencv_core.cvPoint;
import static org.bytedeco.opencv.global.opencv_core.cvReleaseImage;
import static org.bytedeco.opencv.global.opencv_core.cvSeqPush;
import static org.bytedeco.opencv.global.opencv_core.cvSize;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_AA;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_POLY_APPROX_DP;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_RETR_LIST;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_RGB2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_RGBA2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_THRESH_BINARY;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_THRESH_BINARY_INV;
import static org.bytedeco.opencv.global.opencv_imgproc.cvApproxPoly;
import static org.bytedeco.opencv.global.opencv_imgproc.cvCanny;
import static org.bytedeco.opencv.global.opencv_imgproc.cvCheckContourConvexity;
import static org.bytedeco.opencv.global.opencv_imgproc.cvContourPerimeter;
import static org.bytedeco.opencv.global.opencv_imgproc.cvCvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.cvDilate;
import static org.bytedeco.opencv.global.opencv_imgproc.cvDrawContours;
import static org.bytedeco.opencv.global.opencv_imgproc.cvFindContours;
import static org.bytedeco.opencv.global.opencv_imgproc.cvPolyLine;
import static org.bytedeco.opencv.global.opencv_imgproc.cvPyrDown;
import static org.bytedeco.opencv.global.opencv_imgproc.cvPyrUp;
import static org.bytedeco.opencv.global.opencv_imgproc.cvThreshold;
import static org.bytedeco.opencv.helper.opencv_core.CV_RGB;
import static org.bytedeco.opencv.helper.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.opencv.helper.opencv_imgcodecs.cvSaveImage;

public class ROIRenderer {

    private ROIRenderer() {
    }

    @WorkerThread
    public static Bitmap renderRoi(Bitmap bitmap) {
        AndroidFrameConverter androidFrameConverter = new AndroidFrameConverter();
        OpenCVFrameConverter.ToIplImage iplConverter = new OpenCVFrameConverter.ToIplImage();

        CvMemStorage storage = cvCreateMemStorage(0);
        Frame frame = androidFrameConverter.convert(bitmap);
        IplImage src = iplConverter.convert(frame);
        IplImage gray = cvCreateImage(src.cvSize(), 8, 1);
        IplImage sampler = cvCreateImage(cvSize(gray.width() / 2, gray.height() / 2), 8, 1);
        IplImage polygons = cvCreateImage(src.cvSize(), 8, 3);

        cvCvtColor(src, gray, CV_RGBA2GRAY);
        cvPyrDown(gray, sampler, 7);
        cvPyrUp(sampler, gray, 7);
        cvCanny(gray, gray, 0, 50, 5);
        cvDilate(gray, gray, null, 1);


        CvSeq contours = new CvSeq();
        cvFindContours(gray, storage, contours,
                Loader.sizeof(CvContour.class), CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE,
                cvPoint(0, 0));

        while (contours != null && !contours.isNull()) {
            double perimeter = cvContourPerimeter(contours);
            CvSeq polygon = cvApproxPoly(contours, Loader.sizeof(CvContour.class), storage, CV_POLY_APPROX_DP, perimeter * 0.02, 0);
            if (cvCheckContourConvexity(polygon) != 0) {
                cvDrawContours(polygons, polygon, CV_RGB(255, 0, 0), CV_RGB(0, 255, 0), 1);
            }
            contours = contours.h_next();
        }

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
