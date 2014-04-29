package org.myftp.gattserver.grass3.agent;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

@Aspect
@Component
public class LoggerAspect {

	private static Logger logger = LoggerFactory.getLogger(LoggerAspect.class);

	public LoggerAspect() {
		logger.info("MeasureAspect created");
	}

	@Around("execution(* org.springframework.web.client.RestTemplate.getForObject(..))")
	public Object getForObject(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			Object result = joinPoint.proceed();
			logger.info("REST getForObject result: " + result);
			return result;
		} catch (RestClientException e) {
			logger.warn("REST getForObject error: " + e.getMessage());
			throw e;
		}
	}

	@Around("execution(* org.myftp.gattserver.grass3.agent.ping.PingBean.ping(..))")
	public Object ping(ProceedingJoinPoint joinPoint) throws Throwable {

		Object result = joinPoint.proceed();
		logger.info("PING TIME: " + result + " ms");

		return result;
	}

}
