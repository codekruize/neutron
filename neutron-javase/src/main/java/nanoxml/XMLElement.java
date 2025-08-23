package nanoxml;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class XMLElement {
static final long serialVersionUID = 6685035139346394777L;
public static final int NANOXML_MAJOR_VERSION = 2;
public static final int NANOXML_MINOR_VERSION = 2;
private Hashtable attributes;
private Vector children;
private String name;
private String contents;
private Hashtable entities;
private int lineNr;
private boolean ignoreCase;
private boolean ignoreWhitespace;
private char charReadTooMuch;
private Reader reader;
private int parserLineNr;
public XMLElement() {
		this(new Hashtable(), false, true, true);
	}

	public XMLElement(boolean skipLeadingWhitespace, boolean ignoreCase) {
		this(new Hashtable(), skipLeadingWhitespace, true, ignoreCase);
	}
public XMLElement(Hashtable entities) {
		this(entities, false, true, true);
	}
public XMLElement(boolean skipLeadingWhitespace) {
		this(new Hashtable(), skipLeadingWhitespace, true, true);
	}
public XMLElement(Hashtable entities, boolean skipLeadingWhitespace) {
		this(entities, skipLeadingWhitespace, true, true);
	}
public XMLElement(Hashtable entities, boolean skipLeadingWhitespace, boolean ignoreCase) {
		this(entities, skipLeadingWhitespace, true, ignoreCase);
	}
protected XMLElement(Hashtable entities, boolean skipLeadingWhitespace, boolean fillBasicConversionTable,
			boolean ignoreCase) {
		this.ignoreWhitespace = skipLeadingWhitespace;
		this.ignoreCase = ignoreCase;
		this.name = null;
		this.contents = "";
		this.attributes = new Hashtable();
		this.children = new Vector();
		this.entities = entities;
		this.lineNr = 0;

		Enumeration en = this.entities.keys();

		while (en.hasMoreElements()) {
			Object key = en.nextElement();
			Object value = this.entities.get(key);

			if (value instanceof String) {
				value = ((String) value).toCharArray();
				this.entities.put(key, value);
			}
		}

		if (fillBasicConversionTable) {
			this.entities.put("amp", new char[] { '&' });
			this.entities.put("quot", new char[] { '"' });
			this.entities.put("apos", new char[] { '\'' });
			this.entities.put("lt", new char[] { '<' });
			this.entities.put("gt", new char[] { '>' });
		}
	}
public void addChild(XMLElement child) {
		this.children.addElement(child);
	}

	public XMLElement addChild(String name) {
		XMLElement xml = new XMLElement(true, false);
		xml.setName(name);
		this.addChild(xml);
		return xml;
	}

	public XMLElement addChild(String name, String value) {
		XMLElement xml = addChild(name);
		xml.setContent(value);
		return xml;
	}
public void setAttribute(String name, Object value) {
		if (this.ignoreCase) {
			name = name.toUpperCase();
		}

		this.attributes.put(name, value.toString());
	}
public void addProperty(String name, Object value) {
		this.setAttribute(name, value);
	}
public void setIntAttribute(String name, int value) {
		if (this.ignoreCase) {
			name = name.toUpperCase();
		}

		this.attributes.put(name, Integer.toString(value));
	}
public void addProperty(String key, int value) {
		this.setIntAttribute(key, value);
	}
public void setDoubleAttribute(String name, double value) {
		if (this.ignoreCase) {
			name = name.toUpperCase();
		}

		this.attributes.put(name, Double.toString(value));
	}
public void addProperty(String name, double value) {
		this.setDoubleAttribute(name, value);
	}
public int countChildren() {
		return this.children.size();
	}
public Enumeration enumerateAttributeNames() {
		return this.attributes.keys();
	}
public Enumeration enumeratePropertyNames() {
		return this.enumerateAttributeNames();
	}
public Enumeration enumerateChildren() {
		return this.children.elements();
	}
public Vector getChildren() {
		try {
			return (Vector) this.children.clone();
		} catch (Exception e) {
			// this never happens, however, some Java compilers are so
			// braindead that they require this exception clause
			return null;
		}
	}
public XMLElement getChild(String name) {
		for (Enumeration en = this.children.elements(); en.hasMoreElements();) {
			XMLElement el = (XMLElement) en.nextElement();
			if (el.getName().equals(name)) {
				return el;
			}
		}
		return null;
	}

	public XMLElement getChildOrNew(String name) {
		XMLElement c = getChild(name);
		if (c == null) {
			c = addChild(name);
		}
		return c;
	}

	public int getChildCount(String name) {
		int cnt = 0;
		for (Enumeration en = this.children.elements(); en.hasMoreElements();) {
			XMLElement el = (XMLElement) en.nextElement();
			if (el.getName().equals(name)) {
				cnt++;
			}
		}
		return cnt;
	}

	public XMLElement getChild(String name, String attrValue) {
		for (Enumeration en = this.children.elements(); en.hasMoreElements();) {
			XMLElement el = (XMLElement) en.nextElement();
			String elAttrValue = el.getStringAttribute("name");
			if ((el.getName().equals(name)) && ((attrValue == elAttrValue) || attrValue.equals(elAttrValue))) {
				return el;
			}
		}
		return null;
	}

	public XMLElement getChild(String name, Map equalAttributesNameValue) {
		nextChildren: for (Enumeration en = this.children.elements(); en.hasMoreElements();) {
			XMLElement el = (XMLElement) en.nextElement();
			if (el.getName().equals(name)) {
				nextAttribute: for (Iterator i = equalAttributesNameValue.entrySet().iterator(); i.hasNext();) {
					Map.Entry atrNameValue = (Map.Entry) i.next();
					String attrValue = el.getStringAttribute((String) atrNameValue.getKey());
					if (atrNameValue.getValue() == null) {
						if (attrValue != null) {
							continue nextChildren;
						} else {
							continue nextAttribute;
						}
					}
					if (!atrNameValue.getValue().equals(attrValue)) {
						continue nextChildren;
					}
				}
				return el;
			}
		}
		return null;
	}

	public int getChildInteger(String name, int defaultValue) {
		XMLElement xml = this.getChild(name);
		if (xml == null) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(xml.getContent());
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public String getChildString(String name, String defaultValue) {
		XMLElement xml = this.getChild(name);
		if (xml == null) {
			return defaultValue;
		}
		String v = xml.getContent();
		if ((v == null) || (v.length() == 0)) {
			return defaultValue;
		}
		return v;
	}
public String getContents() {
		return this.getContent();
	}
public String getContent() {
		return this.contents;
	}
public int getLineNr() {
		return this.lineNr;
	}
public Object getAttribute(String name) {
		return this.getAttribute(name, null);
	}
public Object getAttribute(String name, Object defaultValue) {
		if (this.ignoreCase) {
			name = name.toUpperCase();
		}

		Object value = this.attributes.get(name);

		if (value == null) {
			value = defaultValue;
		}

		return value;
	}
public Object getAttribute(String name, Hashtable valueSet, String defaultKey, boolean allowLiterals) {
		if (this.ignoreCase) {
			name = name.toUpperCase();
		}

		Object key = this.attributes.get(name);
		Object result;

		if (key == null) {
			key = defaultKey;
		}

		result = valueSet.get(key);

		if (result == null) {
			if (allowLiterals) {
				result = key;
			} else {
				throw this.invalidValue(name, (String) key);
			}
		}

		return result;
	}
public String getStringAttribute(String name) {
		return this.getStringAttribute(name, null);
	}

	public Map getStringAttributes(String[] names) {
		// Allow null values.
		Map attrNameValue = new HashMap();
		for (int i = 0; i < names.length; i++) {
			// System.out.println("add [" + names[i] + "]=[" +
			// this.getStringAttribute(names[i])+"]");
			attrNameValue.put(names[i], this.getStringAttribute(names[i]));
		}
		return attrNameValue;
	}
public String getStringAttribute(String name, String defaultValue) {
		return (String) this.getAttribute(name, defaultValue);
	}
public String getStringAttribute(String name, Hashtable valueSet, String defaultKey, boolean allowLiterals) {
		return (String) this.getAttribute(name, valueSet, defaultKey, allowLiterals);
	}
public int getIntAttribute(String name) {
		return this.getIntAttribute(name, 0);
	}
public int getIntAttribute(String name, int defaultValue) {
		if (this.ignoreCase) {
			name = name.toUpperCase();
		}

		String value = (String) this.attributes.get(name);

		if (value == null) {
			return defaultValue;
		} else {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
				throw this.invalidValue(name, value);
			}
		}
	}
public int getIntAttribute(String name, Hashtable valueSet, String defaultKey, boolean allowLiteralNumbers) {
		if (this.ignoreCase) {
			name = name.toUpperCase();
		}

		Object key = this.attributes.get(name);
		Integer result;

		if (key == null) {
			key = defaultKey;
		}

		try {
			result = (Integer) valueSet.get(key);
		} catch (ClassCastException e) {
			throw this.invalidValueSet(name);
		}

		if (result == null) {
			if (!allowLiteralNumbers) {
				throw this.invalidValue(name, (String) key);
			}

			try {
				result = Integer.valueOf((String) key);
			} catch (NumberFormatException e) {
				throw this.invalidValue(name, (String) key);
			}
		}

		return result.intValue();
	}
public double getDoubleAttribute(String name) {
		return this.getDoubleAttribute(name, 0.);
	}
public double getDoubleAttribute(String name, double defaultValue) {
		if (this.ignoreCase) {
			name = name.toUpperCase();
		}

		String value = (String) this.attributes.get(name);

		if (value == null) {
			return defaultValue;
		} else {
			try {
				return Double.valueOf(value).doubleValue();
			} catch (NumberFormatException e) {
				throw this.invalidValue(name, value);
			}
		}
	}
public double getDoubleAttribute(String name, Hashtable valueSet, String defaultKey, boolean allowLiteralNumbers) {
		if (this.ignoreCase) {
			name = name.toUpperCase();
		}

		Object key = this.attributes.get(name);
		Double result;

		if (key == null) {
			key = defaultKey;
		}

		try {
			result = (Double) valueSet.get(key);
		} catch (ClassCastException e) {
			throw this.invalidValueSet(name);
		}

		if (result == null) {
			if (!allowLiteralNumbers) {
				throw this.invalidValue(name, (String) key);
			}

			try {
				result = Double.valueOf((String) key);
			} catch (NumberFormatException e) {
				throw this.invalidValue(name, (String) key);
			}
		}

		return result.doubleValue();
	}
public boolean getBooleanAttribute(String name, String trueValue, String falseValue, boolean defaultValue) {
		if (this.ignoreCase) {
			name = name.toUpperCase();
		}

		Object value = this.attributes.get(name);

		if (value == null) {
			return defaultValue;
		} else if (value.equals(trueValue)) {
			return true;
		} else if (value.equals(falseValue)) {
			return false;
		} else {
			throw this.invalidValue(name, (String) value);
		}
	}

	public boolean getBooleanAttribute(String name, boolean defaultValue) {
		return getBooleanAttribute(name, "true", "false", defaultValue);
	}
public int getIntProperty(String name, Hashtable valueSet, String defaultKey) {
		return this.getIntAttribute(name, valueSet, defaultKey, false);
	}
public String getProperty(String name) {
		return this.getStringAttribute(name);
	}
public String getProperty(String name, String defaultValue) {
		return this.getStringAttribute(name, defaultValue);
	}
public int getProperty(String name, int defaultValue) {
		return this.getIntAttribute(name, defaultValue);
	}
public double getProperty(String name, double defaultValue) {
		return this.getDoubleAttribute(name, defaultValue);
	}
public boolean getProperty(String key, String trueValue, String falseValue, boolean defaultValue) {
		return this.getBooleanAttribute(key, trueValue, falseValue, defaultValue);
	}
public Object getProperty(String name, Hashtable valueSet, String defaultKey) {
		return this.getAttribute(name, valueSet, defaultKey, false);
	}
public String getStringProperty(String name, Hashtable valueSet, String defaultKey) {
		return this.getStringAttribute(name, valueSet, defaultKey, false);
	}
public int getSpecialIntProperty(String name, Hashtable valueSet, String defaultKey) {
		return this.getIntAttribute(name, valueSet, defaultKey, true);
	}
public double getSpecialDoubleProperty(String name, Hashtable valueSet, String defaultKey) {
		return this.getDoubleAttribute(name, valueSet, defaultKey, true);
	}
public String getName() {
		return this.name;
	}
public String getTagName() {
		return this.getName();
	}
public void parseFromReader(Reader reader) throws IOException, XMLParseException {
		this.parseFromReader(reader, /* startingLineNr */1);
	}
public void parseFromReader(Reader reader, int startingLineNr) throws IOException, XMLParseException {
		this.charReadTooMuch = '\0';
		this.reader = reader;
		this.parserLineNr = startingLineNr;

		for (;;) {
			char ch = this.scanWhitespace();

			if (ch != '<') {
				throw this.expectedInput("<");
			}

			ch = this.readChar();

			if ((ch == '!') || (ch == '?')) {
				this.skipSpecialTag(0);
			} else {
				this.unreadChar(ch);
				this.scanElement(this);
				return;
			}
		}
	}

	public void parse(InputStream is, int startingLineNr) throws IOException, XMLParseException {
		// <?xml version="1.0" encoding="ISO-8859-1"?>
		// or <?xml version="1.0" encoding="UTF-8"?>

		// Very simple read the header and reset to beginning
		BufferedInputStream in = new BufferedInputStream(is);
		int maxHeader = 100;
		in.mark(maxHeader);
		StringBuffer fistLine = new StringBuffer();
		int c;
		while (((c = in.read()) != -1) && (fistLine.length() < maxHeader)) {
			fistLine.append((char) c);
			if (c == '>') {
				break;
			}
		}
		in.reset();
		String encoding = null;
		// Java 1.4
		Pattern pattern = Pattern.compile("(encoding=\")([\\p{Alnum}-]+)\"");
		Matcher matcher = pattern.matcher(fistLine.toString());
		if (matcher.find()) {
			encoding = matcher.group(2);
		}

		BufferedReader dis;
		if (encoding == null) {
			dis = new BufferedReader(new InputStreamReader(in));
		} else {
			dis = new BufferedReader(new InputStreamReader(in, encoding));
		}
		try {
			parseFromReader(dis, 1);
		} finally {
			dis.close();
			in.close();
		}
	}
public void parseString(String string) throws XMLParseException {
		try {
			this.parseFromReader(new StringReader(string),
1);
		} catch (IOException e) {
			// Java exception handling suxx
		}
	}
public void parseString(String string, int offset) throws XMLParseException {
		this.parseString(string.substring(offset));
	}
public void parseString(String string, int offset, int end) throws XMLParseException {
		this.parseString(string.substring(offset, end));
	}
public void parseString(String string, int offset, int end, int startingLineNr) throws XMLParseException {
		string = string.substring(offset, end);

		try {
			this.parseFromReader(new StringReader(string), startingLineNr);
		} catch (IOException e) {
			// Java exception handling suxx
		}
	}
public void parseCharArray(char[] input, int offset, int end) throws XMLParseException {
		this.parseCharArray(input, offset, end, /* startingLineNr */1);
	}
public void parseCharArray(char[] input, int offset, int end, int startingLineNr) throws XMLParseException {
		try {
			Reader reader = new CharArrayReader(input, offset, end);
			this.parseFromReader(reader, startingLineNr);
		} catch (IOException e) {
			// Java exception handling suxx
		}
	}
public void removeChild(XMLElement child) {
		this.children.removeElement(child);
	}

	public void removeChildren() {
		this.children.removeAllElements();
	}
public void removeAttribute(String name) {
		if (this.ignoreCase) {
			name = name.toUpperCase();
		}

		this.attributes.remove(name);
	}
public void removeProperty(String name) {
		this.removeAttribute(name);
	}
public void removeChild(String name) {
		this.removeAttribute(name);
	}
protected XMLElement createAnotherElement() {
		return new XMLElement(this.entities, this.ignoreWhitespace, false, this.ignoreCase);
	}
public void setContent(String content) {
		this.contents = content;
	}
public void setTagName(String name) {
		this.setName(name);
	}
public void setName(String name) {
		this.name = name;
	}
public String toString() {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(out);
			this.write(writer);
			writer.flush();
			return new String(out.toByteArray());
		} catch (IOException e) {
			// Java exception handling suxx
			return super.toString();
		}
	}
