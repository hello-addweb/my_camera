import 'dart:async';
import 'dart:io';
import 'dart:typed_data';

import 'package:flutter/cupertino.dart';
import 'package:my_camera_pluggin/my_cameranew.dart';
import 'package:flutter/material.dart';
import 'new.dart';

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
                          icon: Icon(Icons.flash_off_outlined,color: Colors.black,),
                          onPressed: () {
                            cameraController.setFlashType(FlashType.off);
                          },
                        ),
                        IconButton(
                          icon: Icon(Icons.flash_on,color: Colors.black,),

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
                    helperText: 'The barcode or qrcode you scan will be displayed in this area.',
                    hintText: 'The barcode or qrcode you scan will be displayed in this area.',
                    hintStyle: TextStyle(fontSize: 15),
                    contentPadding: EdgeInsets.symmetric(horizontal: 7, vertical: 15),
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
            FloatingActionButton(heroTag: 1,
              child: Icon(Icons.switch_camera),
              onPressed: () async {
                await cameraController.getFlashType();
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


  Future _scan() async {
  String barcode =await cameraController.scan();

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

  void _navigation() {

    Navigator.push(
      context,
      new MaterialPageRoute(builder: (context) => new Home()),
    );
  }

}