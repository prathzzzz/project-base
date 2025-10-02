package com.eps.serverapi.feature.auth.mapper;

import com.eps.serverapi.feature.auth.dto.PermissionDTO;
import com.eps.serverapi.feature.auth.dto.RoleDTO;
import com.eps.serverapi.feature.auth.dto.UserDTO;
import com.eps.serverapi.feature.auth.entity.Permission;
import com.eps.serverapi.feature.auth.entity.Role;
import com.eps.serverapi.feature.auth.entity.User;
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
