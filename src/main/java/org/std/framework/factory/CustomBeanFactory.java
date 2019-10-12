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
	 * ����xml����װbean����
	 * @param xmlPath
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void parseXml(String xmlPath) {
		try {
			//dom4j����XML�ļ�
			SAXReader reader = new SAXReader();
			//��ȡ��Դ�ļ���ʵ�ʲ���Ҫ�ַ����滻���˴�����ΪĿ¼�ṹ�д��ڿո񣬻�ȡ�ļ���ʶ��%20
			String realPath = (this.getClass().getResource("/").getPath()+xmlPath).replace("%20", " ");
			Document document = reader.read(new File(realPath));
			//��ȡ�����
			Element rootElement = document.getRootElement();
			//ͨ�����ͽ����Զ�װ��default-autowire=byType
			Attribute attributeDefAutowire = rootElement.attribute("default-autowire");
			
			//��ȡ���ڵ��µĸ���bean�ڵ�
			Iterator<Element> elementIterator = rootElement.elementIterator();
			while (elementIterator.hasNext()) {
				//��ȡbean�ڵ㲢����bean�ڵ��µ�id��class����
				Element eleFirstLevelChild = elementIterator.next();
				Attribute attributeBeanName = eleFirstLevelChild.attribute("id");
				String beanName = attributeBeanName.getValue();
				Attribute attributeClazz = eleFirstLevelChild.attribute("class");
				String clazzName = attributeClazz.getValue();
				//����class�������õ�ȫ·����������
				Class beanClazz = Class.forName(clazzName);
				
				//��ȡbean�ڵ��µ��ӽڵ㣨property��constructor-arg��������˵����Ҫ��������ע��
				Iterator<Element> eleFirstIterator = eleFirstLevelChild.elementIterator();
				Object beanObj = null; //bean��ǩ��ʵ�����Ķ���
				//���췽��ע�뷽ʽ�µĲ����ĵ�Class �� Object
				//TODO ���췽���µĲ�����˳�������⣬Ŀǰֻ�Ǽ򵥵�ʵ���˹��췽���еĲ������ͺ�xml�ļ��е�constructor-arg��������һ��
				List<Class> consParamClass = new ArrayList<Class>(); 
				List<Object> consParamObj = new ArrayList<Object>();
				while (eleFirstIterator.hasNext()) {
					Element eleSecondLevelChild = eleFirstIterator.next();
					//set����ע�룬ֱ��ͨ��Ĭ�Ϲ��췽��ʵ��������Ȼ�����Խ���ע��
					if ("property".equals(eleSecondLevelChild.getName())) {
						beanObj = beanClazz.newInstance();
						Attribute attributeName = eleSecondLevelChild.attribute("name");
						Attribute attributeRef = eleSecondLevelChild.attribute("ref");
						Field nameField = beanClazz.getDeclaredField(attributeName.getValue());
						nameField.setAccessible(true);
						nameField.set(beanObj, map.get(attributeRef.getValue()));
					}//���췽��ע�룬��Ҫ���õ����еĹ��췽���еĲ�����Ȼ��ͨ���вι��췽������ʵ��������
					else if ("constructor-arg".equals(eleSecondLevelChild.getName())) {
						Attribute attributeConstName = eleSecondLevelChild.attribute("name");
						Attribute attributeConstRef = eleSecondLevelChild.attribute("ref");
						Object refObj = map.get(attributeConstRef.getValue());
						consParamClass.add(beanClazz.getDeclaredField(attributeConstName.getValue()).getType());
						consParamObj.add(refObj);
					}
				}
				
				//���췽��������Ϊ�գ�˵������Ҫ���췽������ʵ��������ͨ�������õ��Ĳ�����ȡ�вι��췽����ʵ����
				if (consParamClass.size() > 0) {
					Constructor constructor = beanClazz.getConstructor(consParamClass.toArray(new Class[consParamClass.size()]));
					beanObj = constructor.newInstance(consParamObj.toArray());
				}
				
				//bean�����û����������ע��Ҳû�����ù��췽��ע�룬�ж��Ƿ��������Զ�װ��ע��
				if (beanObj == null && attributeDefAutowire != null) {
					//ͨ�������Զ�װ��ע�룬��ȡbean�µ�����ֵ��������bean�������ж��Ƿ������ͬ���͵Ķ��󣬴�����ͨ��set����ע��
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
							//���ڶ����ͬ���͵�beanʱ���׳��쳣
							if (count > 1) {
								throw new CustomException(beanClazz+"���ڶ��ʵ����");
							}
							beanObj = beanClazz.newInstance();
							field.setAccessible(true);
							field.set(beanObj, injectObj);
						}
					}
				}
				
				//û�����Ե�bean���вι��췽����beanֱ��ͨ��Ĭ�Ϲ��췽��ʵ����
				if (beanObj == null) {
					beanObj = beanClazz.newInstance();
				}
				//��������bean�������map
				map.put(beanName, beanObj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(map);
	}
}