public void write(Writer writer) throws IOException {
		writeIdented(writer, 0);
	}

	private void writeTabs(Writer writer, int level) throws IOException {
		for (int i = 0; i < level; i++) {
			writer.write('\t');
		}
	}

	private void writeIdented(Writer writer, int level) throws IOException {
		if (this.name == null) {
			this.writeEncoded(writer, this.contents);
			return;
		}

		final boolean ident = true;

		if (ident) {
			writeTabs(writer, level);
		}

		writer.write('<');
		writer.write(this.name);

		if (!this.attributes.isEmpty()) {
			Enumeration en = this.attributes.keys();

			while (en.hasMoreElements()) {
				writer.write(' ');
				String key = (String) en.nextElement();
				String value = (String) this.attributes.get(key);
				writer.write(key);
				writer.write('=');
				writer.write('"');
				this.writeEncoded(writer, value);
				writer.write('"');
			}
		}

		if ((this.contents != null) && (this.contents.length() > 0)) {
			writer.write('>');
			this.writeEncoded(writer, this.contents);
			writer.write('<');
			writer.write('/');
			writer.write(this.name);
			writer.write('>');
			if (ident) {
				writer.write('\n');
			}
		} else if (this.children.isEmpty()) {
			writer.write('/');
			writer.write('>');
			if (ident) {
				writer.write('\n');
			}
		} else {
			writer.write('>');
			if (ident) {
				writer.write('\n');
			}
			Enumeration en = this.enumerateChildren();
			while (en.hasMoreElements()) {
				XMLElement child = (XMLElement) en.nextElement();
				child.writeIdented(writer, level + 1);
			}
			if (ident) {
				writeTabs(writer, level);
			}
			writer.write('<');
			writer.write('/');
			writer.write(this.name);
			writer.write('>');
			if (ident) {
				writer.write('\n');
			}
		}
	}
