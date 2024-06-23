package com.spkd.tinycrm.tinyos.repository;

import com.spkd.tinycrm.tinyos.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}