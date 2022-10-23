package com.wzy.kts.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author yu.wu
 * @description
 * @date 2022/10/20 23:15
 */
public class CloseUtil {
    public static void close(Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (Closeable closeable : closeables) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
