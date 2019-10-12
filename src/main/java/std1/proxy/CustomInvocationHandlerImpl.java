package std1.proxy;

import java.lang.reflect.Method;

public class CustomInvocationHandlerImpl implements CustomInvocationHandler{
	private Object target;
	
	public CustomInvocationHandlerImpl(Object target) {
		this.target = target;
	}
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)  throws Throwable{
		System.out.println("-------------CustomInvocationHandlerImpl--------------");
		return method.invoke(target, args);
	}

}
