package com.github.axet.desktop.os.win;

import javax.swing.JOptionPane;

import com.github.axet.desktop.DesktopPower;
import com.github.axet.desktop.os.win.handle.WNDPROC;
import com.github.axet.desktop.os.win.libs.Kernel32Ex;
import com.github.axet.desktop.os.win.libs.User32Ex;
import com.github.axet.desktop.os.win.wrap.WndClassExWrap;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HINSTANCE;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser.MSG;

public class WindowsPowerVista extends WindowsPowerXP {


    public WindowsPowerVista() {
        super();
        
        // ShutdownBlockReasonCreate;
        // ShutdownBlockReasonDestroy;
        // ShutdownBlockReasonQuery
    }

}
