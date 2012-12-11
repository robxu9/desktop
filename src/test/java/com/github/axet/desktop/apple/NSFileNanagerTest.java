package com.github.axet.desktop.apple;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.github.axet.desktop.os.mac.cocoa.NSArray;
import com.github.axet.desktop.os.mac.cocoa.NSFileManager;
import com.github.axet.desktop.os.mac.cocoa.NSURL;

public class NSFileNanagerTest {
    public static Test suite() {
        return new TestSuite(NSFileNanagerTest.class);
    }

    public static void main(String[] args) {
        NSFileManager f = new NSFileManager();

        NSArray a = f.URLsForDirectoryInDomains(NSFileManager.NSSearchPathDirectory.NSAllApplicationsDirectory,
                NSFileManager.NSSearchPathDomainMask.NSSystemDomainMask);

        for (long i = 0; i < a.count(); i++) {
            NSURL s = new NSURL(a.objectAtIndex(0));
            System.out.println(s.absoluteString().toString());
            System.out.println(s.path().toString());
        }

    }
}
