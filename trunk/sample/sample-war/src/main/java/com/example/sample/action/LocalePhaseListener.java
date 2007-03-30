package com.example.sample.action;

import java.util.Locale;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

/**
 * This Phase Listener class will restore locale which is store in cookie.
 * 
 * @author tha
 *
 */
public class LocalePhaseListener implements PhaseListener {

	public static String LOCALE_COOKIE_NAME = "LOCALE_COOKIE";
	
	public void afterPhase(PhaseEvent arg0) {
		// Do nothing
	}

	public void beforePhase(PhaseEvent event) {
		FacesContext facesContext = event.getFacesContext();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String,Object> requestCookieMap = externalContext.getRequestCookieMap();
		if (requestCookieMap != null && requestCookieMap.containsKey(LOCALE_COOKIE_NAME)) {
			String locale = (String) requestCookieMap.get(LOCALE_COOKIE_NAME);
			if (locale != null && !locale.equals("")) {
				facesContext.getViewRoot().setLocale(new Locale(locale));
			}
		}
	}

	public PhaseId getPhaseId() {
		return PhaseId.RENDER_RESPONSE;
	}
}
