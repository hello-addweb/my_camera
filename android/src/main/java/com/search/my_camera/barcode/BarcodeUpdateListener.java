package com.search.my_camera;

import androidx.annotation.UiThread;

import com.google.android.gms.vision.barcode.Barcode;

public interface BarcodeUpdateListener {

    @UiThread
    void onBarcodeDetected(Barcode barcode);

}
