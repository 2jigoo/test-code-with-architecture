package com.example.demo.user.service;

import com.example.demo.mock.FakeMailSender;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class CertificationServiceTest {

    @Test
    void 이메일이_정해진_양식으로_발송되는지_확인한다() {
        // given
        FakeMailSender mailSender = new FakeMailSender();
        CertificationService certificationService = new CertificationService(mailSender);

        // when
        certificationService.send("2jigoo@naver.com", 1, "aaaaa-aaaaa-aaaaa-aaaaa");

        // then
        assertThat(mailSender.email).isEqualTo("2jigoo@naver.com");
        assertThat(mailSender.title).isEqualTo("Please certify your email address");
        assertThat(mailSender.content).isEqualTo("Please click the following link to certify your email address: "
                + "http://localhost:8080/api/users/1/verify?certificationCode=aaaaa-aaaaa-aaaaa-aaaaa");
    }

}
