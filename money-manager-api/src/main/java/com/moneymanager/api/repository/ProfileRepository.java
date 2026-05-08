package com.moneymanager.api.repository;

import com.moneymanager.api.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {

    //select * from tbl_profiles where email = ?
    Optional<ProfileEntity> findByEmail(String email);

}
