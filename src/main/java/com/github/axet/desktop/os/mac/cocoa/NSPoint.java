package com.github.axet.desktop.os.mac.cocoa;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class NSPoint extends Structure {

    public static class ByReference extends NSPoint implements com.sun.jna.Structure.ByReference {
        // / Allocate a new Pair.ByRef struct on the heap
        public ByReference() {
        }

        // / Create an instance that shares its memory with another Pair
        // instance
        public ByReference(NSPoint struct) {
            super(struct.getPointer(), 0);
        }
    }

    public static class ByValue extends NSPoint implements Structure.ByValue {
        public ByValue() {
        }

        // / Create an instance that shares its memory with another Pair
        // instance
        public ByValue(NSPoint struct) {
            super(struct.getPointer(), 0);
        }
    }

    public NSPoint() {
    }

    // Cast data at given memory location (pointer + offset) as an existing
    // Pair struct
    public NSPoint(com.sun.jna.Pointer pointer, int offset) {
        super();
        useMemory(pointer, offset);
        read();
    }

    public long x;
    public long y;
}
