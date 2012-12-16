package com.github.axet.desktop.os.win.handle;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.LONG;

// http://msdn.microsoft.com/en-us/library/windows/desktop/ms686735(v=vs.85).aspx
/**
 * typedef struct tagTHREADENTRY32 { DWORD dwSize; DWORD cntUsage; DWORD
 * th32ThreadID; DWORD th32OwnerProcessID; LONG tpBasePri; LONG tpDeltaPri;
 * DWORD dwFlags; } THREADENTRY32, *PTHREADENTRY32;
 * 
 */
public class THREADENTRY32 extends Structure {
    public static class ByValue extends THREADENTRY32 implements Structure.ByValue {
    }

    public static class ByReference extends THREADENTRY32 implements Structure.ByReference {
    }

    public THREADENTRY32() {
        dwSize = size();
    }

    @Override
    protected List getFieldOrder() {
        return Arrays.asList(new String[] { "dwSize", "cntUsage", "th32ThreadID", "th32OwnerProcessID", "tpBasePri",
                "tpDeltaPri", "dwFlags" });
    }

    public int dwSize;
    public int cntUsage;
    public int th32ThreadID;
    public int th32OwnerProcessID;
    public int tpBasePri;
    public int tpDeltaPri;
    public int dwFlags;
}
