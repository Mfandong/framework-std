package std1.proxy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class ProxyUtils {
	
	@SuppressWarnings({ "rawtypes", "unchecked", "resource" })
	public static Object newInstance(Object target) {
		Object proxy = null;
		Class targetInf = target.getClass().getInterfaces()[0];
		Method[] methods = targetInf.getMethods();
		String line = "\n";
		String tab = "\t";
		String infName = targetInf.getSimpleName();
		StringBuffer content = new StringBuffer();
		StringBuffer methodContent = new StringBuffer();
		String packageContent = "package com.std;" + line;
		String importContent = "import " + targetInf.getName() + ";" + line;
		String clazzFirstContent = "public class $Proxy implements " + infName + "{" + line;
		String filedContent = tab + "private " + infName + " target;" + line;
		String constuctorContent = tab + "public $Proxy (" + infName + " target){" + line
								 + tab + tab + "this.target = target;" + line
								 + tab + "}" + line;
		
		for (Method method : methods) {
			String returnTypeName = method.getReturnType().getSimpleName();
			String methodName = method.getName();
			Class[] args = method.getParameterTypes();
			StringBuffer argContent = new StringBuffer();
			StringBuffer paramsContent = new StringBuffer();
			int flag = 0;
			//拼接方法参数列表和请求目标对象方法的参数
			for (Class arg : args) {
				String temp = arg.getSimpleName();
				argContent.append(temp).append(" p").append(flag).append(", ");
				paramsContent.append("p").append(flag).append(", ");
				flag++;
			}
			//截取多余的逗号
			if (argContent.toString().length() > 0) {
				argContent = new StringBuffer(argContent.substring(0, argContent.lastIndexOf(",")));
				paramsContent = new StringBuffer(paramsContent.substring(0, paramsContent.lastIndexOf(",")));
			}
			methodContent.append(tab).append("public ").append(returnTypeName).append(" ").append(methodName).append("(").append(argContent).append("){").append(line)
						 .append(tab).append(tab).append("System.out.println(\"proxy\");").append(line);
				
			//返回值不为空，拼装返回值
			String returnValue = "";
			if (!"void".equals(returnTypeName)) {
				methodContent.append(tab).append(tab).append(returnTypeName).append(" returnValue = ");
				returnValue = tab + tab + "return returnValue;" + line;
			}
			methodContent.append(tab).append(tab).append("target.").append(methodName).append("(").append(paramsContent).append(");").append(line)
						 .append(returnValue)
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
			Constructor constructor = clazz.getConstructor(targetInf);
			proxy = constructor.newInstance(target);
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
		ProxyDao dao = (ProxyDao) ProxyUtils.newInstance(new ProxyDaoImpl());
		String query = dao.query();
		System.out.println(query);
		dao.insert("1001", 1);
	}
}
