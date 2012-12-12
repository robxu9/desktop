package com.github.axet.desktop.os.win.handle;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HBITMAP;

// http://msdn.microsoft.com/en-us/library/windows/desktop/ms648052(v=vs.85).aspx

/**
 * typedef struct _ICONINFO { BOOL fIcon; DWORD xHotspot; DWORD yHotspot;
 * HBITMAP hbmMask; HBITMAP hbmColor; } ICONINFO, *PICONINFO;
 * 
 */
public class ICONINFO extends Structure {
    public static class ByValue extends ICONINFO implements Structure.ByValue {
    }

    public ICONINFO() {
    }

    @Override
    protected List<?> getFieldOrder() {
        return Arrays.asList(new String[] { "IsIcon", "xHotspot", "yHotspot", "MaskBitmap", "ColorBitmap" });
    }

    public boolean IsIcon;
    public DWORD xHotspot;
    public DWORD yHotspot;
    public HBITMAP MaskBitmap;
    public HBITMAP ColorBitmap;

}