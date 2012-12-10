package com.github.axet.desktop.os.win.handle;

import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.win32.StdCallLibrary.StdCallCallback;

public interface WNDPROC extends StdCallCallback {
    LRESULT callback(HWND hWnd, int uMsg, WPARAM uParam, LPARAM lParam);
}
