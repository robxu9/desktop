package com.github.axet.desktop.os.win.libs;

import com.github.axet.desktop.os.win.handle.ATOM;
import com.github.axet.desktop.os.win.handle.ICONINFO;
import com.github.axet.desktop.os.win.handle.WNDCLASSEX;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HICON;
import com.sun.jna.platform.win32.WinDef.HINSTANCE;
import com.sun.jna.platform.win32.WinDef.HMENU;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.W32APIOptions;

public interface User32Ex extends Library {

    public static final int WS_OVERLAPPEDWINDOW = 0xcf0000;

    static User32Ex INSTANCE = (User32Ex) Native.loadLibrary("user32", User32Ex.class, W32APIOptions.DEFAULT_OPTIONS);

    // http://msdn.microsoft.com/en-us/library/windows/desktop/ms648062(v=vs.85).aspx

    /**
     * HICON WINAPI CreateIconIndirect( _In_ PICONINFO piconinfo );
     */

    HICON CreateIconIndirect(ICONINFO piconinfo);

    // http://msdn.microsoft.com/en-us/library/windows/desktop/ms632680(v=vs.85).aspx

    /**
     * HWND WINAPI CreateWindowEx( _In_ DWORD dwExStyle, _In_opt_ LPCTSTR
     * lpClassName, _In_opt_ LPCTSTR lpWindowName, _In_ DWORD dwStyle, _In_ int
     * x, _In_ int y, _In_ int nWidth, _In_ int nHeight, _In_opt_ HWND
     * hWndParent, _In_opt_ HMENU hMenu, _In_opt_ HINSTANCE hInstance, _In_opt_
     * LPVOID lpParam );
     */
    HWND CreateWindowEx(int dwExStyle, String lpClassName, String lpWindowName, int dwStyle, int x, int y, int nWidth,
            int nHeight, HWND hWndParent, HMENU hMenu, HINSTANCE hInstance, HANDLE lpParam);

    // http://msdn.microsoft.com/en-us/library/windows/desktop/ms633587(v=vs.85).aspx

    /**
     * ATOM WINAPI RegisterClassEx( _In_ const WNDCLASSEX *lpwcx );
     */
    ATOM RegisterClassEx(WNDCLASSEX lpwcx);

    // http://msdn.microsoft.com/en-us/library/windows/desktop/ms644899(v=vs.85).aspx

    /**
     * BOOL WINAPI UnregisterClass( _In_ LPCTSTR lpClassName, _In_opt_ HINSTANCE
     * hInstance );
     */
    boolean UnregisterClass(String lpClassName, HINSTANCE hInstance);

    // http://msdn.microsoft.com/en-us/library/windows/desktop/ms633579(v=vs.85).aspx

    /**
     * 
     BOOL WINAPI GetClassInfoEx( _In_opt_ HINSTANCE hinst, _In_ LPCTSTR
     * lpszClass, _Out_ LPWNDCLASSEX lpwcx );
     */

    boolean GetClassInfoEx(HINSTANCE hinst, String lpszClass, WNDCLASSEX.ByReference lpwcx);

    // http://msdn.microsoft.com/en-us/library/windows/desktop/ms633572(v=vs.85).aspx

    /**
     * LRESULT WINAPI DefWindowProc( _In_ HWND hWnd, _In_ UINT Msg, _In_ WPARAM
     * wParam, _In_ LPARAM lParam );
     */
    LRESULT DefWindowProc(HWND hWnd, int Msg, WPARAM wParam, LPARAM lParam);

    // http://msdn.microsoft.com/en-us/library/windows/desktop/ms644947(v=vs.85).aspx

    /**
     * UINT WINAPI RegisterWindowMessage( _In_ LPCTSTR lpString );
     */
    int RegisterWindowMessage(String lpString);
}
