package std1.proxy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.springframework.util.StringUtils;

public class ProxyInvocationUtils {
	
	@SuppressWarnings({ "rawtypes", "unchecked", "resource" })
	public static Object newInstance(Class[] targetInfs, CustomInvocationHandler h) {
		Object proxy = null;
		Class targetInf = targetInfs[0]; //Ŀǰֻ��ȡĿ�����Ľӿ��б��е�һ��
		Method[] methods = targetInf.getMethods(); //ͨ���ӿ�����ȡ��������ķ������õ�Object�еķ���
		String line = "\n";
		String tab = "\t";
		String infName = targetInf.getSimpleName(); //�ӿ�����
		StringBuffer content = new StringBuffer();
		StringBuffer methodContent = new StringBuffer(); //��������
		String packageContent = "package com.std;" + line; //��
		//����
		String importContent = "import std1.proxy.CustomInvocationHandler;" + line
							 + "import java.lang.reflect.Method;" + line
							 + "import std1.proxy.ProxyDao;" + line;
		//�����ʼ��һ��
		String clazzFirstContent = "public class $Proxy implements " + infName + "{" + line;
		//����
		String filedContent = tab + "private CustomInvocationHandler h;" + line;
		//���췽��
		String constuctorContent = tab + "public $Proxy (CustomInvocationHandler h){" + line
								 + tab + tab + "this.h = h;" + line
								 + tab + "}" + line;
		
		for (Method method : methods) {
			//�����ķŻ�����
			String returnTypeName = method.getReturnType().getSimpleName();
			String methodName = method.getName(); //��������
			Class[] args = method.getParameterTypes(); //�����б�
			StringBuffer argContent = new StringBuffer(); //�������Ĳ����б�ƴ������
			StringBuffer paramsContent = new StringBuffer(); //�����б�ƴ�����ݣ����ڵ���Ŀ�귽������
			StringBuffer paramsClass = new StringBuffer(); //�����б��������ƴ�����ݣ����ڻ�ȡMethod����Ĵ��ݲ����б�
			int flag = 0;
			//ƴ�ӷ��������б������Ŀ����󷽷��Ĳ���
			for (Class arg : args) {
				String temp = arg.getSimpleName();
				argContent.append(temp).append(" p").append(flag).append(", ");
				paramsContent.append(", ").append("p").append(flag);
				paramsClass.append(", ").append(temp).append(".class");
				flag++;
			}
			//��ȡ����Ķ���
			if (argContent.toString().length() > 0) {
				argContent = new StringBuffer(argContent.substring(0, argContent.lastIndexOf(",")));
				//Ŀ�������װ��Object��������
				paramsContent.insert(2, "new Object[]{").append("}");
			}
			
			//�����б�����ʱ�����ݵĲ���Ϊnull
			paramsContent = StringUtils.isEmpty(paramsContent.toString()) ? paramsContent.append(", null") : paramsContent;
			
			methodContent.append(tab).append("public ").append(returnTypeName).append(" ").append(methodName).append("(").append(argContent).append("){").append(line)
						 .append(tab).append(tab).append("try{").append(line)
						 .append(tab).append(tab).append(tab).append("Method m = Class.forName(\"").append(targetInf.getName()).append("\").getDeclaredMethod(\"").append(methodName).append("\"").append(paramsClass).append(");").append(line);
				
			//����ֵ��Ϊ�գ�ƴװ����ֵ
			String returnValue = "";
			String cacheReturnValue = ""; //cache�ڲ��ķ���null
			if (!"void".equals(returnTypeName)) {
				returnValue = tab + tab + tab + "return ("+returnTypeName+")";
				cacheReturnValue = tab + tab + tab + "return null;" + line;
			}else {
				returnValue = tab + tab + tab;
			}
			methodContent.append(returnValue).append("h.invoke(this, m").append(paramsContent).append(");").append(line);
			methodContent.append(tab).append(tab).append("} catch (Throwable e) {").append(line)
						 .append(tab).append(tab).append(tab).append("e.printStackTrace();").append(line)
						 //ƴ��try -- cache�ڲ��ķ���ֵ
						 .append(cacheReturnValue)
						 .append(tab).append(tab).append("}").append(line)
						 .append(tab).append("}").append(line);
		}
		
		//��װjava�ļ���
		content.append(packageContent).append(importContent).append(clazzFirstContent).append(filedContent).append(constuctorContent)
			   .append(methodContent).append("}");
		
		FileWriter writer = null;
		try {
			//��java�ļ����������
			File file = new File("D:\\com\\std\\$Proxy.java");
			if (!file.exists()) {
                file.createNewFile();
            }
			
			writer = new FileWriter(file);
			writer.write(content.toString());
			writer.flush();
			
			//����java�ļ���class�ļ�
			JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
			StandardJavaFileManager fileMgr = javaCompiler.getStandardFileManager(null, null, null);
			Iterable javaFileObjects = fileMgr.getJavaFileObjects(file);
			JavaCompiler.CompilationTask task = javaCompiler.getTask(null, fileMgr, null, null, null, javaFileObjects);
			task.call();
			fileMgr.close();
			
			//���������ɵ�class�ļ����ص�������
			URL[] urls = new URL[]{new URL("file:D:\\\\")};
			URLClassLoader classLoader = new URLClassLoader(urls);
			Class clazz = classLoader.loadClass("com.std.$Proxy");
			Constructor constructor = clazz.getConstructor(CustomInvocationHandler.class);
			proxy = constructor.newInstance(h);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return proxy;
	}
	
	public static void main(String[] args) {
		ProxyDao target = new ProxyDaoImpl();
//		ProxyDao dao = (ProxyDao) ProxyInvocationUtils.newInstance(target.getClass().getInterfaces(), new CustomInvocationHandlerImpl(target));
//		String query = dao.query();
//		System.out.println(query);
//		dao.insert("1001", 1);
		
		ProxyDao proxy = (ProxyDao) Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), new InvocationHandlerImpl(target));
		proxy.query();
	}
}
