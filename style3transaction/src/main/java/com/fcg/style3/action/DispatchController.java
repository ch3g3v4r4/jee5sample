package com.fcg.style3.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.fcg.style3.dao.AdminUserDAO;
import com.fcg.style3.dao.LogDAO;
import com.fcg.style3.domain.AdminUser;
import com.fcg.style3.domain.Log;

@Transactional
public class DispatchController extends MultiActionController {
    private AdminUserDAO adminUserDao;
    private LogDAO logDao;

    public void setAdminUserDao(AdminUserDAO adminUserDao) {
        this.adminUserDao = adminUserDao;
    }
    public void setLogDao(LogDAO logDao) {
        this.logDao = logDao;
    }

    public ModelAndView actionName(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        AdminUser adminUser = new AdminUser();
        adminUser.setActive(true);
        adminUser.setEmail("tha@fcg.com");
        adminUser.setFirstName("Thai");
        adminUser.setLastName("Ha");
        adminUser.setPassword("secret");
        adminUser.setUsername("thaiha");
        adminUserDao.persist1(adminUser);
        AdminUser adminUser2 = new AdminUser();
        adminUser2.setActive(true);
        adminUser2.setEmail("tha@fcg.com");
        adminUser2.setFirstName("Thai");
        adminUser2.setLastName("Ha");
        adminUser2.setPassword("secret");
        adminUser2.setUsername("thaiha");
        adminUserDao.persist1(adminUser2);
        Log log = new Log();
        log.setLog("Hello");
        logDao.persist1(log);
        System.out.println(adminUserDao.getClass());
        return null;
    }
}
