package com.example.demo.user.service;

import com.example.demo.exception.CertificationCodeNotMatchedException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.domain.User;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;


@SpringBootTest
@ActiveProfiles("test")
@SqlGroup({
        @Sql(value = "/sql/user-service-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class UserServiceTest  {
    
    @Autowired
    private UserService userService;

    @MockBean
    private JavaMailSender mailSender;
    
    @Test
    void getByEmail은_ACTIVE_상태인_유저를_찾아올_수_있다() {
        // given
        String email = "2jigoo@naver.com";

        // when
        User result = userService.getByEmail(email);

        // then
        assertThat(result.getNickname()).isEqualTo("2jigoo");
    }

    @Test
    void getByEmail은_PENDING_상태인_유저를_찾아올_수_없다() {
        // given
        String email = "2jigoo2@naver.com";

        // when
        // then
        assertThatThrownBy(() -> {
            User result = userService.getByEmail(email);
        }).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getById은_ACTIVE_상태인_유저를_찾아올_수_있다() {
        // given
        // when
        User result = userService.getById(1L);

        // then
        assertThat(result.getNickname()).isEqualTo("2jigoo");
    }

    @Test
    void getById은_PENDING_상태인_유저를_찾아올_수_없다() {
        // given
        // when
        // then
        assertThatThrownBy(() -> {
            User result = userService.getById(2L);
        }).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void userCreateDto를_이용하여_유저를_생성할_수_있다() {
        // given
        UserCreate userCreate = UserCreate.builder()
                .email("2jigoo@naver.com")
                .address("Seoul")
                .nickname("2jigoo")
                .build();

        BDDMockito.doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // when
        User result = userService.create(userCreate);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
        // FIXME
//        assertThat(result.getCertificationCode()).isEqualTo("CANNOT");
    }

    @Test
    void userUpdateDto를_이용하여_유저를_수정할_수_있다() {
        // given
        UserUpdate userUpdate = UserUpdate.builder()
                .address("Seoul")
                .nickname("2bori")
                .build();

        BDDMockito.doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // when
        userService.update(1L, userUpdate);

        // then
        User savedUser = userService.getById(1L);
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getAddress()).isEqualTo("Seoul");
        assertThat(savedUser.getNickname()).isEqualTo("2bori");
    }

    @Test
    void user가_로그인_하면_마지막_로그인_시간이_변경된다() {
        // given
        // when
         userService.login(1L);

        // then
        User savedUser = userService.getById(1L);
        assertThat(savedUser.getLastLoginAt()).isGreaterThan(0L);
        // FIXME
//        assertThat(savedUser.getLastLoginAt()).isEqualTo("CANNOT");
    }

    @Test
    void PENDING_상태의_사용자는_인증_코드로_ACTIVE_할_수_있다() {
        // given
        // when
        userService.verifyEmail(2L, "aaaaa-aaaaa-aaaaa-aaaaa");

        // then
        User userEntity = userService.getById(2L);
        assertThat(userEntity.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void PENDING_상태의_사용자는_잘못된_인증_코드를_받으면_에러를_던진다() {
        // given
        // when
        // then
        assertThatThrownBy(() -> {
            userService.verifyEmail(2L, "aaaaa-aaaaa-aaaaa-aaaaab");
        }).isInstanceOf(CertificationCodeNotMatchedException.class);
    }

}