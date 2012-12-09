package com.github.axet.desktop.os.mac.foundation;

import com.github.axet.desktop.os.mac.CFStringRef;
import com.sun.jna.Library;
import com.sun.jna.Native;

public interface ApplicationServices extends Library {

    ApplicationServices INSTANCE = (ApplicationServices) Native.loadLibrary("ApplicationServices",
            ApplicationServices.class);

    // https://developer.apple.com/library/mac/#documentation/Carbon/Reference/LaunchServicesReference/Reference/reference.html#//apple_ref/c/func/LSSetDefaultRoleHandlerForContentType

    /**
     * OSStatus LSSetDefaultRoleHandlerForContentType ( CFStringRef
     * inContentType, LSRolesMask inRole, CFStringRef inHandlerBundleID );
     * 
     * @param inURLScheme
     * @param inRole
     * @param inHandlerBundleID
     * @return
     */
    public int LSSetDefaultRoleHandlerForContentType(CFStringRef inURLScheme, int inRole, CFStringRef inHandlerBundleID);

    // https://developer.apple.com/library/mac/#documentation/Carbon/Reference/LaunchServicesReference/Reference/reference.html#//apple_ref/c/func/LSSetDefaultHandlerForURLScheme

    /**
     * OSStatus LSSetDefaultHandlerForURLScheme ( CFStringRef inURLScheme,
     * CFStringRef inHandlerBundleID );
     * 
     * @param inURLScheme
     * @param inHandlerBundleID
     * @return
     */
    public int LSSetDefaultHandlerForURLScheme(CFStringRef inURLScheme, CFStringRef inHandlerBundleID);
}
