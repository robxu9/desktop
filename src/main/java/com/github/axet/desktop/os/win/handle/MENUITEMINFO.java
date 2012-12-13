package com.github.axet.desktop.os.win.handle;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HMENU;

// http://msdn.microsoft.com/en-us/library/windows/desktop/ms647578(v=vs.85).aspx
/**
 * typedef struct tagMENUITEMINFO { UINT cbSize; UINT fMask; UINT fType; UINT
 * fState; UINT wID; HMENU hSubMenu; HBITMAP hbmpChecked; HBITMAP hbmpUnchecked;
 * ULONG_PTR dwItemData; LPTSTR dwTypeData; UINT cch; HBITMAP hbmpItem; }
 * MENUITEMINFO, *LPMENUITEMINFO;
 */
public class MENUITEMINFO extends Structure {

    public static final int MFS_CHECKED = 0x00000008;
    public static final int MFS_DEFAULT = 0x00001000;
    public static final int MFS_DISABLED = 0x00000003;
    public static final int MFS_ENABLED = 0x00000000;
    public static final int MFS_GRAYED = 0x00000003;
    public static final int MFS_HILITE = 0x00000080;
    public static final int MFS_UNCHECKED = 0x00000000;
    public static final int MFS_UNHILITE = 0x00000000;
    public static final int MIIM_DATA = 0x00000020;

    public static class ByValue extends MENUITEMINFO implements Structure.ByValue {
    }

    public static class ByReference extends MENUITEMINFO implements Structure.ByReference {
    }

    public MENUITEMINFO() {
        cbSize = size();
    }

    public MENUITEMINFO(Pointer p) {
        super(p);
    }

    public int cbSize;
    public int fMask;
    public int fType;
    public int fState;
    public int wID;
    public HMENU hSubMenu;
    public HBITMAP hbmpChecked;
    public HBITMAP hbmpUnchecked;
    public ULONG_PTR dwItemData;
    public String dwTypeData;
    public int cch;
    public HBITMAP hbmpItem;
}