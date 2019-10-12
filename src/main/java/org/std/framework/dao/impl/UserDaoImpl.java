package org.std.framework.dao.impl;

import org.std.framework.annotation.CustomReposity;
import org.std.framework.dao.UserDao;

@CustomReposity
public class UserDaoImpl implements UserDao{

	@Override
	public void getUser() {
		System.out.println("UserDaoImpl getUser");
	}

	@Override
	public void insertUser() {
		System.out.println("UserDaoImpl insertUser");
	}

}
