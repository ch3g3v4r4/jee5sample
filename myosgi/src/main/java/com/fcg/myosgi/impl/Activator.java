package com.fcg.myosgi.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {	
	private BundleContext context;

	public void start(BundleContext context) throws Exception {
		this.context = context;
		System.out.println("Hello!");
	} 

	public void stop(BundleContext arg0) throws Exception {
		System.out.println("Goodbye!");

	}
}
