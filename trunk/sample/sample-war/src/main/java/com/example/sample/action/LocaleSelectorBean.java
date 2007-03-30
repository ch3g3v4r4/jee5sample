package com.example.sample.action;

import java.util.Locale;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class LocaleSelectorBean {
	private String locale;

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}
	
    public String changeLocale() {
    	
    	FacesContext facesContext = FacesContext.getCurrentInstance();
    	ExternalContext externalContext = facesContext.getExternalContext();
    	HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
    	Locale localeObject = new Locale(locale);
    	
    	facesContext.getViewRoot().setLocale(localeObject);
    	Cookie localeCookie = new Cookie(LocalePhaseListener.LOCALE_COOKIE_NAME, locale);
    	String requestContextPath = externalContext.getRequestContextPath();
    	localeCookie.setPath(requestContextPath.equals("") ? "/" : requestContextPath);
    	response.addCookie(localeCookie);
        return "success";
    }
}
