package com.fcg.style3.hibernate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.aop.TargetSource;

public class SessionFactoryTargetSource implements TargetSource {
    private static Log logger = LogFactory.getLog(SessionFactoryBean.class);

    private Map<String, SessionFactory> sessionFactories = new HashMap<String, SessionFactory>();

    public void setSessionFactories(Map<String, SessionFactory> sessionFactories) {
        this.sessionFactories = sessionFactories;
    }

    public Class<SessionFactory> getTargetClass() {
        return SessionFactory.class;
    }

    public boolean isStatic() {
        return false;
    }

    public Object getTarget() throws Exception {
        SessionFactory sf;
        Date date = new Date();
        SessionFactory[] arr = (SessionFactory[]) sessionFactories.values().toArray(new SessionFactory[sessionFactories.size()]);
        if (date.getMinutes() % 2 == 0) {
            logger.info("Returning sesstionFactory at 0");
            sf = arr[0];
        } else {
            logger.info("Returning sesstionFactory at 1");
            sf = arr[1];
        }

        return sf;
    }

    public void releaseTarget(Object target) throws Exception {
        // no-op
    }

}
