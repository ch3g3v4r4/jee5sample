package org.freejava.android;

import org.apache.commons.lang.SystemUtils;

import com.sun.jna.Library;
import com.sun.jna.Native;

interface WinLibC extends Library {
	public int _putenv(String name);
}

interface LinuxLibC extends Library {
	public int setenv(String name, String value, int overwrite);

	public int unsetenv(String name);
}

public class POSIX {
	static Object libc;

	static {
		if (SystemUtils.IS_OS_WINDOWS)
			libc = Native.loadLibrary("msvcrt", WinLibC.class);
		else
			libc = Native.loadLibrary("c", LinuxLibC.class);
	}

	public int setenv(String name, String value, int overwrite) {
		if (libc instanceof LinuxLibC) {
			return ((LinuxLibC) libc).setenv(name, value, overwrite);
		} else {
			return ((WinLibC) libc)._putenv(name + "=" + value);
		}
	}

	public int unsetenv(String name) {
		if (libc instanceof LinuxLibC) {
			return ((LinuxLibC) libc).unsetenv(name);
		} else {
			return ((WinLibC) libc)._putenv(name + "=");
		}
	}
}
