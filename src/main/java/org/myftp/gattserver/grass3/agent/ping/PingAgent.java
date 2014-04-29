package org.myftp.gattserver.grass3.agent.ping;

import org.myftp.gattserver.grass3.agent.TimedAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PingAgent extends TimedAgent<PingObserver, Long> {

	@Autowired
	private IPingBean pingBean;

	@Override
	protected Long checkEvent() {
		return pingBean.ping();
	}

}
