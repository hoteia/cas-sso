package org.hoteia.cas.dao;

import org.hoteia.cas.model.UserAuthenticationInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAuthenticationInfoRepository extends JpaRepository<UserAuthenticationInfo, Long> {

    UserAuthenticationInfo findByUsername(String username);
}
