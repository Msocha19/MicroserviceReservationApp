package pl.lodz.p.it.adapters.mapper;

import pl.lodz.p.it.domain.model.User;
import pl.lodz.p.it.model.users.UserEnt;

public interface Mapper {
    UserEnt mapDomainToEnt(User u, boolean useID);

    User mapEntToDomain(UserEnt u, boolean useID);
}
