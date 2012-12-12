package com.github.axet.desktop.os.win.libs;

import java.util.HashMap;
import java.util.Map;

import com.github.axet.desktop.os.win.GUID;
import com.github.axet.desktop.os.win.handle.NOTIFYICONDATA;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.W32APIFunctionMapper;
import com.sun.jna.win32.W32APITypeMapper;

public interface Shell32Ex extends Library {
    public static final int MAX_PATH = 260;

    // Local Settings\Application Data
    public static final int CSIDL_LOCAL_APPDATA = 0x001c;
    // ~/My Documents
    public static final int CSIDL_PERSONAL = 0x005;
    // ~/Desktop
    public static final int CSIDL_DESKTOPDIRECTORY = 0x10;

    public static final int SHGFP_TYPE_CURRENT = 0;
    public static final int SHGFP_TYPE_DEFAULT = 1;
    public static final int S_OK = 0;
    public static final int S_FILE_NOT_FOUND = 0x80070002;

    public static final int NIM_ADD = 0;
    public static final int NIM_MODIFY = 1;
    public static final int NIM_DELETE = 2;

    public static final Map<String, Object> OPTIONS = new HashMap<String, Object>() {
        private static final long serialVersionUID = 5531778097995782635L;
        {
            put(Library.OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
            put(Library.OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.UNICODE);
        }
    };

    static Shell32Ex INSTANCE = (Shell32Ex) Native.loadLibrary("shell32", Shell32Ex.class, OPTIONS);

    // http://msdn.microsoft.com/en-us/library/bb762181(VS.85).aspx

    /**
     * HRESULT SHGetFolderPath( HWND hwndOwner, int nFolder, HANDLE hToken,
     * DWORD dwFlags, LPTSTR pszPath);
     */
    public int SHGetFolderPath(HWND hwndOwner, int nFolder, HANDLE hToken, int dwFlags, char[] pszPath);

    /**
     * HRESULT SHGetKnownFolderPath( _In_ REFKNOWNFOLDERID rfid, _In_ DWORD
     * dwFlags, _In_opt_ HANDLE hToken, _Out_ PWSTR *ppszPath );
     */

    public int SHGetKnownFolderPath(GUID rfid, int dwFlags, HANDLE hToken, PointerByReference pszPath);

    // http://msdn.microsoft.com/en-us/library/windows/desktop/bb762159(v=vs.85).aspx

    /**
     * BOOL Shell_NotifyIcon( _In_ DWORD dwMessage, _In_ PNOTIFYICONDATA lpdata
     * );
     */
    boolean Shell_NotifyIcon(int dwMessage, NOTIFYICONDATA lpdata);
}
