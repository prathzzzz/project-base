package com.example.serverapi.domain.auth.mapper;

import com.example.serverapi.domain.auth.dto.PermissionDTO;
import com.example.serverapi.domain.auth.dto.RoleDTO;
import com.example.serverapi.domain.auth.dto.UserDTO;
import com.example.serverapi.domain.auth.entity.Permission;
import com.example.serverapi.domain.auth.entity.Role;
import com.example.serverapi.domain.auth.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);

    @Mapping(target = "password", ignore = true)
    User toEntity(UserDTO userDTO);

    RoleDTO toRoleDTO(Role role);

    PermissionDTO toPermissionDTO(Permission permission);
}
