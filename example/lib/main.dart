import 'dart:async';
import 'dart:developer';
import 'dart:io';
import 'package:custom_camera_v2/my_cameranew.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'new.dart';

void main() {
  String id = DateTime.now().toIso8601String();
  runApp(MaterialApp(
    debugShowCheckedModeBanner: false,
      home: MyApp(id: id)));
}
class MyApp extends StatefulWidget {
  final String? id;
  const MyApp({Key? key, this.id}) : super(key: key);
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  List<String> pictureSizes = [];
  String? imagePath;
  Uint8List bytes = Uint8List(0);
  late TextEditingController outputController;
  late MyCameraController cameraController;

  @override
  initState() {
    super.initState();
    this.outputController = new TextEditingController();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Home'),
        actions: [Container(
          color: Colors.transparent,
          child: Row(
            children: [
              /// Scanning Photo which gives more functionality of flashLight on or off
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
        ),],
      ),
      body: SafeArea(
        child: Stack(
          children: [
            Column(
              children: [
                /// How to create layouts ui container
                /// The barcode or qrcode you scan will be displayed
                TextField(
                  controller: this.outputController,
                  maxLines: 2,
                  enabled: false,
                  decoration: InputDecoration(
                    prefixIcon: Icon(Icons.wrap_text),
                    hintText:
                        'The barcode or qrcode you scan will be displayed in this area.',
                    hintStyle: TextStyle(fontSize: 15),
                    contentPadding:
                        EdgeInsets.symmetric(horizontal: 7, vertical: 15),
                  ),
                ),
                /// Image Capture in camera
                Expanded(
                    child: Container(
                  child: MyCamera(
                    onCameraCreated: _onCameraCreated,
                    onImageCaptured: (String path) {
                      log("onImageCaptured => " + path);
                      if (this.mounted)
                        setState(() {
                          if(path.isNotEmpty)
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
                      child: Image.file(File(imagePath!),),)
                  : Icon(Icons.image),
            )
          ],
        ),
      ),
      floatingActionButton: Column(
          crossAxisAlignment: CrossAxisAlignment.end,
          mainAxisSize: MainAxisSize.min,
          children: [
            /// Switch camera tap button
            FloatingActionButton(
              heroTag: 1,
              child: Icon(Icons.switch_camera),
              onPressed: () async {
                await cameraController.switchCamera();
              },
            ),
            Container(height: 16.0),
            /// Camera alt tap button
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
            Container(height: 16.0),
            FloatingActionButton(
                heroTag: 4,
                child: Icon(Icons.chrome_reader_mode),
                onPressed: () {
                  _navigation();
                }),
          ]),
    );
  }

  /// get the barcode string, after scan the code
  Future _scan() async {
    String barcode = await cameraController.scan();
    this.outputController.text = barcode;
  }

  /// get the PictureSize
  _onCameraCreated(MyCameraController controller) {
    this.cameraController = controller;

    this.cameraController.getPictureSizes().then((pictureSizes) {
      setState(() {
        this.pictureSizes = pictureSizes!;
      });
    });
  }

  /// navigate to Home screen
  void _navigation() {
    Navigator.push(
      context,
      new MaterialPageRoute(builder: (context) => new Home()),
    );
  }
}
