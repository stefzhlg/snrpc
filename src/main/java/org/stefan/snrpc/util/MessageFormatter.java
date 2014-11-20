package org.stefan.snrpc.util;

import java.util.HashMap;
import java.util.Map;


/**
 * @author zhaoliangang
 *	2014-11-20
 */
public class MessageFormatter {
	public static final String DEFAULT_PLACE_HOLDER = "{}";

	public static final char DEFAULT_ESCAPE_CHAR = '\\';

	/**
	 * equivalent to
	 * <code>LogFormatter.format(msgPattern, "{}", '\\', args)</code>
	 * 
	 * @param msgPattern
	 * @param args
	 * @return
	 */
	public static String format(String msgPattern, Object[] args) {
		return format(msgPattern, DEFAULT_PLACE_HOLDER, DEFAULT_ESCAPE_CHAR,
				args);
	}

	/**
	 * equivalent to
	 * <code>LogFormatter.format(msgPattern, placeholder, '\\', args)</code>
	 * 
	 * @param msgPattern
	 * @param placeholder
	 * @param args
	 * @return
	 */
	public static String format(String msgPattern, String placeholder,
			Object[] args) {
		return format(msgPattern, placeholder, DEFAULT_ESCAPE_CHAR, args);
	}

	/**
	 * @param msgPattern
	 * @param placeholder
	 * @param escapeChar
	 * @param args
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String format(String msgPattern, String placeholder,
			char escapeChar, Object[] args) {
		if (null == msgPattern || msgPattern.length() == 0) {
			return null;
		}
		if (null == args || args.length == 0) {
			return msgPattern;
		}
		if (null == placeholder || "".equals(placeholder)) {
			return msgPattern;
		}

		int lastMatchedIndex = 0, currentMatchedIndex = 0;
		StringBuilder strbuf = new StringBuilder(msgPattern.length() + 64);

		for (int argIndex = 0; argIndex < args.length; argIndex++) {
			currentMatchedIndex = msgPattern.indexOf(placeholder,
					lastMatchedIndex);

			if (-1 == currentMatchedIndex) {
				// no more variables
				if (0 == lastMatchedIndex) {
					// this is a simple string
					return msgPattern;
				} else {
					// add the tail string which contains no variables
					strbuf.append(msgPattern.substring(lastMatchedIndex,
							msgPattern.length()));
					return strbuf.toString();
				}
			} else {
				// successive escape chars before the placeholder
				int cnt = countSuccessiveEscapeChar(msgPattern, escapeChar,
						currentMatchedIndex, lastMatchedIndex);
				if (0 == cnt) { // all escaped itself
					strbuf.append(msgPattern.substring(lastMatchedIndex,
							currentMatchedIndex));
					deeplyAppendParameter(strbuf, args[argIndex], new HashMap());
					lastMatchedIndex = currentMatchedIndex
							+ placeholder.length();
				} else {
					int escapeItselfCnt = cnt / 2;
					strbuf.append(msgPattern.substring(lastMatchedIndex,
							(currentMatchedIndex - cnt + escapeItselfCnt)));
					if (cnt % 2 != 0) {
						argIndex--;// placeholder was escaped, thus should not
									// be incremented
						strbuf.append(placeholder.charAt(0));
						lastMatchedIndex = currentMatchedIndex + 1;
					} else {
						deeplyAppendParameter(strbuf, args[argIndex],
								new HashMap());
						lastMatchedIndex = currentMatchedIndex
								+ placeholder.length();
					}
				}
			}
		}
		// append the characters following the last {} pair.
		strbuf.append(msgPattern.substring(lastMatchedIndex,
				msgPattern.length()));
		return strbuf.toString();
	}

	private static int countSuccessiveEscapeChar(String msgPattern,
			char escapeChar, int delimeterStartIndex, int delimeterStopIndex) {
		if (0 == delimeterStartIndex) {
			return 0;
		}
		int cnt = 0;
		for (int i = delimeterStartIndex - 1; i >= delimeterStopIndex; i--) {
			if (msgPattern.charAt(i) == escapeChar) {
				++cnt;
			} else {
				break;
			}
		}
		return cnt;
	}

	@SuppressWarnings("rawtypes")
	private static void deeplyAppendParameter(StringBuilder sbuf, Object o,
			Map seenMap) {
		if (o == null) {
			sbuf.append("null");
			return;
		}
		if (!o.getClass().isArray()) {
			safeObjectAppend(sbuf, o);
		} else {
			// check for primitive array types because they
			// unfortunately cannot be cast to Object[]
			if (o instanceof boolean[]) {
				booleanArrayAppend(sbuf, (boolean[]) o);
			} else if (o instanceof byte[]) {
				byteArrayAppend(sbuf, (byte[]) o);
			} else if (o instanceof char[]) {
				charArrayAppend(sbuf, (char[]) o);
			} else if (o instanceof short[]) {
				shortArrayAppend(sbuf, (short[]) o);
			} else if (o instanceof int[]) {
				intArrayAppend(sbuf, (int[]) o);
			} else if (o instanceof long[]) {
				longArrayAppend(sbuf, (long[]) o);
			} else if (o instanceof float[]) {
				floatArrayAppend(sbuf, (float[]) o);
			} else if (o instanceof double[]) {
				doubleArrayAppend(sbuf, (double[]) o);
			} else {
				objectArrayAppend(sbuf, (Object[]) o, seenMap);
			}
		}
	}

	private static void safeObjectAppend(StringBuilder sbuf, Object o) {
		try {
			String oAsString = o.toString();
			sbuf.append(oAsString);
		} catch (Throwable t) {
			System.err
					.println("Failed toString() invocation on an object of type ["
							+ o.getClass().getName() + "]");
			t.printStackTrace();
			sbuf.append("[FAILED toString()]");
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void objectArrayAppend(StringBuilder sbuf, Object[] a,
			Map seenMap) {
		sbuf.append('[');
		if (!seenMap.containsKey(a)) {
			seenMap.put(a, null);
			final int len = a.length;
			for (int i = 0; i < len; i++) {
				deeplyAppendParameter(sbuf, a[i], seenMap);
				if (i != len - 1) {
					sbuf.append(", ");
				}
			}
			// allow repeats in siblings
			seenMap.remove(a);
		} else {
			sbuf.append("...");
		}
		sbuf.append(']');
	}

	private static void booleanArrayAppend(StringBuilder sbuf, boolean[] a) {
		sbuf.append('[');
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			sbuf.append(a[i]);
			if (i != len - 1) {
				sbuf.append(", ");
			}
		}
		sbuf.append(']');
	}

	private static void byteArrayAppend(StringBuilder sbuf, byte[] a) {
		sbuf.append('[');
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			sbuf.append(a[i]);
			if (i != len - 1)
				sbuf.append(", ");
		}
		sbuf.append(']');
	}

	private static void charArrayAppend(StringBuilder sbuf, char[] a) {
		sbuf.append('[');
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			sbuf.append(a[i]);
			if (i != len - 1)
				sbuf.append(", ");
		}
		sbuf.append(']');
	}

	private static void shortArrayAppend(StringBuilder sbuf, short[] a) {
		sbuf.append('[');
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			sbuf.append(a[i]);
			if (i != len - 1) {
				sbuf.append(", ");
			}
		}
		sbuf.append(']');
	}

	private static void intArrayAppend(StringBuilder sbuf, int[] a) {
		sbuf.append('[');
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			sbuf.append(a[i]);
			if (i != len - 1) {
				sbuf.append(", ");
			}
		}
		sbuf.append(']');
	}

	private static void longArrayAppend(StringBuilder sbuf, long[] a) {
		sbuf.append('[');
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			sbuf.append(a[i]);
			if (i != len - 1) {
				sbuf.append(", ");
			}
		}
		sbuf.append(']');
	}

	private static void floatArrayAppend(StringBuilder sbuf, float[] a) {
		sbuf.append('[');
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			sbuf.append(a[i]);
			if (i != len - 1) {
				sbuf.append(", ");
			}
		}
		sbuf.append(']');
	}

	private static void doubleArrayAppend(StringBuilder sbuf, double[] a) {
		sbuf.append('[');
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			sbuf.append(a[i]);
			if (i != len - 1) {
				sbuf.append(", ");
			}
		}
		sbuf.append(']');
	}
}
