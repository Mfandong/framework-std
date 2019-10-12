package std1.dao.impl;

import org.springframework.stereotype.Repository;

import std1.dao.AopDao;

@Repository
public class AopDaoImpl2 implements AopDao{

	@Override
	public void query() {
		System.out.println("AopImpl2 query");
	}

	@Override
	public void insert() {
		System.out.println("AopImpl2 insert");
	}

	@Override
	public String ret() {
		System.out.println("AopImpl2 ret");
		return "AopDaoImpl2";
	}

}
