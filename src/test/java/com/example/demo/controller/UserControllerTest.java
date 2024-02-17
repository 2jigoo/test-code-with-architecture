package com.example.demo.controller;

import com.example.demo.model.UserStatus;
import com.example.demo.model.dto.UserUpdateDto;
import com.example.demo.repository.UserEntity;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@SqlGroup({
        @Sql(value = "/sql/user-service-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 사용자는_특정_유저의_개인정보를_제외한_정보를_전달_받을_수_있다() throws Exception {
        // given
        // when
        // then

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("2jigoo@naver.com"))
                .andExpect(jsonPath("$.nickname").value("2jigoo"))
                .andExpect(jsonPath("$.address").doesNotExist())
                .andExpect(jsonPath("$.status").value(UserStatus.ACTIVE.getCode()));
    }

    @Test
    void 사용자는_존재하지_않는_유저의_정보를_요청하는_경우_404_응답을_받는다() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/api/users/100"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Users에서 ID 100를 찾을 수 없습니다."));
    }

    @Test
    void 사용자는_인증_코드로_계정을_활성화_할_수_있다() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/api/users/2/verify")
                        .queryParam("certificationCode", "aaaaa-aaaaa-aaaaa-aaaaa"))
                .andExpect(status().isFound());

        UserEntity userEntity = userRepository.findById(2L).get();
        assertThat(userEntity.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void 사용자는_인증_코드가_일치하지_않는_경우_계정을_활성화_할_수_없다() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/api/users/2/verify")
                        .queryParam("certificationCode", "aaaaa-aaaaa-aaaaa-aaaab"))
                .andExpect(status().isForbidden());
    }

    @Test
    void 사용자는_내_정보를_요청할_때_개인정보인_주소도_조회할_수_있다() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/api/users/me")
                        .header("EMAIL", "2jigoo@naver.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("2jigoo@naver.com"))
                .andExpect(jsonPath("$.nickname").value("2jigoo"))
                .andExpect(jsonPath("$.address").value("Seoul"))
                .andExpect(jsonPath("$.status").value(UserStatus.ACTIVE.getCode()));

        UserEntity userEntity = userRepository.findById(1L).get();
        assertThat(userEntity.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void 사용자는_내_정보를_수정할_수_있다() throws Exception {
        // given
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                        .nickname("2jigoo")
                        .address("Paju")
                        .build();
        // when
        // then
        mockMvc.perform(put("/api/users/me")
                        .header("EMAIL", "2jigoo@naver.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("2jigoo@naver.com"))
                .andExpect(jsonPath("$.nickname").value("2jigoo"))
                .andExpect(jsonPath("$.address").value("Paju"))
                .andExpect(jsonPath("$.status").value(UserStatus.ACTIVE.getCode()));
    }

}