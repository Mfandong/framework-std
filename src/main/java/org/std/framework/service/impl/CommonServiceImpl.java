package org.std.framework.service.impl;

import org.std.framework.annotation.CustomAutowire;
import org.std.framework.annotation.CustomService;
import org.std.framework.dao.IndexDao;
import org.std.framework.dao.UserDao;
import org.std.framework.service.CommonService;

@CustomService
public class CommonServiceImpl implements CommonService{
	@CustomAutowire
	private UserDao userDao;
	@CustomAutowire
	private IndexDao indexDao;
//	public CommonServiceImpl(UserDao userDao, IndexDao indexDao) {
//		this.userDao = userDao;
//		this.indexDao = indexDao;
//	}
	@Override
	public void commonQuery() {
		userDao.getUser();
		indexDao.query("1");
	}

	@Override
	public void commonInsert() {
		userDao.insertUser();
		indexDao.insert("1", "ÕÅÈý");
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setIndexDao(IndexDao indexDao) {
		this.indexDao = indexDao;
	}
}
