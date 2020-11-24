package com.search.my_camera;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;

import com.search.my_camera.GraphicOverlay;


class FaceGraphicTracker extends Tracker<Face> {

    private GraphicOverlay<FaceGraphic> overlay;
    private FaceGraphic graphic;

    public FaceGraphicTracker(GraphicOverlay<FaceGraphic> overlay, FaceGraphic graphic) {
        this.overlay = overlay;
        this.graphic = graphic;
    }

    @Override
    public void onNewItem(int id, Face face) {
        graphic.setId(id);
    }

    @Override
    public void onUpdate(Detector.Detections<Face> detectionResults, Face item) {
        overlay.add(graphic);
        graphic.updateItem(item);
    }

    @Override
    public void onMissing(Detector.Detections<Face> detectionResults) {
        overlay.remove(graphic);
    }

    @Override
    public void onDone() {
        overlay.remove(graphic);
    }
}
