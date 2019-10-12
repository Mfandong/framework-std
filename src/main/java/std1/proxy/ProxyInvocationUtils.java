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
		Class targetInf = targetInfs[0]; //目前只获取目标对象的接口列表中的一个
		Method[] methods = targetInf.getMethods(); //通过接口来获取方法，类的方法会拿到Object中的方法
		String line = "\n";
		String tab = "\t";
		String infName = targetInf.getSimpleName(); //接口名称
		StringBuffer content = new StringBuffer();
		StringBuffer methodContent = new StringBuffer(); //方法内容
		String packageContent = "package com.std;" + line; //包
		//导包
		String importContent = "import std1.proxy.CustomInvocationHandler;" + line
							 + "import java.lang.reflect.Method;" + line
							 + "import std1.proxy.ProxyDao;" + line;
		//类的起始第一行
		String clazzFirstContent = "public class $Proxy implements " + infName + "{" + line;
		//属性
		String filedContent = tab + "private CustomInvocationHandler h;" + line;
		//构造方法
		String constuctorContent = tab + "public $Proxy (CustomInvocationHandler h){" + line
								 + tab + tab + "this.h = h;" + line
								 + tab + "}" + line;
		
		for (Method method : methods) {
			//方法的放回类型
			String returnTypeName = method.getReturnType().getSimpleName();
			String methodName = method.getName(); //方法名称
			Class[] args = method.getParameterTypes(); //参数列表
			StringBuffer argContent = new StringBuffer(); //代理方法的参数列表拼接内容
			StringBuffer paramsContent = new StringBuffer(); //参数列表拼接内容，用于调用目标方法传参
			StringBuffer paramsClass = new StringBuffer(); //参数列表各个类型拼接内容，用于获取Method对象的传递参数列表
			int flag = 0;
			//拼接方法参数列表和请求目标对象方法的参数
			for (Class arg : args) {
				String temp = arg.getSimpleName();
				argContent.append(temp).append(" p").append(flag).append(", ");
				paramsContent.append(", ").append("p").append(flag);
				paramsClass.append(", ").append(temp).append(".class");
				flag++;
			}
			//截取多余的逗号
			if (argContent.toString().length() > 0) {
				argContent = new StringBuffer(argContent.substring(0, argContent.lastIndexOf(",")));
				//目标参数组装成Object对象数组
				paramsContent.insert(2, "new Object[]{").append("}");
			}
			
			//参数列表不存在时，传递的参数为null
			paramsContent = StringUtils.isEmpty(paramsContent.toString()) ? paramsContent.append(", null") : paramsContent;
			
			methodContent.append(tab).append("public ").append(returnTypeName).append(" ").append(methodName).append("(").append(argContent).append("){").append(line)
						 .append(tab).append(tab).append("try{").append(line)
						 .append(tab).append(tab).append(tab).append("Method m = Class.forName(\"").append(targetInf.getName()).append("\").getDeclaredMethod(\"").append(methodName).append("\"").append(paramsClass).append(");").append(line);
				
			//返回值不为空，拼装返回值
			String returnValue = "";
			String cacheReturnValue = ""; //cache内部的返回null
			if (!"void".equals(returnTypeName)) {
				returnValue = tab + tab + tab + "return ("+returnTypeName+")";
				cacheReturnValue = tab + tab + tab + "return null;" + line;
			}else {
				returnValue = tab + tab + tab;
			}
			methodContent.append(returnValue).append("h.invoke(this, m").append(paramsContent).append(");").append(line);
			methodContent.append(tab).append(tab).append("} catch (Throwable e) {").append(line)
						 .append(tab).append(tab).append(tab).append("e.printStackTrace();").append(line)
						 //拼接try -- cache内部的返回值
						 .append(cacheReturnValue)
						 .append(tab).append(tab).append("}").append(line)
						 .append(tab).append("}").append(line);
		}
		
		//组装java文件串
		content.append(packageContent).append(importContent).append(clazzFirstContent).append(filedContent).append(constuctorContent)
			   .append(methodContent).append("}");
		
		FileWriter writer = null;
		try {
			//将java文件输出到磁盘
			File file = new File("D:\\com\\std\\$Proxy.java");
			if (!file.exists()) {
                file.createNewFile();
            }
			
			writer = new FileWriter(file);
			writer.write(content.toString());
			writer.flush();
			
			//编译java文件城class文件
			JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
			StandardJavaFileManager fileMgr = javaCompiler.getStandardFileManager(null, null, null);
			Iterable javaFileObjects = fileMgr.getJavaFileObjects(file);
			JavaCompiler.CompilationTask task = javaCompiler.getTask(null, fileMgr, null, null, null, javaFileObjects);
			task.call();
			fileMgr.close();
			
			//将代理生成的class文件加载到工程中
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
