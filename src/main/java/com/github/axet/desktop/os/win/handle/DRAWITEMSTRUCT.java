package com.github.axet.desktop.os.win.handle;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;

// http://msdn.microsoft.com/en-us/library/windows/desktop/bb775802(v=vs.85).aspx

/**
 * typedef struct tagDRAWITEMSTRUCT { UINT CtlType; UINT CtlID; UINT itemID;
 * UINT itemAction; UINT itemState; HWND hwndItem; HDC hDC; RECT rcItem;
 * ULONG_PTR itemData; } DRAWITEMSTRUCT;
 */
public class DRAWITEMSTRUCT extends Structure {

    public static final int ODT_BUTTON = 4;
    public static final int ODT_COMBOBOX = 3;
    public static final int ODT_LISTBOX = 2;
    public static final int ODT_LISTVIEW = 102;
    public static final int ODT_MENU = 1;
    public static final int ODT_STATIC = 5;
    public static final int ODT_TAB = 101;

    public static final int ODS_SELECTED = 1;

    public static class ByValue extends DRAWITEMSTRUCT implements Structure.ByValue {
    }

    public static class ByReference extends DRAWITEMSTRUCT implements Structure.ByReference {
    }

    public DRAWITEMSTRUCT() {
    }

    public DRAWITEMSTRUCT(Pointer p) {
        super(p);

        read();
    }

    @Override
    protected List getFieldOrder() {
        return Arrays.asList(new String[] { "CtlType", "CtlID", "itemID", "itemAction", "itemState", "hwndItem", "hDC",
                "rcItem", "itemData" });
    }

    public int CtlType;
    public int CtlID;
    public int itemID;
    public int itemAction;
    public int itemState;
    public HWND hwndItem;
    public HDC hDC;
    public RECT rcItem;
    public ULONG_PTR itemData;
}