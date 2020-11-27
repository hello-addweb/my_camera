# custom_camera

custom_camera makes it super easy to add camera to your Flutter app

# Features 
Capture image

Save image in gallery

Two types  of camera 

  - Front  
  - Back
   
Flash light

QR Code Scanner

OCR

Custom design of camera

# Usage

First, add custom_camera as a dependency in your pubspec.yaml file.

# And  add some permission in manifest file
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
import 'package:custom_camera/my_cameranew.dart';

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


# Example

```
import 'dart:async';
import 'dart:io';
import 'dart:typed_data';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:custom_camera/my_cameranew.dart';

void main() {
  String id = DateTime.now().toIso8601String();
  runApp(MaterialApp(home: MyApp(id: id)));
}

class MyApp extends StatefulWidget {
  final String id;

  const MyApp({Key key, this.id}) : super(key: key);

  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  List<String> pictureSizes = [];
  String imagePath;
  Uint8List bytes = Uint8List(0);
  TextEditingController _inputController;
  TextEditingController outputController;
  MyCameraController cameraController;

  @override
  initState() {
    super.initState();
    this._inputController = new TextEditingController();
    this.outputController = new TextEditingController();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Home'),
      ),
      body: SafeArea(
        child: Stack(
          children: [
            Column(
              children: [
                Container(
                  color: Colors.transparent,
                  child: Row(
                    children: [
                      IconButton(
                        icon: Icon(
                          Icons.flash_off_outlined,
                          color: Colors.black,
                        ),
                        onPressed: () {
                          cameraController.setFlashType(FlashType.off);
                        },
                      ),
                      IconButton(
                        icon: Icon(
                          Icons.flash_on,
                          color: Colors.black,
                        ),
                        onPressed: () {
                          cameraController.setFlashType(FlashType.torch);
                        },
                      ),
                    ],
                  ),
                ),
                TextField(
                  controller: this.outputController,
                  maxLines: 2,
                  decoration: InputDecoration(
                    prefixIcon: Icon(Icons.wrap_text),
                    hintText:
                        'The barcode or qrcode you scan will be displayed in this area.',
                    hintStyle: TextStyle(fontSize: 15),
                    contentPadding:
                        EdgeInsets.symmetric(horizontal: 7, vertical: 15),
                  ),
                ),
                Expanded(
                    child: Container(
                  child: MyCamera(
                    onCameraCreated: _onCameraCreated,
                    onImageCaptured: (String path) {
                      print("onImageCaptured => " + path);
                      if (this.mounted)
                        setState(() {
                          imagePath = path;
                        });
                    },
                    cameraPreviewRatio: CameraPreviewRatio.r16_9,
                  ),
                )),
              ],
            ),
            Positioned(
              bottom: 16.0,
              left: 16.0,
              child: imagePath != null
                  ? Container(
                      width: 100.0,
                      height: 100.0,
                      child: Image.file(File(imagePath)))
                  : Icon(Icons.image),
            )
          ],
        ),
      ),
      floatingActionButton: Column(
          crossAxisAlignment: CrossAxisAlignment.end,
          mainAxisSize: MainAxisSize.min,
          children: [
            FloatingActionButton(
              heroTag: 1,
              child: Icon(Icons.switch_camera),
              onPressed: () async {
                await cameraController.switchCamera();
                List<FlashType> types = await cameraController.getFlashType();
              },
            ),
            Container(height: 16.0),
            FloatingActionButton(
                heroTag: 2,
                child: Icon(Icons.camera_alt),
                onPressed: () {
                  cameraController.captureImage();
                }),
            Container(height: 16.0),
            FloatingActionButton(
                heroTag: 3,
                child: Icon(Icons.scanner),
                onPressed: () {
                  _scan();
                }),
           
          ]),
    );
  }

  Future _scan() async {
    String barcode = await cameraController.scan();

    if (barcode == null) {
      print('nothing return.');
    } else {
      this.outputController.text = barcode;
      print(barcode);
    }
  }

  _onCameraCreated(MyCameraController controller) {
    this.cameraController = controller;

    this.cameraController.getPictureSizes().then((pictureSizes) {
      setState(() {
        this.pictureSizes = pictureSizes;
      });
    });
  }

  
}

```