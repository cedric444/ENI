package com.stage.pizzeria.service.impl;

import com.stage.pizzeria.model.Role;
import com.stage.pizzeria.repository.RoleRepository;
import com.stage.pizzeria.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    @BeforeEach
    void setup() {
        roleService = new RoleServiceImpl(roleRepository);
        roleRepository.deleteAll();
    }

    @Test
    void shouldAddARole() {
        Role role = new Role(3,"visitor");
        when(roleRepository.save(role)).thenReturn(role);
        Role result = roleService.addRole(role);

        assertNotNull(result);
        assertEquals(role.getId(), result.getId());
        assertEquals("visitor", result.getName());

    }

    @Test
    void shouldUpdateRole() {
        Role role = new Role(1, "admin");
        when(roleRepository.save(role)).thenReturn(role);
        String name = "user";
        role.setName(name);
        Role result = roleService.updateRole(role);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("user", role.getName());
    }

    @Test
    void shouldDeleteRole() {
        Role role = new Role(1, "admin");

        roleService.deleteRole(role);

        verify(roleRepository).delete(role);
    }

    @Test
    void shouldReturnAllRoles() {
        List<Role> roles = new ArrayList<>();
        Role role = new Role(1, "admin");
        Role role2 = new Role(2, "user");
        Role role3 = new Role(3, "visitor");
        roles.add(role);
        roles.add(role2);
        roles.add(role3);
        when(roleRepository.findAll()).thenReturn(roles);

        List<Role> result = roleService.getAllRole();

        assertEquals(3, result.size());
        assertEquals(role, result.get(0));
        assertEquals("user", result.get(1).getName());
        assertEquals(3, result.get(2).getId());
    }

    @Test
    void shouldReturnRoleById() {
        Role role = new Role(1, "admin");
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));

        Optional<Role> result = roleService.getRoleById(1);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        assertEquals("admin", result.get().getName());
    }

    @Test
    void shouldReturnRoleByName() {
        String name = "admin";
        Role role = new Role(1, name);
        when(roleRepository.findByName(name)).thenReturn(Optional.of(role));

        Optional<Role> result = roleService.getRoleByName(name);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        assertEquals("admin", result.get().getName());
    }
}