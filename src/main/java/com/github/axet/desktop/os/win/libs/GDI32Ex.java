package com.github.axet.desktop.os.win.libs;

import com.github.axet.desktop.os.win.handle.COLORREF;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinUser.SIZE;
import com.sun.jna.win32.W32APIOptions;

public interface GDI32Ex extends Library {

    public static final int ETO_OPAQUE = 2;
    public static final int SRCCOPY = 0xCC0020;

    static GDI32Ex INSTANCE = (GDI32Ex) Native.loadLibrary("GDI32", GDI32Ex.class, W32APIOptions.DEFAULT_OPTIONS);

    // http://msdn.microsoft.com/en-us/library/windows/desktop/dd144938(v=vs.85).aspx
    /**
     * BOOL GetTextExtentPoint32( _In_ HDC hdc, _In_ LPCTSTR lpString, _In_ int
     * c, _Out_ LPSIZE lpSize );
     */
    boolean GetTextExtentPoint32(HDC hdc, String lpString, int c, SIZE lpSize);

    // http://msdn.microsoft.com/en-us/library/windows/desktop/dd145093(v=vs.85).aspx
    /**
     * COLORREF SetTextColor( _In_ HDC hdc, _In_ COLORREF crColor );
     */
    COLORREF SetTextColor(HDC hdc, COLORREF crColor);

    // http://msdn.microsoft.com/en-us/library/windows/desktop/dd162964(v=vs.85).aspx
    /**
     * COLORREF SetBkColor( _In_ HDC hdc, _In_ COLORREF crColor );
     */
    COLORREF SetBkColor(HDC hdc, COLORREF crColor);

    // http://msdn.microsoft.com/en-us/library/windows/desktop/dd162713(v=vs.85).aspx
    /**
     * 
     BOOL ExtTextOut( _In_ HDC hdc, _In_ int X, _In_ int Y, _In_ UINT
     * fuOptions, _In_ const RECT *lprc, _In_ LPCTSTR lpString, _In_ UINT
     * cbCount, _In_ const INT *lpDx );
     */

    boolean ExtTextOut(HDC hdc, int X, int Y, int fuOptions, RECT lprc, String lpString, int cbCount, int[] lpDx);

    // http://msdn.microsoft.com/en-us/library/windows/desktop/dd183370(v=vs.85).aspx
    /**
     * BOOL BitBlt( _In_ HDC hdcDest, _In_ int nXDest, _In_ int nYDest, _In_ int
     * nWidth, _In_ int nHeight, _In_ HDC hdcSrc, _In_ int nXSrc, _In_ int
     * nYSrc, _In_ DWORD dwRop );
     */
    boolean BitBlt(HDC hdcDest, int nXDest, int nYDest, int nWidth, int nHeight, HDC hdcSrc, int nXSrc, int nYSrc,
            int dwRop);

}
