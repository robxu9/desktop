package com.github.axet.desktop.os.win.libs;

import com.github.axet.desktop.os.win.handle.HANDLER_ROUTINE;
import com.github.axet.desktop.os.win.handle.THREADENTRY32;
import com.github.axet.desktop.os.win.handle.THREAD_START_ROUTINE;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.BaseTSD.SIZE_T;
import com.sun.jna.platform.win32.WinBase.SECURITY_ATTRIBUTES;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.win32.W32APIOptions;

public interface Kernel32Ex extends Library {

    static Kernel32Ex INSTANCE = (Kernel32Ex) Native.loadLibrary("kernel32", Kernel32Ex.class,
            W32APIOptions.DEFAULT_OPTIONS);

    // http://msdn.microsoft.com/en-us/library/windows/desktop/ms686227(v=vs.85).aspx
    /**
     * BOOL WINAPI SetProcessShutdownParameters( _In_ DWORD dwLevel, _In_ DWORD
     * dwFlags );
     */
    boolean SetProcessShutdownParameters(long dwLevel, long dwFlags);

    // http://msdn.microsoft.com/en-us/library/windows/desktop/ms682453(v=vs.85).aspx
    /**
     * HANDLE WINAPI CreateThread( _In_opt_ LPSECURITY_ATTRIBUTES
     * lpThreadAttributes, _In_ SIZE_T dwStackSize, _In_ LPTHREAD_START_ROUTINE
     * lpStartAddress, _In_opt_ LPVOID lpParameter, _In_ DWORD dwCreationFlags,
     * _Out_opt_ LPDWORD lpThreadId );
     */
    HANDLE CreateThread(SECURITY_ATTRIBUTES lpThreadAttributes, SIZE_T dwStackSize,
            THREAD_START_ROUTINE lpStartAddress, long lpParameter, long dwCreationFlags, LongByReference lpThreadId);

    // http://msdn.microsoft.com/en-us/library/windows/desktop/ms686016(v=vs.85).aspx
    /**
     * BOOL WINAPI SetConsoleCtrlHandler( _In_opt_ PHANDLER_ROUTINE
     * HandlerRoutine, _In_ BOOL Add );
     */
    boolean SetConsoleCtrlHandler(HANDLER_ROUTINE HandlerRoutine, boolean Add);

    boolean AllocConsole();

    // http://msdn.microsoft.com/en-us/library/windows/desktop/ms686728(v=vs.85).aspx
    /**
     * BOOL WINAPI Thread32First( _In_ HANDLE hSnapshot, _Inout_ LPTHREADENTRY32
     * lpte );
     */
    boolean Thread32First(HANDLE hSnapshot, THREADENTRY32 lpte);

    boolean Thread32Next(HANDLE hSnapshot, THREADENTRY32 lpte);
}
