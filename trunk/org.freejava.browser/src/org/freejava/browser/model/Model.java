package org.freejava.browser.model;

import java.util.HashMap;
import java.util.Map;

import org.freejava.browser.RCPLogger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class Model {

	private Map<String, String> properties;

	private String result;

	public String getResult() {
		return result;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = new HashMap<String, String>(properties);
	}

	public void execute() {
		RCPLogger logger= new RCPLogger();
		logger.logInfo(properties.toString(), null);
        WebDriver driver = new HtmlUnitDriver();


        // And now use this to visit Google
        driver.get("http://www.google.com/");

        // Find the text input element by its name
        WebElement element = driver.findElement(By.name("q"));

        // Enter something to search for
        element.sendKeys("Cheese!");

        // Now submit the form. WebDriver will find the form for us from the element
        element.submit();

        // Check the title of the page
        result = driver.getTitle();
	}
}
