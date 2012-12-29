package com.github.axet.desktop.os.win.handle;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinUser;

public class BLENDFUNCTION extends Structure {
    public static class ByValue extends BLENDFUNCTION implements Structure.ByValue {
    }

    public static class ByReference extends BLENDFUNCTION implements Structure.ByReference {
    }

    @Override
    protected List<?> getFieldOrder() {
        return Arrays.asList(new String[] { "BlendOp", "BlendFlags", "SourceConstantAlpha", "AlphaFormat" });
    }

    public byte BlendOp = WinUser.AC_SRC_OVER; // only valid value
    public byte BlendFlags = 0; // only valid value
    public byte SourceConstantAlpha;
    public byte AlphaFormat;
}
