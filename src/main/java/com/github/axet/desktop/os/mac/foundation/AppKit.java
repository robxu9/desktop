package com.github.axet.desktop.os.mac.foundation;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface AppKit extends Library {

    AppKit INSTANCE = (AppKit) Native.loadLibrary("AppKit", AppKit.class);

}