protected void writeEncoded(Writer writer, String str) throws IOException {
		for (int i = 0; i < str.length(); i += 1) {
			char ch = str.charAt(i);

			switch (ch) {
			case '<':
				writer.write('&');
				writer.write('l');
				writer.write('t');
				writer.write(';');
				break;

			case '>':
				writer.write('&');
				writer.write('g');
				writer.write('t');
				writer.write(';');
				break;

			case '&':
				writer.write('&');
				writer.write('a');
				writer.write('m');
				writer.write('p');
				writer.write(';');
				break;

			case '"':
				writer.write('&');
				writer.write('q');
				writer.write('u');
				writer.write('o');
				writer.write('t');
				writer.write(';');
				break;

			case '\'':
				writer.write('&');
				writer.write('a');
				writer.write('p');
				writer.write('o');
				writer.write('s');
				writer.write(';');
				break;

			default:
				int unicode = (int) ch;

				if ((unicode < 32) || (unicode > 126)) {
					writer.write('&');
					writer.write('#');
					writer.write('x');
					writer.write(Integer.toString(unicode, 16));
					writer.write(';');
				} else {
					writer.write(ch);
				}
			}
		}
	}
protected void scanIdentifier(StringBuffer result) throws IOException {
		for (;;) {
			char ch = this.readChar();

			if (((ch < 'A') || (ch > 'Z')) && ((ch < 'a') || (ch > 'z')) && ((ch < '0') || (ch > '9')) && (ch != '_')
					&& (ch != '.') && (ch != ':') && (ch != '-') && (ch <= '\u007E')) {
				this.unreadChar(ch);
				return;
			}

			result.append(ch);
		}
	}
