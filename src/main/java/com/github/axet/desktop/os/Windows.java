package com.github.axet.desktop.os;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;

import com.github.axet.desktop.Desktop;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeMapped;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.win32.W32APIFunctionMapper;
import com.sun.jna.win32.W32APITypeMapper;

/**
 * Windows helper.
 * 
 * http://stackoverflow.com/questions/585534/what-is-the-best-way-to-find-the-
 * users-home-directory-in-java
 * 
 */
public class Windows extends Desktop {

    public File getHome() {
        return new File(System.getenv("USERPROFILE"));
    }

    public File getDocuments() {
        return path(Shell32.CSIDL_PERSONAL);
    }

    public File getDownloads() {
        // xp has no default downloads folder. so be it My Documents :)
        if (SystemUtils.IS_OS_WINDOWS_XP)
            return getDocuments();

        return getDownloadsVista();
    }

    /**
     * http://stackoverflow.com/questions/7672774/how-do-i-determine-the-windows
     * -download-folder-path
     * 
     * @return
     */
    public File getDownloadsVista() {
        GUID guid = new GUID("374DE290-123F-4565-9164-39C4925E467B");

        HANDLE hToken = null;
        int dwFlags = Shell32.SHGFP_TYPE_CURRENT;
        char[] pszPath = new char[Shell32.MAX_PATH];
        int hResult = Shell32.INSTANCE.SHGetKnownFolderPath(guid, dwFlags, hToken, pszPath);
        if (Shell32.S_OK == hResult) {
            String path = new String(pszPath);
            int len = path.indexOf('\0');
            path = path.substring(0, len);
            return new File(path);
        } else {
            throw new RuntimeException("Error: " + hResult);
        }
    }

    @Override
    public File getAppData() {
        return path(Shell32.CSIDL_LOCAL_APPDATA);
    }

    @Override
    public File getDesktop() {
        return path(Shell32.CSIDL_DESKTOPDIRECTORY);
    }

    public File path(int nFolder) {
        HWND hwndOwner = null;
        HANDLE hToken = null;
        int dwFlags = Shell32.SHGFP_TYPE_CURRENT;
        char[] pszPath = new char[Shell32.MAX_PATH];
        int hResult = Shell32.INSTANCE.SHGetFolderPath(hwndOwner, nFolder, hToken, dwFlags, pszPath);
        if (Shell32.S_OK == hResult) {
            String path = new String(pszPath);
            int len = path.indexOf('\0');
            path = path.substring(0, len);
            return new File(path);
        } else {
            throw new RuntimeException("Error: " + hResult);
        }
    }

    private static Map<String, Object> OPTIONS = new HashMap<String, Object>();
    static {
        OPTIONS.put(Library.OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
        OPTIONS.put(Library.OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.UNICODE);
    }

    static class HANDLE extends PointerType implements NativeMapped {
    }

    static class HWND extends HANDLE {
    }

    public static class GUID extends Structure {

        public static class ByValue extends GUID implements Structure.ByValue {
        }

        public byte[] data;

        public GUID() {
        }

        public GUID(String g) {
            Pattern p = Pattern.compile("(\\w+)-(\\w+)-(\\w+)-(\\w+)-(\\w+)");
            Matcher m = p.matcher(g);
            if (!m.find())
                throw new RuntimeException("bad guid");
            int l1 = Integer.parseInt(m.group(1), 16);
            int l2 = Integer.parseInt(m.group(2), 16);
            int l3 = Integer.parseInt(m.group(3), 16);
            int l4 = Integer.parseInt(m.group(4), 16);

            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(bos);
                dos.writeInt(l1);
                dos.writeShort(l2);
                dos.writeShort(l3);
                dos.writeShort(l4);
                for (String c : m.group(5).split("(?<=\\G.{2})")) {
                    int bb = Integer.parseInt(c, 16);
                    dos.writeByte(bb);
                }
                dos.flush();

                data = bos.toByteArray();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    static interface Shell32 extends Library {
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

        static Shell32 INSTANCE = (Shell32) Native.loadLibrary("shell32", Shell32.class, OPTIONS);

        /**
         * see http://msdn.microsoft.com/en-us/library/bb762181(VS.85).aspx
         * 
         * HRESULT SHGetFolderPath( HWND hwndOwner, int nFolder, HANDLE hToken,
         * DWORD dwFlags, LPTSTR pszPath);
         */
        public int SHGetFolderPath(HWND hwndOwner, int nFolder, HANDLE hToken, int dwFlags, char[] pszPath);

        /**
         * HRESULT SHGetKnownFolderPath( _In_ REFKNOWNFOLDERID rfid, _In_ DWORD
         * dwFlags, _In_opt_ HANDLE hToken, _Out_ PWSTR *ppszPath );
         */

        public int SHGetKnownFolderPath(GUID rfid, int dwFlags, HANDLE hToken, char[] ppszPath);

    }
}