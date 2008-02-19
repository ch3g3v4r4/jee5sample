package org.builder.eclipsebuilder;

import org.openqa.selenium.server.SeleniumServer;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;


public class Main1 {

    public static void main(String[] args) throws Exception {
        Selenium selenium = new DefaultSelenium( "localhost",
                4444,
                "*firefox",
                "http://www.eclipse.org");
        SeleniumServer seleniumServer = new SeleniumServer();
        seleniumServer.start();
        selenium.start();
        try {
            selenium.open("http://download.eclipse.org/eclipse/downloads/");

    } catch (SeleniumException ex) {
            //throw ex;
    }
        selenium.stop();
        seleniumServer.stop();
    }
}
