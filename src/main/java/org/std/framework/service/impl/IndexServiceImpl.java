package org.std.framework.service.impl;

import org.std.framework.annotation.CustomAutowire;
import org.std.framework.annotation.CustomService;
import org.std.framework.dao.IndexDao;
import org.std.framework.service.IndexService;

@CustomService
public class IndexServiceImpl implements IndexService{
	@CustomAutowire
	private IndexDao indexDao;
	
	public void setIndexDao(IndexDao indexDao) {
		this.indexDao = indexDao;
	}

	@Override
	public String query(String id) {
		return indexDao.query(id);
	}

	@Override
	public void insert(String id, String name) {
		indexDao.insert(id, name);
	}

}