protected char scanWhitespace() throws IOException {
		for (;;) {
			char ch = this.readChar();

			switch (ch) {
			case ' ':
			case '\t':
			case '\n':
			case '\r':
				break;

			default:
				return ch;
			}
		}
	}
protected char scanWhitespace(StringBuffer result) throws IOException {
		for (;;) {
			char ch = this.readChar();

			switch (ch) {
			case ' ':
			case '\t':
			case '\n':
				result.append(ch);

			case '\r':
				break;

			default:
				return ch;
			}
		}
	}
protected void scanString(StringBuffer string) throws IOException {
		char delimiter = this.readChar();

		if ((delimiter != '\'') && (delimiter != '"')) {
			throw this.expectedInput("' or \"");
		}

		for (;;) {
			char ch = this.readChar();

			if (ch == delimiter) {
				return;
			} else if (ch == '&') {
				this.resolveEntity(string);
			} else {
				string.append(ch);
			}
		}
	}
protected void scanPCData(StringBuffer data) throws IOException {
		for (;;) {
			char ch = this.readChar();

			if (ch == '<') {
				ch = this.readChar();

				if (ch == '!') {
					this.checkCDATA(data);
				} else {
					this.unreadChar(ch);
					return;
				}
			} else if (ch == '&') {
				this.resolveEntity(data);
			} else {
				data.append(ch);
			}
		}
	}
