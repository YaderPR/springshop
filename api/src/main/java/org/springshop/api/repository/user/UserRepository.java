package org.springshop.api.repository.user;

import org.springframework.stereotype.Repository;
import org.springshop.api.model.user.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    public Optional<User> findBySub(String sub);
}
