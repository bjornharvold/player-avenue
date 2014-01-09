package com.online.casino.service.impl;

import com.online.casino.domain.entity.ApplicationUser;
import com.online.casino.service.AdministrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements UserDetailsService {
    private final AdministrationService administrationService;

    @Autowired
    public UserServiceImpl(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    @Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        UserDetails result = ApplicationUser.findApplicationUserByUsername(username);

		return result != null ? result : new ApplicationUser();
	}

}
