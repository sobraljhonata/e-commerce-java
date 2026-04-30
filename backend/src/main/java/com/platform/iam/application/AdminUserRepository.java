package com.platform.iam.application;

import com.platform.iam.domain.AdminUser;
import java.util.Optional;

public interface AdminUserRepository {

  Optional<AdminUser> findByEmail(String canonicalEmail);

  void save(AdminUser user);
}
