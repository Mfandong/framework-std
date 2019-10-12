package org.std.framework.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.std.framework.factory.CustomBeanFactory;
import org.std.framework.service.CommonService;
import org.std.framework.service.IndexService;

public class BeanFactoryTest {

	public static void main(String[] args) throws IOException {
//		System.out.println(BeanFactoryTest.class.getResource("/").getPath()+"custom/spring-custom.xml");
//		File file = new File(BeanFactoryTest.class.getResource("/").getPath()+"custom/spring-custom.xml");
//		File file2 = new File("D:/work%20soft/eclipse/workspace/20190925lmzc/std1/target/classes/custom/spring-custom.xml");
//		System.out.println(file2.exists());
		CustomBeanFactory beanFactory = new CustomBeanFactory("custom/spring-custom.xml");
		IndexService indexService = (IndexService) beanFactory.getBean("indexService");
		indexService.query("1");
		CommonService commonService = (CommonService) beanFactory.getBean("commonService");
		commonService.commonQuery();
	}
}