protected boolean checkCDATA(StringBuffer buf) throws IOException {
		char ch = this.readChar();

		if (ch != '[') {
			this.unreadChar(ch);
			this.skipSpecialTag(0);
			return false;
		} else if (!this.checkLiteral("CDATA[")) {
			this.skipSpecialTag(1); // one [ has already been read
			return false;
		} else {
			int delimiterCharsSkipped = 0;

			while (delimiterCharsSkipped < 3) {
				ch = this.readChar();
				switch (ch) {
				case ']':
					if (delimiterCharsSkipped < 2) {
						delimiterCharsSkipped += 1;
					} else {
						buf.append(']');
						buf.append(']');
						delimiterCharsSkipped = 0;
					}

					break;

				case '>':
					if (delimiterCharsSkipped < 2) {
						for (int i = 0; i < delimiterCharsSkipped; i++) {
							buf.append(']');
						}

						delimiterCharsSkipped = 0;
						buf.append('>');
					} else {
						delimiterCharsSkipped = 3;
					}

					break;

				default:
					for (int i = 0; i < delimiterCharsSkipped; i += 1) {
						buf.append(']');
					}

					buf.append(ch);
					delimiterCharsSkipped = 0;
				}
			}

			return true;
		}
	}
protected void skipComment() throws IOException {
		int dashesToRead = 2;

		while (dashesToRead > 0) {
			char ch = this.readChar();

			if (ch == '-') {
				dashesToRead -= 1;
			} else {
				dashesToRead = 2;
			}
		}

		if (this.readChar() != '>') {
			throw this.expectedInput(">");
		}
	}
