package std1.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import std1.dao.AopDao;

public class AopXmlTest {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/spring-aop.xml");
		AopDao dao = (AopDao) context.getBean("aopXmlDaoImpl");
		dao.query();
		dao.insert();
		dao.ret();
	}
}
