package com.fcg.style3.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.fcg.style3.dao.AdminUserDAO;
import com.fcg.style3.domain.AdminUser;

public class DispatchController extends MultiActionController {
    private AdminUserDAO adminUserDao;

    public void setAdminUserDao(AdminUserDAO adminUserDao) {
        this.adminUserDao = adminUserDao;
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
        adminUserDao.persist(adminUser);
        System.out.println(adminUserDao.getClass());
        return null;
    }
}
