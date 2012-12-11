package com.github.axet.desktop.apple;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.github.axet.desktop.os.mac.cocoa.NSArray;
import com.github.axet.desktop.os.mac.cocoa.NSString;
import com.github.axet.desktop.os.mac.foundation.NSFileNanager;

public class NSFileNanagerTest {
    public static Test suite() {
        return new TestSuite(NSFileNanagerTest.class);
    }

    public static void main(String[] args) {
        NSArray a = new NSArray(NSFileNanager.INSTANCE.NSSearchPathForDirectoriesInDomains(
                NSFileNanager.NSSearchPathDirectory.NSAllApplicationsDirectory,
                NSFileNanager.NSSearchPathDomainMask.NSSystemDomainMask, true));

        for (long i = 0; i < a.count(); i++) {
            NSString s = new NSString(a.objectAtIndex(0));
            String ss = s.toString();
            System.out.println(ss);
        }

    }
}
