package com.github.axet.desktop.os.win;

import java.io.File;

import org.apache.commons.lang.SystemUtils;

import com.github.axet.desktop.DesktopFolders;
import com.github.axet.desktop.os.win.libs.Ole32Ex;
import com.github.axet.desktop.os.win.libs.Shell32Ex;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.PointerByReference;

/**
 * Windows helper.
 * 
 * http://stackoverflow.com/questions/585534/what-is-the-best-way-to-find-the-
 * users-home-directory-in-java
 * 
 */
public class WindowsFolders implements DesktopFolders {

    public File getHome() {
        return new File(System.getenv("USERPROFILE"));
    }

    public File getDocuments() {
        return path(Shell32Ex.CSIDL_PERSONAL);
    }

    public File getDownloads() {
        // xp has no default downloads folder. so be it My Documents :)
        if (SystemUtils.IS_OS_WINDOWS_XP)
            return getDocuments();

        return getDownloadsVista();
    }

    //
    // http://stackoverflow.com/questions/7672774/how-do-i-determine-the-windows-download-folder-path
    //

    /**
     * 
     * 
     * @return
     */
    public File getDownloadsVista() {
        GUID guid = new GUID("374DE290-123F-4565-9164-39C4925E467B");

        int dwFlags = Shell32Ex.SHGFP_TYPE_CURRENT;
        PointerByReference pszPath = new PointerByReference();

        int hResult = Shell32Ex.INSTANCE.SHGetKnownFolderPath(guid, dwFlags, null, pszPath);
        switch (hResult) {
        case Shell32Ex.S_FILE_NOT_FOUND:
            throw new RuntimeException("File not Found");
        case Shell32Ex.S_OK:
            String path = new String(pszPath.getValue().getString(0, true));
            Ole32Ex.INSTANCE.CoTaskMemFree(pszPath.getValue());
            return new File(path);
        default:
            throw new RuntimeException("Error: " + Integer.toHexString(hResult));
        }
    }

    @Override
    public File getAppData() {
        return path(Shell32Ex.CSIDL_LOCAL_APPDATA);
    }

    @Override
    public File getDesktop() {
        return path(Shell32Ex.CSIDL_DESKTOPDIRECTORY);
    }

    public File path(int nFolder) {
        HWND hwndOwner = null;
        HANDLE hToken = null;
        int dwFlags = Shell32Ex.SHGFP_TYPE_CURRENT;
        char[] pszPath = new char[Shell32Ex.MAX_PATH];
        int hResult = Shell32Ex.INSTANCE.SHGetFolderPath(hwndOwner, nFolder, hToken, dwFlags, pszPath);
        if (Shell32Ex.S_OK == hResult) {
            String path = new String(pszPath);
            int len = path.indexOf('\0');
            path = path.substring(0, len);
            return new File(path);
        } else {
            throw new HResultException(hResult);
        }
    }

}