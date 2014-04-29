package org.myftp.gattserver.grass3.agent.medic;

import java.util.Arrays;
import java.util.List;

import org.myftp.gattserver.grass3.agent.Configuration;
import org.myftp.gattserver.grass3.medic.dto.ScheduledVisitDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

@Component
public class MedicBean implements IMedicBean {

	@Autowired
	private RestOperations restTemplate;

	public List<ScheduledVisitDTO> getSchedules() {
		List<ScheduledVisitDTO> scheduledVisits = Arrays.asList(restTemplate.getForObject("http://"
				+ Configuration.SERVER + "/ws/medic/visit", ScheduledVisitDTO[].class));
		return scheduledVisits;
	}

}
