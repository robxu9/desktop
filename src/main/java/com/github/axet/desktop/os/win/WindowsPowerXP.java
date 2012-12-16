package com.github.axet.desktop.os.win;

import java.awt.Frame;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.github.axet.desktop.DesktopPower;
import com.github.axet.desktop.os.win.handle.CWPSSTRUCT;
import com.github.axet.desktop.os.win.handle.HANDLER_ROUTINE;
import com.github.axet.desktop.os.win.handle.WNDPROC;
import com.github.axet.desktop.os.win.libs.Kernel32Ex;
import com.github.axet.desktop.os.win.libs.User32Ex;
import com.github.axet.desktop.os.win.wrap.WndClassExWrap;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HINSTANCE;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.sun.jna.platform.win32.WinUser.HOOKPROC;
import com.sun.jna.platform.win32.WinUser.MSG;

public class WindowsPowerXP extends DesktopPower {

    public static final int WM_QUERYENDSESSION = 17;
    public static final int WM_ENDSESSION = 22;
    public static final int WH_CALLWNDPROC = 4;
    public static final int WH_CALLWNDPROCRET = 12;
    public static final int TH32CS_SNAPTHREAD = 0x00000004;

    public class MessagePump implements Runnable {
        Thread t;

        WndClassExWrap wc;
        WNDPROC WndProc;
        HWND hWnd;
        HINSTANCE hInstance;

        Object lock = new Object();

        public MessagePump() {
            t = new Thread(this, WindowsPowerXP.class.getSimpleName());
        }

        public void start() {
            synchronized (lock) {
                t.start();
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        void create() {
            WndProc = new WNDPROC() {
                public LRESULT callback(HWND hWnd, int msg, WPARAM wParam, LPARAM lParam) {
                    switch (msg) {
                    case WM_ENDSESSION:
                        return new LRESULT(0);
                    case WM_QUERYENDSESSION:
                        JOptionPane.showMessageDialog(null, "exit");
                        for (Listener l : listeners) {
                            l.quit();
                        }
                        return new LRESULT(0);
                    case User32.WM_QUIT:
                        User32.INSTANCE.PostMessage(hWnd, User32.WM_QUIT, null, null);
                        break;
                    }

                    return User32Ex.INSTANCE.DefWindowProc(hWnd, msg, wParam, lParam);
                }
            };
            hWnd = createWindow();
        }

        // http://osdir.com/ml/java.jna.user/2008-07/msg00049.html

        HWND createWindow() {
            hInstance = Kernel32.INSTANCE.GetModuleHandle(null);

            wc = new WndClassExWrap(hInstance, WndProc, WindowsPowerXP.class.getSimpleName());

            HWND hwnd = User32Ex.INSTANCE.CreateWindowEx(0, wc.getName(), wc.getName(), User32Ex.WS_OVERLAPPED, 0, 0,
                    0, 0, null, null, hInstance, null);

            if (hwnd == null)
                throw new GetLastErrorException();

            return hwnd;
        }

        @Override
        public void run() {
            create();

            synchronized (lock) {
                lock.notifyAll();
            }

            MSG msg = new MSG();

            while (User32.INSTANCE.GetMessage(msg, null, 0, 0) > 0) {
                User32.INSTANCE.DispatchMessage(msg);
            }

            destory();
        }

        void destory() {
            if (hWnd != null) {
                if (!User32Ex.INSTANCE.DestroyWindow(hWnd))
                    throw new GetLastErrorException();
                hWnd = null;
            }

            if (wc != null) {
                wc.close();
                wc = null;
            }
        }

        void close() {
            User32Ex.INSTANCE.SendMessage(hWnd, User32.WM_QUIT, null, null);

            try {
                if (!Thread.currentThread().equals(t))
                    t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    MessagePump mp = new MessagePump();

    HANDLER_ROUTINE hr = new HANDLER_ROUTINE() {
        @Override
        public long callback(long dwCtrlType) {
            if ((dwCtrlType & HANDLER_ROUTINE.CTRL_CLOSE_EVENT) == HANDLER_ROUTINE.CTRL_CLOSE_EVENT) {
                for (Listener l : listeners) {
                    l.quit();
                }
            }
            if ((dwCtrlType & HANDLER_ROUTINE.CTRL_LOGOFF_EVENT) == HANDLER_ROUTINE.CTRL_LOGOFF_EVENT) {
                for (Listener l : listeners) {
                    l.quit();
                }
            }
            if ((dwCtrlType & HANDLER_ROUTINE.CTRL_SHUTDOWN_EVENT) == HANDLER_ROUTINE.CTRL_SHUTDOWN_EVENT) {
                for (Listener l : listeners) {
                    l.quit();
                }
            }
            return 1;
        }
    };

    HOOKPROC hp = new HOOKPROC() {
        @SuppressWarnings("unused")
        public LRESULT callback(int nCode, WPARAM wParam, CWPSSTRUCT hookProcStruct) {
            switch (hookProcStruct.message) {
            case WM_QUERYENDSESSION:
                for (Listener l : listeners) {
                    l.quit();
                }
                break;
            }
            return new LRESULT();
        }
    };
    HHOOK hHook;

    public WindowsPowerXP() {
        if (!Kernel32Ex.INSTANCE.SetProcessShutdownParameters(0x03FF, 0))
            throw new GetLastErrorException();

        mp.start();

        if (!Kernel32Ex.INSTANCE.SetConsoleCtrlHandler(hr, true))
            throw new GetLastErrorException();

        final HWND hwnd = new HWND();
        JFrame f = new JFrame();
        f.pack();
        hwnd.setPointer(Native.getComponentPointer(f));

        int wID = User32.INSTANCE.GetWindowThreadProcessId(hwnd, null);
        hHook = User32.INSTANCE.SetWindowsHookEx(WH_CALLWNDPROC, hp, null, wID);
        if (hHook == null)
            throw new GetLastErrorException();
    }

    @Override
    public void close() {
        if (!User32.INSTANCE.UnhookWindowsHookEx(hHook))
            throw new GetLastErrorException();

        mp.close();

        if (!Kernel32Ex.INSTANCE.SetConsoleCtrlHandler(hr, false))
            throw new GetLastErrorException();
    }

}
