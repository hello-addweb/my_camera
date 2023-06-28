# custom_camera_v2

custom_camera_v2 makes it super easy to add camera to your Flutter app

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

First, add custom_camera_v2 as a dependency in your pubspec.yaml file.

# And  add some permission in manifest file
```
//1.
// <uses-permission android:name="android.permission.INTERNET"/> is used in Android applications to grant access to the internet.When an Android app requires this permission, it means that the app needs to establish network connections and communicate with remote servers or services over the internet. This permission allows the app to send and receive data, download resources, make API calls, and interact with online services.
<uses-permission android:name="android.permission.INTERNET"/> 

//2.
// The permission android.permission.READ_EXTERNAL_STORAGE is used in Android applications to request access to read the external storage of the device. This permission allows the app to read files and folders from the user's external storage, such as the SD card or other storage locations.
// It's important to note that starting from Android 10 (API level 29), apps are required to use the Storage Access Framework (SAF) to access files on external storage. The READ_EXTERNAL_STORAGE permission is considered a "dangerous" permission, meaning that users have to explicitly grant it, and developers need to handle runtime permissions to request and obtain this permission from the user.
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />

//3.
// The permission android.permission.WRITE_EXTERNAL_STORAGE is used in Android applications to allow the app to write data to external storage, such as the device's SD card or other external storage devices. This permission is necessary if the app needs to save or modify files on the user's device outside of its private internal storage.
// It's worth noting that starting from Android 11 (API level 30), apps are generally encouraged to use more granular storage permissions, such as MANAGE_EXTERNAL_STORAGE, to request access to specific directories or types of files on external storage.
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

//4., 5.
// The <uses-feature> element is used to indicate that the app requires a particular hardware feature on the device to function properly. In this case, android.hardware.camera indicates that the app requires a camera to be present on the device. It does not grant permission to access the camera directly.
// If your app needs to access the camera and capture photos or videos, you would need to request the appropriate camera permission in addition to specifying the required camera feature. The permission to access the camera is typically declared using the <uses-permission> element in the manifest file, with a declaration like <uses-permission android:name="android.permission.CAMERA" />.
// Please note that camera-related permissions should be used responsibly and only when necessary for the functionality of your app. Additionally, starting from Android 6.0 (API level 23), users are prompted to grant permissions at runtime, so you would need to handle the permission request and user consent in your app code as well.
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera" />

//6.
// It indicates that the application requires the presence of autofocus capability on the device's camera in order to function properly.
// The android.hardware.camera.autofocus feature is used to determine if the device's camera supports autofocus functionality. Autofocus is a feature that allows the camera to automatically focus on the subject being captured, resulting in sharper and clearer images.
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
import 'package:custom_camera_v2/my_cameranew.dart';

/// 
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
import 'package:custom_camera_v2/my_cameranew.dart';

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
                   // Flash light off button. clicking on the button will turn off the torch.
                     IconButton(
                       icon: Icon(
                         Icons.flash_off_outlined,
                         color: Colors.black,
                       ),
                       onPressed: () {
                         cameraController.setFlashType(FlashType.off);
                       },
                     ),
                    /// Flash light on button. click on the button on the torch.
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
               // This text field will show the url of the QR Code and BarCode.
               TextField(
                 controller: this.outputController,
                 maxLines: 2,
                 decoration: InputDecoration(
                   prefixIcon: Icon(Icons.wrap_text),
                   hintText:
                       'The barcode or qr code you scan will be displayed in this area.',
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
         // switch camera button. This is for switching front and back camera.
         
           FloatingActionButton(
             heroTag: 1,
             child: Icon(Icons.switch_camera),
             onPressed: () async {
               await cameraController.switchCamera();
               List<FlashType> types = await cameraController.getFlashType();
             },
           ),
           Container(height: 16.0),
           // this button is for capture the image.
         
           FloatingActionButton(
               heroTag: 2,
               child: Icon(Icons.camera_alt),
               onPressed: () {
                 cameraController.captureImage();
               }),
           Container(height: 16.0),
           // Scan the QR code and Barcode.
          
           FloatingActionButton(
               heroTag: 3,
               child: Icon(Icons.scanner),
               onPressed: () {
                 _scan();
               }),
         
         ]),
   );
 }

/// it will return the barcode
 Future _scan() async {
   String barcode = await cameraController.scan();

   if (barcode == null) {
     print('nothing return.');
   } else {
     this.outputController.text = barcode;
     print(barcode);
   }
 }
 
/// get the picture size after calling this function.
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