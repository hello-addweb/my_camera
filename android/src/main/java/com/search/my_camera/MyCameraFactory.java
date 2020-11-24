package com.search.my_camera;

import android.content.Context;

import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class MyCameraFactory extends PlatformViewFactory {
    private final PluginRegistry.Registrar mPluginRegistrar;

    MyCameraFactory(PluginRegistry.Registrar registrar) {
        super(StandardMessageCodec.INSTANCE);
        mPluginRegistrar = registrar;
    }

    @Override
    public PlatformView create(Context context, int id, Object args) {
        return new MyCamera(id, context, mPluginRegistrar, args);
    }
}
