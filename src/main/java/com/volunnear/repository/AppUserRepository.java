package com.volunnear.repository;

import com.volunnear.entity.user.AppUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    @EntityGraph(attributePaths = {"roles"})
    Optional<AppUser> findAppUserByUsername(String username);

    @EntityGraph(attributePaths = {"roles"})
    Optional<AppUser> findAppUserByEmail(String email);
    @EntityGraph(attributePaths = {"roles"})
    Optional<AppUser> findAppUserById(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
