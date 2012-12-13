package com.github.axet.desktop.os.win.libs;

import com.github.axet.desktop.os.win.handle.ATOM;
import com.github.axet.desktop.os.win.handle.COLORREF;
import com.github.axet.desktop.os.win.handle.ICONINFO;
import com.github.axet.desktop.os.win.handle.MENUITEMINFO;
import com.github.axet.desktop.os.win.handle.WNDCLASSEX;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HICON;
import com.sun.jna.platform.win32.WinDef.HINSTANCE;
import com.sun.jna.platform.win32.WinDef.HMENU;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.W32APIOptions;

public interface User32Ex extends Library {

    public static final int WS_OVERLAPPEDWINDOW = 0xcf0000;
    public static final int SPI_GETNONCLIENTMETRICS = 0x0029;
    public static final int COLOR_MENU = 4;
    public static final int COLOR_MENUTEXT = 7;
    public static final int COLOR_HIGHLIGHTTEXT = 14;
    public static final int COLOR_HIGHLIGHT = 13;

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

    // http://msdn.microsoft.com/en-us/library/windows/desktop/ms632682(v=vs.85).aspx

    /**
     * BOOL WINAPI DestroyWindow( _In_ HWND hWnd );
     */
    boolean DestroyWindow(HWND hWnd);

    // http://msdn.microsoft.com/en-us/library/windows/desktop/ms644950(v=vs.85).aspx

    /**
     * LRESULT WINAPI SendMessage( _In_ HWND hWnd, _In_ UINT Msg, _In_ WPARAM
     * wParam, _In_ LPARAM lParam );
     */
    LRESULT SendMessage(HWND hWnd, int Msg, WPARAM wParam, LPARAM lParam);

    // http://msdn.microsoft.com/en-us/library/windows/desktop/ms647626(v=vs.85).aspx
    /**
     * HMENU WINAPI CreatePopupMenu(void);
     */
    HMENU CreatePopupMenu();

    // http://msdn.microsoft.com/en-us/library/windows/desktop/ms647616(v=vs.85).aspx
    /**
     * BOOL WINAPI AppendMenu( _In_ HMENU hMenu, _In_ UINT uFlags, _In_ UINT_PTR
     * uIDNewItem, _In_opt_ LPCTSTR lpNewItem );
     */
    boolean AppendMenu(HMENU hMenu, int uFlags, int uIDNewItem, String lpNewItem);

    // http://msdn.microsoft.com/en-us/library/windows/desktop/ms647998(v=vs.85).aspx
    /**
     * BOOL WINAPI SetMenuItemBitmaps( _In_ HMENU hMenu, _In_ UINT uPosition,
     * _In_ UINT uFlags, _In_opt_ HBITMAP hBitmapUnchecked, _In_opt_ HBITMAP
     * hBitmapChecked );
     */
    boolean SetMenuItemBitmaps(HMENU hMenu, int uPosition, int uFlags, HBITMAP hBitmapUnchecked, HBITMAP hBitmapChecked);

    // http://msdn.microsoft.com/en-us/library/windows/desktop/ms647631(v=vs.85).aspx
    /**
     * BOOL WINAPI DestroyMenu( _In_ HMENU hMenu );
     */
    boolean DestroyMenu(HMENU hMenu);

    // http://msdn.microsoft.com/en-us/library/windows/desktop/ms648002(v=vs.85).aspx
    /**
     * BOOL WINAPI TrackPopupMenu( _In_ HMENU hMenu, _In_ UINT uFlags, _In_ int
     * x, _In_ int y, _In_ int nReserved, _In_ HWND hWnd, _In_opt_ const RECT
     * *prcRect );
     */
    boolean TrackPopupMenu(HMENU hMenu, int uFlags, int x, int y, int nReserved, HWND hWnd, RECT prcRect);

    // http://msdn.microsoft.com/en-us/library/windows/desktop/ms648001(v=vs.85).aspx
    /**
     * BOOL WINAPI SetMenuItemInfo( _In_ HMENU hMenu, _In_ UINT uItem, _In_ BOOL
     * fByPosition, _In_ LPMENUITEMINFO lpmii );
     */
    boolean SetMenuItemInfo(HMENU hMenu, int uItem, boolean fByPosition, MENUITEMINFO lpmii);

    // http://msdn.microsoft.com/en-us/library/windows/desktop/ms647980(v=vs.85).aspx
    /**
     * BOOL WINAPI GetMenuItemInfo( _In_ HMENU hMenu, _In_ UINT uItem, _In_ BOOL
     * fByPosition, _Inout_ LPMENUITEMINFO lpmii );
     */
    boolean GetMenuItemInfo(HMENU hMenu, int uItem, boolean fByPosition, MENUITEMINFO lpmii);

    // http://msdn.microsoft.com/en-us/library/windows/desktop/ms724947(v=vs.85).aspx
    /**
     * BOOL WINAPI SystemParametersInfo( _In_ UINT uiAction, _In_ UINT uiParam,
     * _Inout_ PVOID pvParam, _In_ UINT fWinIni );
     */
    boolean SystemParametersInfo(int uiAction, int uiParam, Structure pvParam, int fWinIni);

    // http://msdn.microsoft.com/en-us/library/windows/desktop/ms724371(v=vs.85).aspx
    /**
     * 
     DWORD WINAPI GetSysColor( _In_ int nIndex );
     */
    COLORREF GetSysColor(int nIndex);

}
