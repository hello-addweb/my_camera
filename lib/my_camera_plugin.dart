import 'package:flutter/services.dart';

class MyCameraPlugin {
  static const int CAMERA_BACK = 0;
  static const int CAMERA_FRONT = 1;
  static const MethodChannel _channel = const MethodChannel('my_camera',);



  static Future<bool> checkForPermission() async {
    return await _channel.invokeMethod('checkForPermission');

  }
  static final Map<int, List<Call>> _previewSizes = {};
  static const Call PREVIEW = Call(640, 480);
 // static const MethodChannel channel = const MethodChannel('my_camera',);

  ///
  ///
  ///
  static List<Call> getPreviewSizes(int facing) {
    if (_previewSizes.containsKey(facing)) {
      return _previewSizes[facing];
    }
    return null;
  }

  static Future<List<OcrText>> read({
    bool flash = false,
    bool autoFocus = true,
    bool multiple = false,
    bool waitTap = false,
    bool showText = true,
    Call preview = PREVIEW,
    int camera = CAMERA_BACK,
    double fps = 2.0,
  }) async {
    Map<String, dynamic> arguments = {
      'flash': flash,
      'autoFocus': autoFocus,
      'multiple': multiple,
      'waitTap': waitTap,
      'showText': showText,
      'previewWidth': preview != null ? preview.width : PREVIEW.width,
      'previewHeight': preview != null ? preview.height : PREVIEW.height,
      'camera': camera,
      'fps': fps,
    };

    final List list = await _channel.invokeMethod('read', arguments);

    return list.map((map) => OcrText.fromMap(map)).toList();
  }

}
class OcrText {
  final String value;
  final String language;
  final int top;
  final int bottom;
  final int left;
  final int right;

  OcrText(
      this.value, {
        this.language = '',
        this.top = -1,
        this.bottom = -1,
        this.left = -1,
        this.right = -1,
      });

  OcrText.fromMap(Map map)
      : value = map['value'],
        language = map['language'],
        top = map['top'],
        bottom = map['bottom'],
        left = map['left'],
        right = map['right'];

  Map<String, dynamic> toMap() {
    return {
      'value': value,
      'language': language,
      'top': top,
      'bottom': bottom,
      'left': left,
      'right': right,
    };
  }
}
class Call {
  final int width;
  final int height;

  const Call(this.width, this.height);

  Call.fromMap(Map map)
      : width = map['width'],
        height = map['height'];

  @override
  String toString() {
    return '$width x $height';
  }
}