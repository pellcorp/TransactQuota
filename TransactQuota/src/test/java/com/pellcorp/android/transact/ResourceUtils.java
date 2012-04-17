package com.pellcorp.android.transact;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

public final class ResourceUtils {
	private ResourceUtils() {
	}
	
	/**
	 * Save a classpath resource to a temporary directory so it can be referenced safely from
	 * a File.
	 */
	public static File getResourceAsFile(String resourcePath) throws IOException {
		InputStream is = ResourceUtils.class.getResourceAsStream(resourcePath);
		if (is != null) {
			File file = File.createTempFile("tmp", ".bin");
			OutputStream fileStream = new FileOutputStream(file);
			IOUtils.copy(is, fileStream);
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(fileStream);
			return file;
		} else {
			throw new IOException("Resource not found: " + resourcePath);
		}
	}
}
