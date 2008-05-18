package com.fcg.style3.dao.impl;

import org.springframework.stereotype.Repository;

import com.fcg.style3.dao.AdminUserDAO;
import com.fcg.style3.domain.AdminUser;

@Repository
public class AdminUserDAOImpl extends GenericDAOImpl<AdminUser, Long>
        implements AdminUserDAO {

}
