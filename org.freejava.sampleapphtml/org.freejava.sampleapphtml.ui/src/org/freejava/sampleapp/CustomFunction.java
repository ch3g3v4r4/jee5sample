package org.freejava.sampleapp;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

public class CustomFunction extends BrowserFunction {
	CustomFunction (Browser browser, String name) {
		super (browser, name);
	}
	public Object function (Object[] arguments) {
		System.out.println ("javahandle() called from javascript with args:");
		for (int i = 0; i < arguments.length; i++) {
			Object arg = arguments[i];
			if (arg == null) {
				System.out.println ("\t-->null");
			} else {
				System.out.println ("\t-->" + arg.getClass ().getName () + ": " + arg.toString ());
			}
		}
		Object returnValue = new Object[] {
			new Short ((short)3),
			new Boolean (true),
			null,
			new Object[] {"a string", new Boolean (false)},
			"hi",
			new Float (2.0f / 3.0f),
		};
		//int z = 3 / 0; // uncomment to cause a java error instead
		return returnValue;
	}
}