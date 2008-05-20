package com.fcg.style3.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.fcg.style3.dao.AdminUserDAO;
import com.fcg.style3.domain.AdminUser;

@Transactional
public class AdminUserDAOImpl extends GenericDAOImpl<AdminUser, Long>
        implements AdminUserDAO {

}
