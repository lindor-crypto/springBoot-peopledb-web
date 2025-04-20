
package com.neutrinosys.peopledbweb.data;

import com.neutrinosys.peopledbweb.biz.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
