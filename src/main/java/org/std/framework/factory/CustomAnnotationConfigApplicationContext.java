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
	 * ����ָ��Ŀ¼�µ�class�ļ�������ָ��ע��ģ�ʵ����bean����������
	 * @param file
	 * @param basePackage
	 */
	@SuppressWarnings("rawtypes")
	private void registerFileAnno(File file, String basePackage) {
		File[] children = file.listFiles();
		if (children != null) {
			for (File child : children) {
				//�ж��ļ��Ƿ�Ŀ¼����Ŀ¼�ĵݹ����
				if (child.isDirectory()) {
					System.out.println("�ļ���Ŀ¼��********" + child.getName());
					registerFileAnno(child, basePackage+"."+child.getName());
				}else {
					try {
						//���ظ���Ŀ¼�µ�class�ļ�
						Class clazz = Class.forName(basePackage+"."+child.getName().substring(0, child.getName().lastIndexOf(".")));
						//��ȡClass��ӵ�ע�⣬�ж��Ƿ����ָ����ע��
						Annotation[] annotatedInterfaces = clazz.getDeclaredAnnotations();
						for (Annotation annotation : annotatedInterfaces) {
							String annoName = annotation.annotationType().getSimpleName();
							System.out.println("��������ע������Ϊ��" + annoName);
							if (SCAN_ANNO_NAME.indexOf(annoName) != -1) {
								//ͨ��classʵ��������
								Object obj = clazz.newInstance();
								//��ȡ��������������
								Field[] declaredFields = clazz.getDeclaredFields();
								for (Field field : declaredFields) {
									Annotation[] fieldAnnotations = field.getDeclaredAnnotations();
									//�������ԣ��ж��������Ƿ����ָ����ע��
									for (Annotation fieldAnnotation : fieldAnnotations) {
										if ("CustomAutowire".equals(fieldAnnotation.annotationType().getSimpleName())) {
											//����map�е�bean��������bean�����ж�Ӧ�Ķ���ͨ��set����ע��
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
								//����������ĸСд��ΪbeanName��ӵ�bean������
								String firstChar = clazz.getInterfaces()[0].getSimpleName().substring(0, 1);
								String beanName = firstChar.toLowerCase() + clazz.getInterfaces()[0].getSimpleName().substring(1);
								map.put(beanName, obj);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException(e.getMessage());
					}
					System.out.println("�����ļ���------" +child.getName());
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
