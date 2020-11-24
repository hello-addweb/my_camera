package com.search.my_camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.face.FaceDetector;

import java.util.ArrayList;

import com.search.my_camera.CameraSource;
import com.search.my_camera.AbstractCaptureActivity;
import com.search.my_camera.MobileVisionException;

public final class FaceCaptureActivity extends AbstractCaptureActivity<FaceGraphic> {


    @SuppressLint("InlinedApi")
    protected void createCameraSource() throws MobileVisionException {
        Context context = getApplicationContext();

        // TODO: Verify attributes.
        FaceDetector faceDetector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        FaceTrackerFactory faceTrackerFactory = new FaceTrackerFactory(graphicOverlay, showText);

        faceDetector.setProcessor(
                new MultiProcessor.Builder<>(faceTrackerFactory).build());

        if (!faceDetector.isOperational()) {
            IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowStorageFilter) != null;

            if (hasLowStorage) {
                throw new MobileVisionException("Low Storage.");
            }
        }

        cameraSource = new CameraSource
                .Builder(getApplicationContext(), faceDetector)
                .setFacing(camera)
                .setRequestedPreviewSize(previewWidth, previewHeight)
                .setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null)
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .setRequestedFps(fps)
                .build();
    }

    protected boolean onTap(float rawX, float rawY) {
        ArrayList<MyFace> list = new ArrayList<>();

        if (multiple) {
            for (FaceGraphic graphic : graphicOverlay.getGraphics()) {
                list.add(new MyFace(graphic.getFace()));
            }
        } else {
            FaceGraphic graphic = graphicOverlay.getBest(rawX, rawY);
            if (graphic != null && graphic.getFace() != null) {
                list.add(new MyFace(graphic.getFace()));
            }
        }

        if (!list.isEmpty()) {
            Intent data = new Intent();
            data.putExtra(OBJECT, list);
            setResult(CommonStatusCodes.SUCCESS, data);
            finish();
            return true;
        }

        return false;
    }
}