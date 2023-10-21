package com.example.usersProxy.repository;

import com.example.usersProxy.entity.UserRequestsMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRequestsMetaDataRepository extends JpaRepository<UserRequestsMetaData, Long> {
    UserRequestsMetaData findByLogin(String login);
}
