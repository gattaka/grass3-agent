package org.myftp.gattserver.grass3.agent.ping;

import org.myftp.gattserver.grass3.agent.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

@Component
public class PingBean implements IPingBean {

	@Autowired
	private RestOperations restTemplate;

	public Long ping() {
		long start = System.currentTimeMillis();
		restTemplate.getForObject("http://" + Configuration.SERVER + "/ws/agent/ping", String.class);
		long end = System.currentTimeMillis();
		return end - start;
	}
}
