package std1.aop;

import org.aspectj.lang.ProceedingJoinPoint;

import std1.annotation.AopAnnotation;

public class AopXml {

	public void before() {
		System.out.println("before");
	}
	
	public void after() {
		System.out.println("after");
	}
	
	public void around(ProceedingJoinPoint point, AopAnnotation anno) {
		System.out.println("around begin");
		System.out.println("anno value: " + anno.value());
		try {
			point.proceed();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		System.out.println("around end");
	}
	
	public void ret(Object retResult) {
		System.out.println("ret...");
		System.out.println("ret retResult:" + retResult);
	}
}
