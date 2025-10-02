package com.eps.serverapi.feature.auth.repository;

import com.eps.serverapi.feature.auth.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByName(String name);

    Boolean existsByName(String name);
}
