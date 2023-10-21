package com.example.usersProxy.controller;

import com.example.usersProxy.exception.UserInternalServerException;
import com.example.usersProxy.exception.UserNotFoundException;
import com.example.usersProxy.response.userProxy.UserProxyResponse;
import com.example.usersProxy.service.UserProxyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserProxyController.class)
class UserProxyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserProxyService userProxyService;

    @Test
    void findByLogin() throws Exception {

        when(userProxyService.findByLogin("octocat")).thenReturn(getUserProxyResponse());

        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/octocat")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.login").value("octocat"))
                .andExpect(jsonPath("$.calculations").value(BigDecimal.valueOf(8)));
    }

    @Test
    void findByLogin_notFound() throws Exception {

        when(userProxyService.findByLogin("NotExist")).thenThrow(new UserNotFoundException("ErrorBody"));

        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/NotExist")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User does not exist"));
    }

    @Test
    void findByLogin_InternalError() throws Exception {

        when(userProxyService.findByLogin("octocat")).thenThrow(new UserInternalServerException("ErrorBody"));

        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/octocat")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.message").value("Internal server error"));
    }

    private UserProxyResponse getUserProxyResponse() {
        UserProxyResponse userProxyResponse = new UserProxyResponse();
        userProxyResponse.setId(1L);
        userProxyResponse.setLogin("octocat");
        userProxyResponse.setCalculations(BigDecimal.valueOf(8));

        return userProxyResponse;
    }
}