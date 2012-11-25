package com.github.axet.desktop.os.win;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.jna.Structure;

public class GUID extends Structure {

    public static class ByValue extends GUID implements Structure.ByValue {
    }

    public byte[] data;

    public GUID() {
    }

    public GUID(String g) {
        Pattern p = Pattern.compile("(\\w+)-(\\w+)-(\\w+)-(\\w+)-(\\w+)");
        Matcher m = p.matcher(g);
        if (!m.find())
            throw new RuntimeException("bad guid");

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeInt(intLE(m.group(1)));
            dos.writeShort(shortLE(m.group(2)));
            dos.writeShort(shortLE(m.group(3)));
            dos.writeShort(shortBE(m.group(4)));
            for (String c : m.group(5).split("(?<=\\G.{2})")) {
                int bb = Integer.parseInt(c, 16);
                dos.writeByte(bb);
            }
            dos.flush();

            data = bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // little endian (lowest first)
    public int intLE(String hex) {
        int i = Integer.parseInt(hex, 16);
        ByteBuffer bb = ByteBuffer.allocate(Integer.SIZE);
        bb.putInt(i);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb.getInt(0);
    }

    public int shortLE(String hex) {
        Integer i = Integer.parseInt(hex, 16);
        ByteBuffer bb = ByteBuffer.allocate(Short.SIZE);
        bb.putShort(i.shortValue());
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb.getShort(0);
    }

    public int shortBE(String hex) {
        Integer i = Integer.parseInt(hex, 16);
        ByteBuffer bb = ByteBuffer.allocate(Short.SIZE);
        bb.putShort(i.shortValue());
        bb.order(ByteOrder.BIG_ENDIAN);
        return bb.getShort(0);
    }
}