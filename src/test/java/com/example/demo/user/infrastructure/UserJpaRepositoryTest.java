package com.example.demo.user.infrastructure;

import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Sql("/sql/user-repository-test-data.sql")
class UserJpaRepositoryTest {

    @Autowired
    private UserJpaRepository userJpaRepository;

//    @Test
    void UserRepository가_제대로_연결되었다() {
        // given
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("2jigoo@naver.com");
        userEntity.setAddress("Seoul");
        userEntity.setNickname("2jigoo");
        userEntity.setStatus(UserStatus.ACTIVE);
        userEntity.setCertificationCode("aaaaa-aaaaa-aaaaa-aaaaa");

        // when
        UserEntity savedUser = userJpaRepository.save(userEntity);

        // then
        assertThat(savedUser.getId()).isNotNull();
    }

    @Test
    void findByIdAndStatus로_유저_데이터를_조회한다() {
        // given
        // whdn
        Optional<UserEntity> result = userJpaRepository.findByIdAndStatus(1L, UserStatus.ACTIVE);

        // then
        assertThat(result.isPresent()).isTrue();
    }

    @Test
    void findByIdAndStatus는_데이터가_없으면_Optional_Empty를_반환한다() {
        // given
        // when
        Optional<UserEntity> result = userJpaRepository.findByIdAndStatus(1L, UserStatus.PENDING);

        // then
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    void findByEmailAndStatus로_유저_데이터를_조회한다() {
        // given
        // when
        Optional<UserEntity> result = userJpaRepository.findByEmailAndStatus("2jigoo@naver.com", UserStatus.ACTIVE);

        // then
        assertThat(result.isPresent()).isTrue();
    }

    @Test
    void findByEmailAndStatus는_데이터가_없으면_Optional_Empty를_반환한다() {
        // given
        // when
        Optional<UserEntity> result = userJpaRepository.findByEmailAndStatus("2jigoo@naver.com", UserStatus.PENDING);

        // then
        assertThat(result.isEmpty()).isTrue();
    }

}