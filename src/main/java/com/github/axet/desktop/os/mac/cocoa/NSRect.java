package com.github.axet.desktop.os.mac.cocoa;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class NSRect extends Structure {

    public static class ByReference extends NSRect implements com.sun.jna.Structure.ByReference {
        // / Allocate a new Pair.ByRef struct on the heap
        public ByReference() {
        }

        // / Create an instance that shares its memory with another Pair
        // instance
        public ByReference(NSRect struct) {
            super(struct.getPointer(), 0);
        }
    }

    public static class ByValue extends NSRect implements Structure.ByValue {
        public ByValue() {
        }

        // / Create an instance that shares its memory with another Pair
        // instance
        public ByValue(NSRect struct) {
            super(struct.getPointer(), 0);
        }
    }

    public NSRect() {
    }

    public NSRect(int x, int y, int cx, int cy) {
        origin.x = x;
        origin.y = y;
        size.width = cx;
        size.height = cy;
    }

    // Cast data at given memory location (pointer + offset) as an existing
    // Pair struct
    public NSRect(com.sun.jna.Pointer pointer, int offset) {
        super();
        useMemory(pointer, offset);
        read();
    }

    public NSPoint origin;
    public NSSize size;
}
