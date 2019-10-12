package org.std.framework.dao.impl;

import org.std.framework.dao.IndexDao;

public class IndexDaoImpl2 implements IndexDao{

	@Override
	public String query(String id) {
		System.out.println("IndexDaoImpl2 query id --> " + id);
		return "IndexDaoImpl2 query success!";
	}

	@Override
	public void insert(String id, String name) {
		System.out.println("IndexDaoImpl2 insert id --> " + id + ", name --> " + name);
	}

}
