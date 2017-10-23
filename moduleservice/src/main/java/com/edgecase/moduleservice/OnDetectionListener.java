package com.edgecase.moduleservice;

import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.Face;

import java.util.List;

public interface OnDetectionListener {

    void onFaceDetected(boolean bDetected);

    void onImageResults(List<Face> faces, Frame frame, float v);

}
