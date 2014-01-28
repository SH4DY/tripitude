package ac.tuwien.ase08.tripitude.service.interfaces;

import ac.tuwien.ase08.tripitude.entity.Role;

public interface IRoleService extends IGenericService<Role, Long> {

	Role getRoleByRole(String role);
}
