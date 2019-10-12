package std1.test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import std1.config.AopConfig;
import std1.dao.AopDao;

public class AopTest {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AopConfig.class);
//		AopDao dao = context.getBean(AopDao.class);
//		System.out.println(dao.getClass());
//		dao.query();
//		dao.insert();
		
		AopDao dao1 = (AopDao) context.getBean("aopDaoImpl");
		AopDao dao2 = (AopDao) context.getBean("aopDaoImpl2");
		dao1.query();
		dao1.insert();
		dao1.ret();
		
		dao2.query();
		dao2.insert();
		dao2.ret();
	}
}
