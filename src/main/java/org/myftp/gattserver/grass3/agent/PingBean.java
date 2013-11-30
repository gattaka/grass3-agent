package org.myftp.gattserver.grass3.agent;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

@Component
public class PingBean implements ApplicationContextAware {

	private RestOperations restTemplate;

	private ApplicationContext applicationContext;

	@PostConstruct
	private void init() {
		restTemplate = applicationContext.getBean(RestOperations.class);
	}

	public String ping() {
		return restTemplate.getForObject("http://gattserver.myftp.org/ws/agent/ping", String.class);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
