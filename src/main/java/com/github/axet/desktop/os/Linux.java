package com.github.axet.desktop.os;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.github.axet.desktop.Desktop;

// man xdg-user-dirs

public class Linux extends Desktop {

    File rootFile = new File("/etc/xdg/user-dirs.conf");
    Map<String, String> root;
    long rootLast = 0;

    File userFile = new File(expand("~/.config/user-dirs.dirs"));
    Map<String, String> user;
    long userLast = 0;

    File defaultsFile = new File("/etc/xdg/user-dirs.defaults");
    Map<String, String> defaults;
    long defaultsLast = 0;

    @Override
    public File getAppData() {
        return path("XDG_CONFIG_HOME", null, "$HOME/.config");
    }

    @Override
    public File getHome() {
        return new File(expand("$HOME"));
    }

    @Override
    public File getDocuments() {
        return path("XDG_DOCUMENTS_DIR", "DOCUMENTS", "$HOME/Documents");
    }

    @Override
    public File getDesktop() {
        return path("XDG_DESKTOP_DIR", "DESKTOP", "$HOME/Desktop");
    }

    @Override
    public File getDownloads() {
        return path("XDG_DOWNLOAD_DIR", "DOWNLOAD", "$HOME/Downloads");
    }

    String trim(String s) {
        s = s.trim();
        s = StringUtils.strip(s, "\"");
        return s;
    }

    Map<String, String> getIni(File file) {
        try {
            TreeMap<String, String> map = new TreeMap<String, String>();

            final BufferedReader in = new BufferedReader(new FileReader(file));
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty())
                        continue;
                    if (line.startsWith("#"))
                        continue;

                    Pattern p = Pattern.compile("([^=]*)=([^\n]*)");
                    Matcher m = p.matcher(line);

                    while (m.find()) {
                        String key = m.group(1).trim();
                        key = trim(key);
                        String value = m.group(2).trim();
                        value = trim(value);
                        map.put(key, value);
                    }
                }
            } finally {
                in.close();
            }

            return map;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class LongestFirstComparator implements java.util.Comparator<String> {

        public LongestFirstComparator() {
            super();
        }

        public int compare(String s1, String s2) {
            int l1 = s1.length();
            int l2 = s2.length();
            if (l1 < l2)
                return 1;
            if (l1 == l2)
                return 0;
            return -1;
        }
    }

    /**
     * simple expand function. may fail on long variables like $HOMED -> will be
     * replaced to $HOME value.
     * 
     * @param e
     * @return
     */
    String expand(String e) {
        Map<String, String> map = System.getenv();
        String[] longestFirst = map.keySet().toArray(new String[0]);
        Arrays.sort(longestFirst, new LongestFirstComparator());

        for (String key : longestFirst) {
            e = e.replaceAll("\\$" + key, map.get(key));
            e = e.replaceAll("%" + key + "%", map.get(key));
        }

        e = e.replaceAll("~", map.get("HOME"));

        return e;
    }

    /**
     * 
     * @param key
     *            - key like "XDG_DOCUMENTS_DIR"
     * @param xdgDefaultKey
     *            - "DOCUMENTS", from /etc/xdg/user-dirs.defaults
     * @param xdgDefaultPath
     *            - default fallback value, "$HOME/Documents"
     * @return
     */
    File path(String key, String xdgDefaultKey, String xdgDefaultPath) {
        // 1) we have to check /etc/xdg/user-dirs.conf
        // if it is diabled, file is not here. rollback to xdgDefault parameter
        //
        // 2) ~/.config/user-dirs.dirs
        // try to locate user dirs file. and look for a value in there. if here
        // is no value or no file, switch to step 3
        //
        // 3) /etc/xdg/user-dirs.defaults
        // try to get system defaults for specified value. is here is no default
        // switch to xdgDefault parameter

        // 1) check global
        boolean enabled = false;

        if (rootFile.exists()) {
            try {
                if (root == null || rootLast < rootFile.lastModified()) {
                    root = getIni(rootFile);
                    rootLast = rootFile.lastModified();
                }
                String e = root.get("enabled").toLowerCase();
                enabled = e.equals("true") || e.equals("yes");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if (!enabled)
            return new File(expand(xdgDefaultPath));

        // 2) check user specified file
        if (userFile.exists()) {
            try {
                if (user == null || userLast < userFile.lastModified()) {
                    user = getIni(userFile);
                    userLast = userFile.lastModified();
                }
                String v = user.get(key);
                if (v != null)
                    return new File(expand(v));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // 3) check default
        if (xdgDefaultKey != null) {
            if (defaultsFile.exists()) {
                try {
                    if (defaults == null || defaultsLast < defaultsFile.lastModified()) {
                        defaults = getIni(defaultsFile);
                        defaultsLast = defaultsFile.lastModified();
                    }
                    String v = defaults.get(xdgDefaultKey);
                    if (v != null)
                        return new File(expand("$HOME/" + v));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return new File(expand(xdgDefaultPath));
    }
}
