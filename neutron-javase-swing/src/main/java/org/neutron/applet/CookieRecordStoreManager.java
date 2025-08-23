package org.neutron.applet;

import java.applet.Applet;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

//import netscape.javascript.JSObject;

import org.neutron.Neutron;
import org.neutron.RecordStoreManager;
import org.neutron.log.Logger;
import org.neutron.util.Base64Coder;
import org.neutron.util.ExtendedRecordListener;
import org.neutron.util.RecordStoreImpl;

// TODO add JSObject import again
public class CookieRecordStoreManager implements RecordStoreManager {

	private static final int MAX_SPLIT_COOKIES = 5; // max 10

	private static final int MAX_COOKIE_SIZE = 4096 * 3 / 4; // Base64

	private ExtendedRecordListener recordListener = null;

	private Applet applet;

//	private JSObject document;

	private HashMap cookies;

	private String expires;

	public CookieRecordStoreManager(Applet applet) {
		this.applet = applet;

		Calendar c = Calendar.getInstance();
		c.add(java.util.Calendar.YEAR, 1);
		SimpleDateFormat format = new SimpleDateFormat("EEE, dd-MM-yyyy hh:mm:ss z");
		this.expires = "; Max-Age=" + (60 * 60 * 24 * 365);
		System.out.println("CookieRecordStoreManager: " + this.expires);
	}

	public void init(Neutron emulator) {
	}

	public String getName() {
		return this.getClass().toString();
	}

	public void deleteRecordStore(String recordStoreName) throws RecordStoreNotFoundException, RecordStoreException {
		CookieContent cookieContent = (CookieContent) cookies.get(recordStoreName);
		if (cookieContent == null) {
			throw new RecordStoreNotFoundException(recordStoreName);
		}

		removeCookie(recordStoreName, cookieContent);
		cookies.remove(recordStoreName);

		fireRecordStoreListener(ExtendedRecordListener.RECORDSTORE_DELETE, recordStoreName);

		System.out.println("deleteRecordStore: " + recordStoreName);
	}

	public void deleteStores() {
		for (Iterator it = cookies.keySet().iterator(); it.hasNext();) {
			try {
				deleteRecordStore((String) it.next());
			} catch (RecordStoreException ex) {
				Logger.error(ex);
			}
		}
		System.out.println("deleteStores:");
	}

	public void init() {
}

	public String[] listRecordStores() {
		System.out.println("listRecordStores:");
		String[] result = (String[]) cookies.keySet().toArray();

		if (result.length == 0) {
			result = null;
		}

		return result;
	}

	public RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary)
			throws RecordStoreNotFoundException {
		RecordStoreImpl result;

		CookieContent load = (CookieContent) cookies.get(recordStoreName);
		if (load != null) {
			try {
				byte[] data = Base64Coder.decode(load.toCharArray());
				result = new RecordStoreImpl(this);
				DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
				int size = result.readHeader(dis);
				for (int i = 0; i < size; i++) {
					result.readRecord(dis);
				}
				dis.close();
			} catch (IOException ex) {
				Logger.error(ex);
				throw new RecordStoreNotFoundException(ex.getMessage());
			}
			System.out.println("openRecordStore: " + recordStoreName + " (" + load.getParts().length + ")");
		} else {
			if (!createIfNecessary) {
				throw new RecordStoreNotFoundException(recordStoreName);
			}
			result = new RecordStoreImpl(this, recordStoreName);
			System.out.println("openRecordStore: " + recordStoreName + " (" + load + ")");
		}
		result.setOpen(true);
		if (recordListener != null) {
			result.addRecordListener(recordListener);
		}

		fireRecordStoreListener(ExtendedRecordListener.RECORDSTORE_OPEN, recordStoreName);

		return result;
	}
	
	public void deleteRecord(RecordStoreImpl recordStoreImpl, int recordId) throws RecordStoreNotOpenException, RecordStoreException {
		saveRecord(recordStoreImpl, recordId);
	}
	
	public void loadRecord(RecordStoreImpl recordStoreImpl, int recordId)
			throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException 
	{
		// records are loaded when record store opens
	}

	public void saveRecord(RecordStoreImpl recordStoreImpl, int recordId) throws RecordStoreException {
}

	public int getSizeAvailable(RecordStoreImpl recordStoreImpl) {
		int size = MAX_COOKIE_SIZE * MAX_SPLIT_COOKIES;

		size -= recordStoreImpl.getHeaderSize();
		try {
			RecordEnumeration en = recordStoreImpl.enumerateRecords(null, null, false);
			while (en.hasNextElement()) {
				size -= en.nextRecord().length + recordStoreImpl.getRecordHeaderSize();
			}
		} catch (RecordStoreException ex) {
			Logger.error(ex);
		}

		// TODO Auto-generated method stub
		System.out.println("getSizeAvailable: " + size);
		return size;
	}

	private void removeCookie(String recordStoreName, CookieContent cookieContent) {
}

	private class CookieContent {
		private String[] parts;

		public CookieContent() {
		}

		public CookieContent(char[] buffer) {
			parts = new String[buffer.length / MAX_COOKIE_SIZE + 1];
			System.out.println("CookieContent(before): " + parts.length);
			int index = 0;
			for (int i = 0; i < parts.length; i++) {
				int size = MAX_COOKIE_SIZE;
				if (index + size > buffer.length) {
					size = buffer.length - index;
				}
				System.out.println("CookieContent: " + i + "," + index + "," + size);
				parts[i] = new String(buffer, index, size);
				index += size;
			}
		}

		public void setPart(int index, String content) {
			if (parts == null) {
				parts = new String[index + 1];
			} else {
				if (parts.length <= index) {
					String[] newParts = new String[index + 1];
					System.arraycopy(parts, 0, newParts, 0, parts.length);
					parts = newParts;
				}
			}
			System.out.println("setPart: " + index + "," + parts.length);

			parts[index] = content;
		}

		public String[] getParts() {
			System.out.println("getParts: " + parts);
			return parts;
		}

		public char[] toCharArray() {
			int size = 0;
			for (int i = 0; i < parts.length; i++) {
				size += parts[i].length();
			}

			char[] result = new char[size];

			int index = 0;
			for (int i = 0; i < parts.length; i++) {
				System.out.println("toCharArray: " + i + "," + index + "," + size + "," + parts[i].length());
				System.arraycopy(parts[i].toCharArray(), 0, result, index, parts[i].length());
				index += parts[i].length();
			}

			return result;
		}
	}

	public void setRecordListener(ExtendedRecordListener recordListener) {
		this.recordListener = recordListener;
	}

	public void fireRecordStoreListener(int type, String recordStoreName) {
		if (recordListener != null) {
			recordListener.recordStoreEvent(type, System.currentTimeMillis(), recordStoreName);
		}
	}
}
