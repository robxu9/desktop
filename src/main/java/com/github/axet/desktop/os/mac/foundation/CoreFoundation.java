package com.github.axet.desktop.os.mac.foundation;

import com.github.axet.desktop.os.mac.cocoa.CFRange;
import com.github.axet.desktop.os.mac.cocoa.NSArray;
import com.github.axet.desktop.os.mac.cocoa.NSString;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public interface CoreFoundation extends Library {

    CoreFoundation INSTANCE = (CoreFoundation) Native.loadLibrary("CoreFoundation", CoreFoundation.class);

    //
    // https://developer.apple.com/library/mac/#documentation/CoreFOundation/Reference/CFBundleRef/Reference/reference.html#//apple_ref/c/func/CFBundleGetMainBundle
    //

    /**
     * CFBundleRef CFBundleGetMainBundle ( void );
     * 
     * @return
     */
    public Pointer CFBundleGetMainBundle();

    /**
     * CFStringRef CFBundleGetIdentifier ( CFBundleRef bundle );
     * 
     * @param bundle
     * @return
     */
    public NSString CFBundleGetIdentifier(Pointer bundle);

}
