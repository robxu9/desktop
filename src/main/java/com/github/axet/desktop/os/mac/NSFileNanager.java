package com.github.axet.desktop.os.mac;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public interface NSFileNanager extends Library {

    NSFileNanager INSTANCE = (NSFileNanager) Native.loadLibrary("Foundation", NSFileNanager.class);

    static final int NSDownloadsDirectory = 15;
    static final int NSUserDomainMask = 1;

    // http://developer.apple.com/library/mac/#documentation/Cocoa/Reference/Foundation/Miscellaneous/Foundation_Functions/Reference/reference.html#//apple_ref/c/func/NSSearchPathForDirectoriesInDomains

    public Pointer NSSearchPathForDirectoriesInDomains(int directory, int domainMask, boolean expandTilde);
}
