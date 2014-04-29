package org.myftp.gattserver.grass3.agent.medic;

import java.util.List;

import org.myftp.gattserver.grass3.agent.TimedAgent;
import org.myftp.gattserver.grass3.medic.dto.ScheduledVisitDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MedicAgent extends TimedAgent<MedicObserver, List<ScheduledVisitDTO>> {

	@Autowired
	private IMedicBean medicBean;

	@Override
	protected List<ScheduledVisitDTO> checkEvent() {
		return medicBean.getSchedules();
	}

}
