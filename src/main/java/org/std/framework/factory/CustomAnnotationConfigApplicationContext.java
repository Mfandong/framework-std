package org.std.framework.factory;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.std.framework.service.CommonService;

public class CustomAnnotationConfigApplicationContext {
	private Map<String, Object> map = new HashMap<String, Object>();
	private final static String SCAN_ANNO_NAME = "CustomService,CustomReposity,CustomComponent";
	
	public void scan(String basePackage) {
		String realBasePackage = basePackage.replace(".", "\\\\");
		String path = this.getClass().getResource("/").getPath().replace("%20", " ") + realBasePackage;
		File file = new File(path);
		registerFileAnno(file, basePackage);
		System.out.println(map.toString());
	}
	
	public Object getBean(String beanName) {
		return map.get(beanName);
	}
	
	/**
	 * 加载指定目录下的class文件，含有指定注解的，实例化bean放入容器中
	 * @param file
	 * @param basePackage
	 */
	@SuppressWarnings("rawtypes")
	private void registerFileAnno(File file, String basePackage) {
		File[] children = file.listFiles();
		if (children != null) {
			for (File child : children) {
				//判断文件是否目录，是目录的递归加载
				if (child.isDirectory()) {
					System.out.println("文件夹目录：********" + child.getName());
					registerFileAnno(child, basePackage+"."+child.getName());
				}else {
					try {
						//加载给定目录下的class文件
						Class clazz = Class.forName(basePackage+"."+child.getName().substring(0, child.getName().lastIndexOf(".")));
						//获取Class添加的注解，判断是否存在指定的注解
						Annotation[] annotatedInterfaces = clazz.getDeclaredAnnotations();
						for (Annotation annotation : annotatedInterfaces) {
							String annoName = annotation.annotationType().getSimpleName();
							System.out.println("类声明的注解名称为：" + annoName);
							if (SCAN_ANNO_NAME.indexOf(annoName) != -1) {
								//通过class实例化对象
								Object obj = clazz.newInstance();
								//获取对象声明的属性
								Field[] declaredFields = clazz.getDeclaredFields();
								for (Field field : declaredFields) {
									Annotation[] fieldAnnotations = field.getDeclaredAnnotations();
									//遍历属性，判断属性上是否存在指定的注解
									for (Annotation fieldAnnotation : fieldAnnotations) {
										if ("CustomAutowire".equals(fieldAnnotation.annotationType().getSimpleName())) {
											//遍历map中的bean容器，将bean容器中对应的对象通过set方法注入
											for (String key : map.keySet()) {
												Class<?>[] interfaces = map.get(key).getClass().getInterfaces();
												for (Class intf : interfaces) {
													if (intf.getName().equals(field.getType().getName())) {
														field.setAccessible(true);
														field.set(obj, map.get(key));
													}
												}
											}
										}
									}
								}
								//将类名首字母小写作为beanName添加到bean容器中
								String firstChar = clazz.getInterfaces()[0].getSimpleName().substring(0, 1);
								String beanName = firstChar.toLowerCase() + clazz.getInterfaces()[0].getSimpleName().substring(1);
								map.put(beanName, obj);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException(e.getMessage());
					}
					System.out.println("具体文件：------" +child.getName());
				}
			}
		}
	}
	
	public static void main(String[] args) {
		CustomAnnotationConfigApplicationContext context = new CustomAnnotationConfigApplicationContext();
		context.scan("org.std.framework");
		CommonService commonService = (CommonService) context.getBean("commonService");
		commonService.commonInsert();
		commonService.commonQuery();
	}
}
