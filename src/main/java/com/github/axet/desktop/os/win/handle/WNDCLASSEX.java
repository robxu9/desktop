package com.github.axet.desktop.os.win.handle;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinDef.HCURSOR;
import com.sun.jna.platform.win32.WinDef.HICON;
import com.sun.jna.platform.win32.WinDef.HINSTANCE;
import com.sun.jna.platform.win32.WinNT.HANDLE;

// http://msdn.microsoft.com/en-us/library/windows/desktop/ms633577(v=vs.85).aspx

/**
 * typedef struct tagWNDCLASSEX { UINT cbSize; UINT style; WNDPROC lpfnWndProc;
 * int cbClsExtra; int cbWndExtra; HINSTANCE hInstance; HICON hIcon; HCURSOR
 * hCursor; HBRUSH hbrBackground; LPCTSTR lpszMenuName; LPCTSTR lpszClassName;
 * HICON hIconSm; } WNDCLASSEX, *PWNDCLASSEX;
 * 
 * @author axet
 * 
 */
public class WNDCLASSEX extends Structure {
    public int cbSize;
    public int style;
    public WNDPROC lpfnWndProc;
    public int cbClsExtra;
    public int cbWndExtra;
    public HINSTANCE hInstance;
    public HICON hIcon;
    public HCURSOR hCursor;
    public HANDLE hbrBackground;
    public WString lpszMenuName;
    public WString lpszClassName;
    public HICON hIconSm;

    @Override
    protected List getFieldOrder() {
        return Arrays.asList(new String[] { "cbSize", "style", "lpfnWndProc", "cbClsExtra", "cbWndExtra", "hInstance",
                "hIcon", "hCursor", "hbrBackground", "lpszMenuName", "lpszClassName", "hIconSm" });
    }

    public static class ByReference extends WNDCLASSEX implements Structure.ByReference {
    }
}
