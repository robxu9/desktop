package com.github.axet.desktop.os.win.handle;

import java.util.Arrays;
import java.util.List;

import com.github.axet.desktop.os.win.GUID;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.HICON;
import com.sun.jna.platform.win32.WinDef.HWND;

// http://msdn.microsoft.com/en-us/library/windows/desktop/bb773352(v=vs.85).aspx

/**
 * typedef struct _NOTIFYICONDATA { DWORD cbSize; HWND hWnd; UINT uID; UINT
 * uFlags; UINT uCallbackMessage; HICON hIcon; TCHAR szTip[64]; DWORD dwState;
 * DWORD dwStateMask; TCHAR szInfo[256]; union { UINT uTimeout; UINT uVersion;
 * }; TCHAR szInfoTitle[64]; DWORD dwInfoFlags; GUID guidItem; HICON
 * hBalloonIcon; } NOTIFYICONDATA, *PNOTIFYICONDATA;
 * 
 */
public class NOTIFYICONDATA extends Structure {

    public static final int NIF_ICON = 0x01;
    public static final int NIF_MESSAGE = 0x02;
    public static final int NIF_TIP = 0x04;
    public static final int NIF_INFO = 0x10;

    public static class ByValue extends NOTIFYICONDATA implements Structure.ByValue {
    }

    public static class ByReference extends NOTIFYICONDATA implements Structure.ByReference {
    }

    public NOTIFYICONDATA() {
        uFlags = NIF_INFO | NIF_ICON | NIF_MESSAGE | NIF_TIP;
        cbSize = size();
    }

    @Override
    protected List<?> getFieldOrder() {
        return Arrays.asList(new String[] { "cbSize", "hWnd", "uID", "uFlags", "uCallbackMessage", "hIcon", "szTip",
                "dwState", "dwStateMask", "szInfo", "union", "szInfoTitle", "dwInfoFlags", "uidItem", "hBalloonIcon" });
    }

    public int cbSize = 0;
    public HWND hWnd = null;
    public int uID = 0;
    public int uFlags = 0;
    public int uCallbackMessage = 0;
    public HICON hIcon = null;
    public char[] szTip = new char[64];
    public int dwState = 0;
    public int dwStateMask = 0;
    public char[] szInfo = new char[256];
    public int union; // {UINT uTimeout; UINT uVersion;};
    public char[] szInfoTitle = new char[64];
    public int dwInfoFlags = 0;
    public GUID guidItem = null;
    public HICON hBalloonIcon = null;

    public void setTooltip(String s) {
        System.arraycopy(s.toCharArray(), 0, szTip, 0, Math.min(s.length(), szTip.length));
        System.arraycopy(s.toCharArray(), 0, szInfoTitle, 0, Math.min(s.length(), szInfoTitle.length));
        System.arraycopy(s.toCharArray(), 0, szInfo, 0, Math.min(s.length(), szInfo.length));
    }
}