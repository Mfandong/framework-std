package std1.proxy;

import std1.proxy.CustomInvocationHandler;
import java.lang.reflect.Method;
public class $Proxy implements ProxyDao{
	private CustomInvocationHandler h;
	public $Proxy (CustomInvocationHandler h){
		this.h = h;
	}
	public void insert(String p0, int p1){
		try{
			Method m = this.getClass().getDeclaredMethod("insert", String.class, int.class);
			h.invoke(this, m, new Object[]{p0, p1});
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	public String query(){
		try{
			Method m = this.getClass().getDeclaredMethod("query");
			return (String)h.invoke(this, m, null);
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}
}