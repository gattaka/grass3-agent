package org.myftp.gattserver.grass3.agent;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MeasureAspect {

	private static Logger logger = LoggerFactory.getLogger(MeasureAspect.class);

	public MeasureAspect() {
		logger.info("MeasureAspect created");
	}

	@Around("execution(* org.springframework.web.client.RestTemplate.getForObject(..))")
	public Object invoke(ProceedingJoinPoint joinPoint) throws Throwable {

		long start = System.currentTimeMillis();
		Object result = joinPoint.proceed();
		long end = System.currentTimeMillis();

		logger.info("TIME: " + (end - start) + " ms");

		return result;
	}

}
