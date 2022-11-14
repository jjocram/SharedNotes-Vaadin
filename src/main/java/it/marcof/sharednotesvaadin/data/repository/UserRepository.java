package it.marcof.sharednotesvaadin.data.repository;

import it.marcof.sharednotesvaadin.data.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByUsername(String username);
}
