package com.github.axet.desktop.os.mac;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public interface CoreFoundation extends Library {

    CoreFoundation INSTANCE = (CoreFoundation) Native.loadLibrary("CoreFoundation", CoreFoundation.class);

    //
    // https://developer.apple.com/library/mac/#documentation/CoreFoundation/Reference/CFStringRef/Reference/reference.html
    //

    public CFStringRef CFStringCreateWithCharacters(Void alloc, char[] chars, int numChars);

    public void CFStringGetCharacters(CFStringRef theString, CFRange.ByValue range, Pointer buffer);

    public void CFStringGetCharacters(Pointer theString, CFRange.ByValue range, Pointer buffer);

    public Pointer CFStringGetCharactersPtr(CFStringRef theString);

    public int CFStringGetLength(CFStringRef theString);
    
    public int CFStringGetLength(Pointer theString);

    //
    // https://developer.apple.com/library/mac/#documentation/CoreFOundation/Reference/CFBundleRef/Reference/reference.html#//apple_ref/c/func/CFBundleGetMainBundle
    //

    public Pointer CFBundleGetMainBundle();

    public CFStringRef CFBundleGetIdentifier(Pointer bundle);

    //
    // https://developer.apple.com/library/mac/#documentation/CoreFoundation/Reference/CFArrayRef/Reference/reference.html
    //

    public int CFArrayGetCount(Pointer theArray);

    public Pointer CFArrayGetValueAtIndex(Pointer theArray, int idx);
}
