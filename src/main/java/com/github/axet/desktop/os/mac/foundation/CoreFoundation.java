package com.github.axet.desktop.os.mac.foundation;

import com.github.axet.desktop.os.mac.cocoa.CFRange;
import com.github.axet.desktop.os.mac.cocoa.NSArray;
import com.github.axet.desktop.os.mac.cocoa.NSString;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public interface CoreFoundation extends Library {

    CoreFoundation INSTANCE = (CoreFoundation) Native.loadLibrary("CoreFoundation", CoreFoundation.class);

    //
    // https://developer.apple.com/library/mac/#documentation/CoreFoundation/Reference/CFStringRef/Reference/reference.html
    //

    /**
     * CFStringRef CFStringCreateWithCharacters ( CFAllocatorRef alloc, const
     * UniChar *chars, CFIndex numChars );
     * 
     * @param alloc
     * @param chars
     * @param numChars
     * @return
     */
    public NSString CFStringCreateWithCharacters(Void alloc, char[] chars, int numChars);

    /**
     * void CFStringGetCharacters ( CFStringRef theString, CFRange range,
     * UniChar *buffer );
     * 
     * @param theString
     * @param range
     * @param buffer
     */
    public void CFStringGetCharacters(NSString theString, CFRange.ByValue range, Pointer buffer);

    /**
     * const UniChar * CFStringGetCharactersPtr ( CFStringRef theString );
     * 
     * @param theString
     * @return
     */
    public Pointer CFStringGetCharactersPtr(NSString theString);

    /**
     * CFIndex CFStringGetLength ( CFStringRef theString );
     * 
     * @param theString
     * @return
     */
    public int CFStringGetLength(NSString theString);

    //
    // https://developer.apple.com/library/mac/#documentation/CoreFOundation/Reference/CFBundleRef/Reference/reference.html#//apple_ref/c/func/CFBundleGetMainBundle
    //

    /**
     * CFBundleRef CFBundleGetMainBundle ( void );
     * 
     * @return
     */
    public Pointer CFBundleGetMainBundle();

    /**
     * CFStringRef CFBundleGetIdentifier ( CFBundleRef bundle );
     * 
     * @param bundle
     * @return
     */
    public NSString CFBundleGetIdentifier(Pointer bundle);

    //
    // https://developer.apple.com/library/mac/#documentation/CoreFoundation/Reference/CFArrayRef/Reference/reference.html
    //

    /**
     * CFIndex CFArrayGetCount ( CFArrayRef theArray );
     * 
     * @param theArray
     * @return
     */
    public int CFArrayGetCount(NSArray theArray);

    /**
     * const void * CFArrayGetValueAtIndex ( CFArrayRef theArray, CFIndex idx );
     * 
     * @param theArray
     * @param idx
     * @return
     */
    public Pointer CFArrayGetValueAtIndex(NSArray theArray, int idx);
}
