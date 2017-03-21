package nexcore.scorpion.core.context.component;

public class BeanTestBean {

	public String testworld(){		
		String str = "call method wooga~~~~~~ ";
		System.out.println(str);
		return str;
	}

	public String helloworld(String word){
		String str = "hello world " + word;
		System.out.println(str);
		return str;
	}
}