protected void skipSpecialTag(int bracketLevel) throws IOException {
		int tagLevel = 1; // <
		char stringDelimiter = '\0';

		if (bracketLevel == 0) {
			char ch = this.readChar();

			if (ch == '[') {
				bracketLevel += 1;
			} else if (ch == '-') {
				ch = this.readChar();

				if (ch == '[') {
					bracketLevel += 1;
				} else if (ch == ']') {
					bracketLevel -= 1;
				} else if (ch == '-') {
					this.skipComment();
					return;
				}
			}
		}

		while (tagLevel > 0) {
			char ch = this.readChar();

			if (stringDelimiter == '\0') {
				if ((ch == '"') || (ch == '\'')) {
					stringDelimiter = ch;
				} else if (bracketLevel <= 0) {
					if (ch == '<') {
						tagLevel += 1;
					} else if (ch == '>') {
						tagLevel -= 1;
					}
				}

				if (ch == '[') {
					bracketLevel += 1;
				} else if (ch == ']') {
					bracketLevel -= 1;
				}
			} else {
				if (ch == stringDelimiter) {
					stringDelimiter = '\0';
				}
			}
		}
	}
protected boolean checkLiteral(String literal) throws IOException {
		int length = literal.length();

		for (int i = 0; i < length; i += 1) {
			if (this.readChar() != literal.charAt(i)) {
				return false;
			}
		}

		return true;
	}
protected char readChar() throws IOException {
		if (this.charReadTooMuch != '\0') {
			char ch = this.charReadTooMuch;
			this.charReadTooMuch = '\0';
			return ch;
		} else {
			int i = this.reader.read();

			if (i < 0) {
				throw this.unexpectedEndOfData();
			} else if (i == 10) {
				this.parserLineNr += 1;
				return '\n';
			} else {
				return (char) i;
			}
		}
	}
