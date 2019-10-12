package std1.proxy;

import java.lang.reflect.Method;

public interface CustomInvocationHandler {
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
}
