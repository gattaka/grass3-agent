package org.myftp.gattserver.grass3.agent.medic;

import java.util.List;

import org.myftp.gattserver.grass3.medic.dto.ScheduledVisitDTO;

public interface IMedicBean {

	public List<ScheduledVisitDTO> getSchedules();

}
