#import "MyCameraPlugin.h"
#if __has_include(<my_camera/my_camera-Swift.h>)
#import <my_camera/my_camera-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "my_camera-Swift.h"
#endif

@implementation MyCameraPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftMyCameraPlugin registerWithRegistrar:registrar];
}
@end
