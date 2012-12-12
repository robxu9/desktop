package com.github.axet.desktop.os.mac.cocoa;

import com.sun.jna.Structure;

public class NSSize extends Structure {

    public static class ByReference extends NSSize implements com.sun.jna.Structure.ByReference {
        // / Allocate a new Pair.ByRef struct on the heap
        public ByReference() {
        }

        // / Create an instance that shares its memory with another Pair
        // instance
        public ByReference(NSSize struct) {
            super(struct.getPointer(), 0);
        }
    }

    public static class ByValue extends NSSize implements Structure.ByValue {
        public ByValue() {
        }

        // / Create an instance that shares its memory with another Pair
        // instance
        public ByValue(NSSize struct) {
            super(struct.getPointer(), 0);
        }
    }

    public NSSize() {
    }

    // Cast data at given memory location (pointer + offset) as an existing
    // Pair struct
    public NSSize(com.sun.jna.Pointer pointer, int offset) {
        super();
        useMemory(pointer, offset);
        read();
    }

    public double width;
    public double height;
}
