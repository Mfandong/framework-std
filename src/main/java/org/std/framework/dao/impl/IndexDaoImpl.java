package org.std.framework.dao.impl;

import org.std.framework.annotation.CustomReposity;
import org.std.framework.dao.IndexDao;

@CustomReposity
public class IndexDaoImpl implements IndexDao{

	@Override
	public String query(String id) {
		System.out.println("IndexDaoImpl query id --> " + id);
		return "IndexDaoImpl query success!";
	}

	@Override
	public void insert(String id, String name) {
		System.out.println("IndexDaoImpl insert id --> " + id + ", name --> " + name);
	}

}
