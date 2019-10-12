package std1.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import std1.annotation.AopAnnotation;

@Component
@Aspect
public class Aop {
	
//	@Pointcut("execution(* std1.dao.*.*(..))")
	public void pointCut() {
		
	}

//	@Pointcut("@annotation(std1.annotation.AopAnnotation)")
	public void pointCutAnno() {
		
	}
	
//	@Before("pointCut() && pointCutAnno()")
	public void before() {
		System.out.println("before");
	}
	
//	@After("pointCutAnno()")
	public void after() {
		System.out.println("after");
	}
	
	@Around("@annotation(anno)")
	public void aroundAnno(ProceedingJoinPoint point, AopAnnotation anno) {
		System.out.println("anno around begin ...");
		System.out.println("anno around value: " + anno.value());
		try {
			point.proceed();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		System.out.println("anno around end ...");
	}
	
//	@Around("pointCutAnno()")
	public void around(ProceedingJoinPoint point) {
		System.out.println("around begin...");
		try {
			point.proceed();
			Class<? extends Object> targetClazz = point.getTarget().getClass();
			Method[] declaredMethods = targetClazz.getDeclaredMethods();
			for (Method method : declaredMethods) {
				AopAnnotation anno = method.getAnnotation(AopAnnotation.class);
				if (anno != null) {
					System.out.println(anno.value());
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		System.out.println("around end...");
	}
	
//	@Pointcut("target(std1.dao.AopDao)")
	public void pointCutTarget() {}
	
//	@Before("pointCutTarget()")
	public void beforeTarget() {
		System.out.println("target 增强 ");
	}
	
//	@Pointcut("@target(std1.annotation.AopAnnotation)")
	public void pointTargetAnno(){}
	
//	@Before("pointTargetAnno()")
	public void beforeTargetAnno() {
		System.out.println("@target 增强");
	}
	
//	@AfterReturning(pointcut="pointTargetAnno()", returning="retVal")
	public void afterReturningAnno(Object retVal) {
		System.out.println("------afterReturning------" + retVal);
	}
}
