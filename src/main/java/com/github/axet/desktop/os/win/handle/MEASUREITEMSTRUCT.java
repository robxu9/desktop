package com.github.axet.desktop.os.win.handle;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;

// http://msdn.microsoft.com/en-us/library/windows/desktop/bb775804(v=vs.85).aspx

/**
 * typedef struct MEASUREITEMSTRUCT { UINT CtlType; UINT CtlID; UINT itemID;
 * UINT itemWidth; UINT itemHeight; ULONG_PTR itemData; } MEASUREITEMSTRUCT;
 */
public class MEASUREITEMSTRUCT extends Structure {

    public static final int ODT_MENU = 1;
    public static final int ODT_LISTBOX = 2;
    public static final int ODT_COMBOBOX = 3;
    public static final int ODT_BUTTON = 4;
    public static final int ODT_STATIC = 5;

    public static class ByValue extends MEASUREITEMSTRUCT implements Structure.ByValue {
    }

    public static class ByReference extends MEASUREITEMSTRUCT implements Structure.ByReference {
    }

    public MEASUREITEMSTRUCT() {
    }

    public MEASUREITEMSTRUCT(Pointer p) {
        super(p);

        read();
    }

    @Override
    protected List getFieldOrder() {
        return Arrays.asList(new String[] { "CtlType", "CtlID", "itemID", "itemWidth", "itemHeight", "itemData" });
    }

    public int CtlType;
    public int CtlID;
    public int itemID;
    public int itemWidth;
    public int itemHeight;
    public ULONG_PTR itemData;

}