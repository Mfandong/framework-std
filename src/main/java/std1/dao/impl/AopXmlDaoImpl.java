package std1.dao.impl;

import std1.annotation.AopAnnotation;
import std1.dao.AopDao;

public class AopXmlDaoImpl implements AopDao{

	@Override
	@AopAnnotation
	public void query() {
		System.out.println("---------AopXmlDaoImpl query--------");
	}

	@Override
	public void insert() {
		System.out.println("---------AopXmlDaoImpl insert--------");
	}

	@Override
	public String ret() {
		System.out.println("---------AopXmlDaoImpl ret--------");
		return "AopXmlDaoImpl";
	}

}
