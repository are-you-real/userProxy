package com.example.usersProxy.service;

import com.example.usersProxy.repository.UserRequestsMetaDataRepository;
import com.example.usersProxy.response.userProxy.UserProxyResponse;
import com.example.usersProxy.response.user.UserResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProxyServiceTest {

    @Spy
    public WebClient webClient = WebClient.builder().baseUrl(mockBackEnd.url("").url().toString()).build();

    @Mock
    private BlockingQueue<String> loginBlockingQueue;

    @Mock
    private UserRequestsMetaDataRepository userRequestsMetaDataRepository;

    @Mock
    private ModelMapper modelMapper;

    @Autowired
    @InjectMocks
    private UserProxyService userProxyService;

    public static MockWebServer mockBackEnd;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    void findByLogin_should_return_userResponse() throws JsonProcessingException {
        //given
        UserResponse userResponse = getUserResponse();
        UserProxyResponse userProxyResponseExpected = getUserProxyResponse();
        String login = "octocat";

        //when
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(userResponse))
                .addHeader("Content-Type", "application/json"));
        when(modelMapper.map(userResponse, UserProxyResponse.class))
                .thenReturn(getUserProxyResponse());
        UserProxyResponse userProxyResponseActual = userProxyService.findByLogin(login);

        //then
        assertEquals(userProxyResponseExpected, userProxyResponseActual);
        verify(modelMapper,times(1)).map(any(),any());
    }

    @Test
    void doCalculation_should_return_expected_results() {

        //when
        BigDecimal result1 = userProxyService.doCalculation(6, 6);
        BigDecimal result2 = userProxyService.doCalculation(124, 1);
        BigDecimal result3 = userProxyService.doCalculation(22, 0);

        //then
        assertEquals(BigDecimal.valueOf(8).setScale(10, RoundingMode.HALF_UP), result1);
        assertEquals(BigDecimal.valueOf(0.1451612903).setScale(10, RoundingMode.HALF_UP), result2);
        assertEquals(BigDecimal.valueOf(0.5454545455).setScale(10, RoundingMode.HALF_UP), result3);

    }


    private UserResponse getUserResponse() {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setLogin("octocat");
        userResponse.setFollowers(6);
        userResponse.setPublicRepos(6);
        return userResponse;
    }

    private UserProxyResponse getUserProxyResponse() {
        UserProxyResponse userProxyResponse = new UserProxyResponse();
        userProxyResponse.setId(1L);
        userProxyResponse.setLogin("octocat");
        userProxyResponse.setCalculations(BigDecimal.valueOf(8).setScale(10, RoundingMode.HALF_UP));

        return userProxyResponse;
    }
}