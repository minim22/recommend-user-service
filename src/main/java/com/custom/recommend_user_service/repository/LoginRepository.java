package com.custom.recommend_user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.custom.recommend_user_service.entity.User;

/**
 * 로그인 Repository
 */
@Repository
public interface LoginRepository extends JpaRepository<User, Long> {

    /**
     * 이메일에 해당되는 유저정보 조회
     * @param email 이메일
     * @return
     */
    User findUserByEmail(String email);

}
