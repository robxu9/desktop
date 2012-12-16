package com.github.axet.desktop.os.win.handle;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.WPARAM;

public class CWPSSTRUCT extends Structure {
    public LPARAM lParam;
    public WPARAM wParam;
    public int message;
    public HWND hwnd;

    @Override
    protected List getFieldOrder() {
        return Arrays.asList(new String[] { "lParam", "wParam", "message", "hwnd" });
    }
}