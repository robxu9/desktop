package com.github.axet.desktop.os.mac;

import com.sun.jna.Structure;

public class CFRange extends Structure {

    public static class ByValue extends CFRange implements Structure.ByValue { }
    
    public long loc;
    public long len;
    
}
