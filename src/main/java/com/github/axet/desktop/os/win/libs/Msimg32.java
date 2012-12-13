package com.github.axet.desktop.os.win.libs;

import com.github.axet.desktop.os.win.handle.BLENDFUNCTION;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.win32.W32APIOptions;

public interface Msimg32 extends Library {

    public static final int ETO_OPAQUE = 2;
    public static final int SRCCOPY = 0xCC0020;

    static Msimg32 INSTANCE = (Msimg32) Native.loadLibrary("Msimg32", Msimg32.class, W32APIOptions.DEFAULT_OPTIONS);

    // http://msdn.microsoft.com/en-us/library/windows/desktop/dd183351(v=vs.85).aspx
    /**
     * 
     BOOL AlphaBlend( _In_ HDC hdcDest, _In_ int xoriginDest, _In_ int
     * yoriginDest, _In_ int wDest, _In_ int hDest, _In_ HDC hdcSrc, _In_ int
     * xoriginSrc, _In_ int yoriginSrc, _In_ int wSrc, _In_ int hSrc, _In_
     * BLENDFUNCTION ftn );
     */
    boolean AlphaBlend(HDC hdcDest, int xoriginDest, int yoriginDest, int wDest, int hDest, HDC hdcSrc, int xoriginSrc,
            int yoriginSrc, int wSrc, int hSrc, BLENDFUNCTION.ByValue ftn);

}
