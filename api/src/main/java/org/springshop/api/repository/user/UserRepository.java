package org.springshop.api.repository.user;

import org.springframework.stereotype.Repository;
import org.springshop.api.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    public User findBySub(String sub);
}
