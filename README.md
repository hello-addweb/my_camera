# my_camera

my_camera makes it super easy to add camera to your Flutter app

# Features 
Capture image

Save image in gallery

two types  of camera 

  - front  
  - Back
   
flash light

QR Code Scanner

OCR

Custome Design of camera

# Usage

First, add My_camera as a dependency in your pubspec.yaml file.

# And  add some pemission in manifest file
```
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera" />
<uses-feature android:name="android.hardware.camera.autofocus" />

```
#### And some dependency add in android/build.gradle
```
classpath 'androidx.annotation:annotation:1.0.0'
classpath 'androidx.core:core:1.0.0'
classpath 'androidx.appcompat:appcompat:1.0.0'
classpath 'com.github.bumptech.glide:glide:3.7.0'
classpath 'com.karumi:dexter:6.0.0'
```

# And use this Method in your code

# Add camera

```
import 'package:my_camera/my_camera.dart';

  _onCameraCreated(MyCameraController controller) {
  this.cameraController = controller;
  this.cameraController.getPictureSizes().then((pictureSizes) {
   setState(() {
   this.pictureSizes = pictureSizes; 
   });
 });}
```

# Add flash light
```
IconButton(
  icon: Icon(Icons.flash_off_outlined,color: Colors.black,),
   onPressed: () {  
  cameraController.setFlashType(FlashType.off); 
  },),
IconButton(
 icon: Icon(Icons.flash_on,color: Colors.black,),
  onPressed: () {
    cameraController.setFlashType(FlashType.torch);
  },),
```

# Add Scanner
```
  Future _scan() asyncoutputController {
  String barcode =await cameraController.scan();
  if (barcode == null) {
   print('nothing return.');
   } else {
   this.outputController.text = barcode;
    print(barcode);
    }
  }
```

# Add OCR
```
int _cameraOcr = MyCamera.CAMERA_BACK;
  bool _autoFocusOcr = true;
  bool _torchOcr = false;
  bool _multipleOcr = true;
  bool _waitTapOcr = true;
  bool _showTextOcr = true;
  Call _previewOcr;
  List<OcrText> _textsOcr = [];


 Future<Null> _read() async {
  List<OcrText> texts = [];
    try {
        texts = await MyCameraPlugin.read(
        flash: _torchOcr,
        autoFocus: _autoFocusOcr,
        multiple: _multipleOcr,
        waitTap: _waitTapOcr,
        showText: _showTextOcr,
        preview: _previewOcr,
        camera: _cameraOcr,
        fps: 2.0,
);
    } on Exception {
      texts.add(OcrText(''))
}
    if (!mounted) return;
    setState(() => _textsOcr = texts);
  }
```