protected void scanElement(XMLElement elt) throws IOException {
		StringBuffer buf = new StringBuffer();
		this.scanIdentifier(buf);
		String name = buf.toString();
		elt.setName(name);
		char ch = this.scanWhitespace();

		while ((ch != '>') && (ch != '/')) {
			buf.setLength(0);
			this.unreadChar(ch);
			this.scanIdentifier(buf);
			String key = buf.toString();
			ch = this.scanWhitespace();

			if (ch != '=') {
				throw this.expectedInput("=");
			}

			this.unreadChar(this.scanWhitespace());
			buf.setLength(0);
			this.scanString(buf);
			elt.setAttribute(key, buf);
			ch = this.scanWhitespace();
		}

		if (ch == '/') {
			ch = this.readChar();

			if (ch != '>') {
				throw this.expectedInput(">");
			}

			return;
		}

		buf.setLength(0);
		ch = this.scanWhitespace(buf);

		if (ch != '<') {
			this.unreadChar(ch);
			this.scanPCData(buf);
		} else {
			for (;;) {
				ch = this.readChar();

				if (ch == '!') {
					if (this.checkCDATA(buf)) {
						this.scanPCData(buf);
						break;
					} else {
						ch = this.scanWhitespace(buf);
						if (ch != '<') {
							this.unreadChar(ch);
							this.scanPCData(buf);
							break;
						}
					}
				} else {
					buf.setLength(0);
					break;
				}
			}
		}

		if (buf.length() == 0) {
			while (ch != '/') {
				if (ch == '!') {
					ch = this.readChar();
					if (ch != '-') {
						throw this.expectedInput("Comment or Element");
					}
					ch = this.readChar();
					if (ch != '-') {
						throw this.expectedInput("Comment or Element");
					}
					this.skipComment();
				} else {
					this.unreadChar(ch);
					XMLElement child = this.createAnotherElement();
					this.scanElement(child);
					elt.addChild(child);
				}
				ch = this.scanWhitespace();
				if (ch != '<') {
					throw this.expectedInput("<");
				}
				ch = this.readChar();
			}

			this.unreadChar(ch);
		} else {
			if (this.ignoreWhitespace) {
				elt.setContent(buf.toString().trim());
			} else {
				elt.setContent(buf.toString());
			}
		}

		ch = this.readChar();

		if (ch != '/') {
			throw this.expectedInput("/");
		}

		this.unreadChar(this.scanWhitespace());

		if (!this.checkLiteral(name)) {
			throw this.expectedInput(name);
		}

		if (this.scanWhitespace() != '>') {
			throw this.expectedInput(">");
		}
	}
protected void resolveEntity(StringBuffer buf) throws IOException {
		char ch = '\0';
		StringBuffer keyBuf = new StringBuffer();

		for (;;) {
			ch = this.readChar();

			if (ch == ';') {
				break;
			}

			keyBuf.append(ch);
		}

		String key = keyBuf.toString();

		if (key.charAt(0) == '#') {
			try {
				if (key.charAt(1) == 'x') {
					ch = (char) Integer.parseInt(key.substring(2), 16);
				} else {
					ch = (char) Integer.parseInt(key.substring(1), 10);
				}
			} catch (NumberFormatException e) {
				throw this.unknownEntity(key);
			}

			buf.append(ch);
		} else {
			char[] value = (char[]) this.entities.get(key);

			if (value == null) {
				throw this.unknownEntity(key);
			}

			buf.append(value);
		}
	}
protected void unreadChar(char ch) {
		this.charReadTooMuch = ch;
	}
protected XMLParseException invalidValueSet(String name) {
		String msg = "Invalid value set (entity name = \"" + name + "\")";
		return new XMLParseException(this.getName(), this.parserLineNr, msg);
	}
protected XMLParseException invalidValue(String name, String value) {
		String msg = "Attribute \"" + name + "\" does not contain a valid " + "value (\"" + value + "\")";
		return new XMLParseException(this.getName(), this.parserLineNr, msg);
	}
protected XMLParseException unexpectedEndOfData() {
		String msg = "Unexpected end of data reached";
		return new XMLParseException(this.getName(), this.parserLineNr, msg);
	}
protected XMLParseException syntaxError(String context) {
		String msg = "Syntax error while parsing " + context;
		return new XMLParseException(this.getName(), this.parserLineNr, msg);
	}
protected XMLParseException expectedInput(String charSet) {
		String msg = "Expected: " + charSet;
		return new XMLParseException(this.getName(), this.parserLineNr, msg);
	}
protected XMLParseException unknownEntity(String name) {
		String msg = "Unknown or invalid entity: &" + name + ";";
		return new XMLParseException(this.getName(), this.parserLineNr, msg);
	}

}
