/*****************************************************************************
 *
 * Copyright (c) 2008 DxTech, LLC. All rights reserved.
 *
 * This is unpublished proprietary source code of DxTech, LLC.
 *
 * The copyright notice above does not evidence any actual or intended
 * publication of such source code.
 *
 *****************************************************************************/

package com.fcg.style3.hibernate;

import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.FactoryBean;


/**
 * SessionFactoryBean class.
 */
public class SessionFactoryBean implements FactoryBean {

    private static final String DEFAULT_DS = "default";
    /**
     * Map of <code>SessionFactory</code> objects. This FactoryBean class will
     * return one of the SessionFactory classes in this map.
     */
    private Map<String, SessionFactory> sessionFactoryMap;

    /**
     * Loggger for class.
     */
    private static Log logger = LogFactory.getLog(SessionFactoryBean.class);

    /**
     * Sets the map of SessionFactory objects.
     *
     * @param sessionFactoryMap
     *            the SessionFactory map.
     */
    public void setSessionFactoryMap(
            Map<String, SessionFactory> sessionFactoryMap) {
        this.sessionFactoryMap = sessionFactoryMap;
    }

    /**
     * Returns the appropriate SessionFactory based on the user's owner (the
     * customer).
     *
     * @throws Exception
     *             there is any problems, the application throw Exception
     * @return section factory bean
     */
    public Object getObject() throws Exception {
        SessionFactory sf;
        Date date = new Date();
        SessionFactory[] arr =  (SessionFactory[]) sessionFactoryMap.keySet().toArray(new SessionFactory[sessionFactoryMap.size()]);
        if (date.getMinutes() % 2 == 0) {
            logger.info("Returning sesstionFactory at 0");
            sf = arr[0];
        } else {
            logger.info("Returning sesstionFactory at 1");
            sf = arr[1];
        }
        return sf;

    }

    /**
     * Returns instance of SectionFactory class.
     *
     * @return SectionFactory class
     */
    @SuppressWarnings("unchecked")
    public Class getObjectType() {
        return SessionFactory.class;
    }

    /**
     * This class is a non-singleton because the particular SessionFactory to
     * return is determined at runtime and can vary during the application
     * lifecycle as different users log in. Therefore, the result of this bean
     * should not be cached.
     *
     * @return class is singleton
     */
    public boolean isSingleton() {
        return false;
    }

}
