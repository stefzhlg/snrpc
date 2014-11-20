package org.stefan.snrpc.util;

/**
 * @author zhaoliangang 2014-11-12
 */
public class StringUtil {

	public static boolean isEmpty(String str) {
		return str == null || "".equals(str.trim()) ? true : false;
	}

	public static boolean isNotEmpty(String str) {
		return str == null || "".equals(str.trim()) ? false : true;
	}

}
