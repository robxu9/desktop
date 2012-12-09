package com.github.axet.desktop.apple.fundations;

import com.github.axet.desktop.apple.CFArrayRef;
import com.sun.jna.Library;
import com.sun.jna.Native;

public interface NSFileNanager extends Library {

    NSFileNanager INSTANCE = (NSFileNanager) Native.loadLibrary("Foundation", NSFileNanager.class);

    // http://developer.apple.com/library/mac/#documentation/Cocoa/Reference/Foundation/Miscellaneous/Foundation_Constants/Reference/reference.html#//apple_ref/doc/c_ref/NSSearchPathDirectory

    public interface NSSearchPathDirectory {
        public final int NSApplicationDirectory = 1;
        public final int NSDemoApplicationDirectory = 2;
        public final int NSDeveloperApplicationDirectory = 3;
        public final int NSAdminApplicationDirectory = 4;
        public final int NSLibraryDirectory = 5;
        public final int NSDeveloperDirectory = 6;
        public final int NSUserDirectory = 7;
        public final int NSDocumentationDirectory = 8;
        public final int NSDocumentDirectory = 9;
        public final int NSCoreServiceDirectory = 10;
        public final int NSAutosavedInformationDirectory = 11;
        public final int NSDesktopDirectory = 12;
        public final int NSCachesDirectory = 13;
        public final int NSApplicationSupportDirectory = 14;
        public final int NSDownloadsDirectory = 15;
        public final int NSInputMethodsDirectory = 16;
        public final int NSMoviesDirectory = 17;
        public final int NSMusicDirectory = 18;
        public final int NSPicturesDirectory = 19;
        public final int NSPrinterDescriptionDirectory = 20;
        public final int NSSharedPublicDirectory = 21;
        public final int NSPreferencePanesDirectory = 22;
        public final int NSApplicationScriptsDirectory = 23;
        public final int NSItemReplacementDirectory = 99;
        public final int NSAllApplicationsDirectory = 100;
        public final int NSAllLibrariesDirectory = 101;
        public final int NSTrashDirectory = 102;
    }

    // http://developer.apple.com/library/mac/#documentation/Cocoa/Reference/Foundation/Miscellaneous/Foundation_Constants/Reference/reference.html#//apple_ref/doc/c_ref/NSSearchPathDomainMask

    public interface NSSearchPathDomainMask {
        public final int NSUserDomainMask = 1;
        public final int NSLocalDomainMask = 2;
        public final int NSNetworkDomainMask = 4;
        public final int NSSystemDomainMask = 8;
        public final int NSAllDomainsMask = 0x0ffff;
    };

    // http://developer.apple.com/library/mac/#documentation/Cocoa/Reference/Foundation/Miscellaneous/Foundation_Functions/Reference/reference.html#//apple_ref/c/func/NSSearchPathForDirectoriesInDomains

    /**
     * NSArray * NSSearchPathForDirectoriesInDomains ( NSSearchPathDirectory
     * directory, NSSearchPathDomainMask domainMask, BOOL expandTilde );
     * 
     * @param directory
     * @param domainMask
     * @param expandTilde
     * @return
     */
    public CFArrayRef NSSearchPathForDirectoriesInDomains(int directory, int domainMask, boolean expandTilde);
}
