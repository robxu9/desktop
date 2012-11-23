package com.github.axet.desktop.os.mac;

import java.io.File;
import java.lang.reflect.Method;

import com.github.axet.desktop.Desktop;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public class OSX extends Desktop {

    @Override
    public File getAppData() {
        // From CarbonCore/Folders.h
        return path("asup");
    }

    @Override
    public File getHome() {
        return new File(System.getenv("HOME"));
    }

    @Override
    public File getDocuments() {
        // From CarbonCore/Folders.h
        return path("docs");
    }

    @Override
    public File getDownloads() {
        Pointer p = NSFileNanager.INSTANCE.NSSearchPathForDirectoriesInDomains(NSFileNanager.NSDownloadsDirectory,
                NSFileNanager.NSUserDomainMask, true);

        int count = CoreFoundation.INSTANCE.CFArrayGetCount(p);
        if (count != 1)
            throw new RuntimeException("Download folder not found");

        Pointer pp = CoreFoundation.INSTANCE.CFArrayGetValueAtIndex(p, 0);
        String path = CFStringRef.toString(pp);

        return new File(path);
    }

    private static Class<?> FileManagerClass;
    private static Method OSTypeToInt;
    private static Short kUserDomain;

    protected static Class<?> getFileManagerClass() {
        if (FileManagerClass == null) {
            try {
                FileManagerClass = Class.forName("com.apple.eio.FileManager");
                OSTypeToInt = FileManagerClass.getMethod("OSTypeToInt", String.class);
                kUserDomain = (Short) FileManagerClass.getField("kUserDomain").get(null);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return FileManagerClass;
    }

    File path(String p) {
        try {
            final Method findFolder = getFileManagerClass().getMethod("findFolder", Short.TYPE, Integer.TYPE);
            final String path = (String) findFolder.invoke(null, kUserDomain, OSTypeToInt.invoke(null, p));
            return new File(path);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
