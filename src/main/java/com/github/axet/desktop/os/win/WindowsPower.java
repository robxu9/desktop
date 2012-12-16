package com.github.axet.desktop.os.win;

import com.github.axet.desktop.DesktopPower;
import com.github.axet.desktop.os.win.handle.WNDPROC;
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

public class WindowsPower extends DesktopPower {

    public static final int WM_QUERYENDSESSION = 17;

    public class MessagePump implements Runnable {
        Thread t;

        WndClassExWrap wc;
        WNDPROC WndProc;
        HWND hWnd;
        HINSTANCE hInstance;

        Object lock = new Object();

        public MessagePump() {
            t = new Thread(this, "MessagePump");
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
                    case WM_QUERYENDSESSION:
                        for (Listener l : listeners) {
                            l.quit();
                        }
                        break;
                    case User32.WM_QUIT:
                        User32.INSTANCE.PostQuitMessage(0);
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

            wc = new WndClassExWrap(hInstance, WndProc, WindowsPower.class.getSimpleName());

            HWND hwnd = User32Ex.INSTANCE.CreateWindowEx(0, wc.getName(), wc.getName(), User32Ex.WS_OVERLAPPEDWINDOW,
                    0, 0, 0, 0, null, null, hInstance, null);

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

            while (User32.INSTANCE.GetMessage(msg, hWnd, 0, 0) > 0) {
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
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    MessagePump mp = new MessagePump();

    public WindowsPower() {
    }

    @Override
    public void close() {
        mp.close();
    }

}
