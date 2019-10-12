package std1.dao.impl;

import org.springframework.stereotype.Repository;

import std1.annotation.AopAnnotation;
import std1.dao.AopDao;

@Repository
@AopAnnotation
public class AopDaoImpl implements AopDao{

	@Override
	public void query() {
		System.out.println("query");
	}
	
	@Override
	@AopAnnotation("insert1111")
	public void insert() {
		System.out.println("insert");
	}
	
	@Override
	public String ret() {
		System.out.println("ret");
		return "AopDaoImpl";
	}
}
