package std1.proxy;

public class ProxyDaoImpl implements ProxyDao{

	@Override
	public String query() {
		System.out.println("query");
		return "ProxyDaoImpl query success!";
	}

	@Override
	public void insert(String id, int num) {
		System.out.println("insert id --> "+ id + ", num --> " + num);
	}

}
