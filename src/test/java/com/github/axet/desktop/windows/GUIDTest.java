package com.github.axet.desktop.windows;

import com.github.axet.desktop.os.win.GUID;

public class GUIDTest {
    public static void main(String[] args) {
        try {
            @SuppressWarnings("unused")
            GUID g = new GUID("374DE290-123F-4565-9164-39C4925E467B");
            g = null;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
