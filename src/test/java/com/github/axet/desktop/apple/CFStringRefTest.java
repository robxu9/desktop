package com.github.axet.desktop.apple;

import com.github.axet.desktop.apple.CFStringRef;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CFStringRefTest extends TestCase {
    public CFStringRefTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(CFStringRefTest.class);
    }

    public void testApp() {
        CFStringRef a = CFStringRef.CFSTR("abc");
        String s = a.toString();
        assertTrue(s.equals("abc"));
    }
}
