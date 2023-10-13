import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

class LifecycleEventHandler extends WidgetsBindingObserver {
  final AsyncCallback onResumed;
  final AsyncCallback onInactive;
  final AsyncCallback onPaused;
  final AsyncCallback onDetached;

  LifecycleEventHandler({
    required this.onResumed,
    required this.onInactive,
    required this.onPaused,
    required this.onDetached,
  });

  @override
  Future<Null> didChangeAppLifecycleState(state) async {
    switch (state) {
      case AppLifecycleState.resumed:
        await onResumed();
        break;
      case AppLifecycleState.inactive:
        await onInactive();
        break;
      case AppLifecycleState.paused:
        await onPaused();
        break;
      case AppLifecycleState.detached:
        await onDetached();
        break;
      case AppLifecycleState.hidden:
        // TODO: Handle this case.
        break;
    }
  }
}
