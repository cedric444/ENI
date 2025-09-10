package com.stage.pizzeria.service;

import com.stage.pizzeria.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {

    Role addRole(Role role);
    Role updateRole(Role role);
    void deleteRole(Role role);
    Optional<Role> getRoleById(int id);
    Optional<Role> getRoleByName(String name);
    List<Role> getAllRole();
}
