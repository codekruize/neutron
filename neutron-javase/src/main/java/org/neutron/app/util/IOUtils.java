package org.neutron.app.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
public class IOUtils {
public static String getCanonicalFileURL(File file) {
		String path = file.getAbsoluteFile().getPath();
		if (File.separatorChar != '/') {
			path = path.replace(File.separatorChar, '/');
		}
		// Not network path
		if (!path.startsWith("//")) {
			if (path.startsWith("/")) {
				path = "//" + path;
			} else {
				path = "///" + path;
			}
		}
		return "file:" + path;
	}
	
	public static String getCanonicalFileClassLoaderURL(File file) {
		String url = getCanonicalFileURL(file);
		if ((file.isDirectory()) && (!url.endsWith("/"))) {
			url += "/";
		}
		return url;
	}
	
	public static void copyFile(File src, File dst) throws IOException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(src);
			copyToFile(fis, dst);
		} finally {
			closeQuietly(fis); 
		}
	}
	
	public static void copyToFile(InputStream is, File dst) throws IOException {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(dst);
			byte[] buf = new byte[1024]; 
			int i = 0;
			while ((i = is.read(buf)) != -1) { 
				fos.write(buf, 0, i);
			}
		} finally {
			closeQuietly(fos);	
		}
	}
public static void closeQuietly(InputStream input) {
        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException ignore) {
            // ignore
        }
    }
public static void closeQuietly(OutputStream output) {
        try {
            if (output != null) {
                output.close();
            }
        } catch (IOException ignore) {
            // ignore
        }
    }
public static void closeQuietly(Writer output) {
        try {
            if (output != null) {
                output.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }
}
