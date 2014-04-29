package org.myftp.gattserver.grass3.agent.medic;

import java.util.List;

import org.myftp.gattserver.grass3.agent.Observer;
import org.myftp.gattserver.grass3.medic.dto.ScheduledVisitDTO;

public interface MedicObserver extends Observer<List<ScheduledVisitDTO>> {
}