package com.kanke.gateway.util;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

import com.itranswarp.compiler.JavaStringCompiler;
import com.kanke.gateway.entry.Forward;

public class ServiceInstanceListSupplierUtil  {

	
	
	public static Class<?> getServiceInstanceListSupplierSimple(String name,List<Forward> forwards)  {
		
		String className = "ServiceInstanceListSupplier"+RandomStringUtils.randomAlphabetic(8)+System.currentTimeMillis()%10000;
		
		try {
			String source ="package com.kanke.gateway.util;\r\n"
					+ "\r\n"
					+ "import java.util.ArrayList;\r\n"
					+ "import java.util.List;\r\n"
					+ "\r\n"
					+ "import org.springframework.cloud.client.DefaultServiceInstance;\r\n"
					+ "import org.springframework.cloud.client.ServiceInstance;\r\n"
					+ "import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;\r\n"
					+ "\r\n"
					+ "import reactor.core.publisher.Flux;\r\n"
					+ "\r\n"
					+ "public class "+className+" implements ServiceInstanceListSupplier {\r\n"
					+ "\r\n"
					+ "	 @Override\r\n"
					+ "	  public Flux<List<ServiceInstance>> get() {\r\n"
					+ "		 List<ServiceInstance> list = new ArrayList<ServiceInstance>();\r\n";
					
					
					for(Forward forward:forwards) {
						source+="		 list.add(new DefaultServiceInstance(this.getServiceId() + list.size(), this.getServiceId(), \""+forward.getHost()+"\", "+forward.getPort()+", "+forward.isSecure()+"));\r\n";
					}
					
					source +="	    return Flux.just(list);\r\n"
					+ "	  }\r\n"
					+ "\r\n"
					+ "	@Override\r\n"
					+ "	public String getServiceId() {\r\n"
					+ "		return \""+name+"\";\r\n"
					+ "	}\r\n"
					+ "}";
			
			
			JavaStringCompiler compiler = new JavaStringCompiler();
			Map<String, byte[]> results = compiler.compile(className+".java",  source);
			return compiler.loadClass("com.kanke.gateway.util."+className, results);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
}
