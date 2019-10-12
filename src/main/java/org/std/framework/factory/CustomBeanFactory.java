package org.std.framework.factory;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.std.framework.exceptiom.CustomException;

public class CustomBeanFactory {
	private Map<String, Object> map = new HashMap<String, Object>();
	
	public CustomBeanFactory(String path) {
		parseXml(path);
	}
	public Object getBean(String beanName) {
		return map.get(beanName);
	}
	
	/**
	 * 解析xml，封装bean对象
	 * @param xmlPath
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void parseXml(String xmlPath) {
		try {
			//dom4j解析XML文件
			SAXReader reader = new SAXReader();
			//获取资源文件，实际不需要字符串替换，此处是因为目录结构中存在空格，获取文件不识别%20
			String realPath = (this.getClass().getResource("/").getPath()+xmlPath).replace("%20", " ");
			Document document = reader.read(new File(realPath));
			//获取根结点
			Element rootElement = document.getRootElement();
			//通过类型进行自动装配default-autowire=byType
			Attribute attributeDefAutowire = rootElement.attribute("default-autowire");
			
			//获取跟节点下的各个bean节点
			Iterator<Element> elementIterator = rootElement.elementIterator();
			while (elementIterator.hasNext()) {
				//获取bean节点并解析bean节点下的id和class属性
				Element eleFirstLevelChild = elementIterator.next();
				Attribute attributeBeanName = eleFirstLevelChild.attribute("id");
				String beanName = attributeBeanName.getValue();
				Attribute attributeClazz = eleFirstLevelChild.attribute("class");
				String clazzName = attributeClazz.getValue();
				//根据class属性配置的全路径名加载类
				Class beanClazz = Class.forName(clazzName);
				
				//获取bean节点下的子节点（property和constructor-arg），存在说明需要进行属性注入
				Iterator<Element> eleFirstIterator = eleFirstLevelChild.elementIterator();
				Object beanObj = null; //bean标签下实例化的对象
				//构造方法注入方式下的参数的的Class 和 Object
				//TODO 构造方法下的参数的顺序性问题，目前只是简单的实现了构造方法中的参数类型和xml文件中的constructor-arg参数保持一致
				List<Class> consParamClass = new ArrayList<Class>(); 
				List<Object> consParamObj = new ArrayList<Object>();
				while (eleFirstIterator.hasNext()) {
					Element eleSecondLevelChild = eleFirstIterator.next();
					//set方法注入，直接通过默认构造方法实例化对象，然后将属性进行注入
					if ("property".equals(eleSecondLevelChild.getName())) {
						beanObj = beanClazz.newInstance();
						Attribute attributeName = eleSecondLevelChild.attribute("name");
						Attribute attributeRef = eleSecondLevelChild.attribute("ref");
						Field nameField = beanClazz.getDeclaredField(attributeName.getValue());
						nameField.setAccessible(true);
						nameField.set(beanObj, map.get(attributeRef.getValue()));
					}//构造方法注入，需要先拿到所有的构造方法中的参数，然后通过有参构造方法进行实例化对象
					else if ("constructor-arg".equals(eleSecondLevelChild.getName())) {
						Attribute attributeConstName = eleSecondLevelChild.attribute("name");
						Attribute attributeConstRef = eleSecondLevelChild.attribute("ref");
						Object refObj = map.get(attributeConstRef.getValue());
						consParamClass.add(beanClazz.getDeclaredField(attributeConstName.getValue()).getType());
						consParamObj.add(refObj);
					}
				}
				
				//构造方法参数不为空，说明是需要构造方法进行实例化对象，通过解析得到的参数获取有参构造方法并实例化
				if (consParamClass.size() > 0) {
					Constructor constructor = beanClazz.getConstructor(consParamClass.toArray(new Class[consParamClass.size()]));
					beanObj = constructor.newInstance(consParamObj.toArray());
				}
				
				//bean对象既没有配置属性注入也没有配置构造方法注入，判断是否配置了自动装配注入
				if (beanObj == null && attributeDefAutowire != null) {
					//通过类型自动装配注入，获取bean下的属性值，并遍历bean容器，判断是否存在相同类型的对象，存在则通过set方法注入
					if ("byType".equals(attributeDefAutowire.getValue())) {
						Field[] declaredFields = beanClazz.getDeclaredFields();
						for (Field field : declaredFields) {
							Class injectObjClazz = field.getType();
							
							int count = 0;
							Object injectObj = null;
							for (String key : map.keySet()) {
								for (Class intfClazz : map.get(key).getClass().getInterfaces()) {
									if (intfClazz.getName().equals(injectObjClazz.getName())) {
										count++;
										injectObj = map.get(key);
									}
								}
							}
							//存在多个相同类型的bean时，抛出异常
							if (count > 1) {
								throw new CustomException(beanClazz+"存在多个实现类");
							}
							beanObj = beanClazz.newInstance();
							field.setAccessible(true);
							field.set(beanObj, injectObj);
						}
					}
				}
				
				//没有属性的bean和有参构造方法的bean直接通过默认构造方法实例化
				if (beanObj == null) {
					beanObj = beanClazz.newInstance();
				}
				//将解析的bean对象存入map
				map.put(beanName, beanObj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(map);
	}
}
