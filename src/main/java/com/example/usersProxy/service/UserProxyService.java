package com.example.usersProxy.service;

import com.example.usersProxy.entity.UserRequestsMetaData;
import com.example.usersProxy.repository.UserRequestsMetaDataRepository;
import com.example.usersProxy.response.userProxy.UserProxyResponse;
import com.example.usersProxy.response.user.UserResponse;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.concurrent.BlockingQueue;

import static java.math.BigDecimal.valueOf;


@Service
@AllArgsConstructor
@Transactional
public class UserProxyService {

    private final WebClient webClient;

    private final BlockingQueue<String> loginBlockingQueue;

    private final UserRequestsMetaDataRepository userRequestsMetaDataRepository;

    private final ModelMapper modelMapper;

    public UserProxyResponse findByLogin(String login) {

        UserResponse userResponse = getUserByLoginFromWebService(webClient, login);

        UserProxyResponse userProxyResponse = modelMapper.map(userResponse, UserProxyResponse.class);

        userProxyResponse.setCalculations(doCalculation(userResponse.getFollowers(),userResponse.getPublicRepos()));

        // Increment RequestCount
        submitLoginBlockingQueue(login);

        return userProxyResponse;
    }

    private UserResponse getUserByLoginFromWebService(WebClient webClient, String login) {
        return webClient.get().uri("/" + login)
                .retrieve().bodyToMono(UserResponse.class).block();
    }

    BigDecimal doCalculation(Integer followers, Integer publicRepos) {
        MathContext precision = new MathContext(20);

        // Dividing by 0 is forbidden
        if(followers == 0) {
            return null;
        }
        BigDecimal component1 = valueOf(6).divide(valueOf(followers), precision);
        BigDecimal component2 = valueOf(2).add(valueOf(publicRepos));
        return component1.multiply(component2).setScale(10,RoundingMode.HALF_UP);
    }

    @PostConstruct
    private void initiateThread(){
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    if (!loginBlockingQueue.isEmpty()) {
                        String login = loginBlockingQueue.take();
                        UserRequestsMetaData result = incrementRequestCount(login);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    initiateThread();
                }
            }
        });
        thread.setName("IncrementRequestCountThread");
        thread.start();
    }

    public void submitLoginBlockingQueue(String login) {
        loginBlockingQueue.add(login);
    }

    private UserRequestsMetaData incrementRequestCount(String login) {
        UserRequestsMetaData userRequestsMetaData = userRequestsMetaDataRepository.findByLogin(login);
        if (userRequestsMetaData != null) {
            userRequestsMetaData.setRequestCount(userRequestsMetaData.getRequestCount() + 1);
            return userRequestsMetaDataRepository.save(userRequestsMetaData);
        }  else {
            UserRequestsMetaData newUserRequestsMetaData = new UserRequestsMetaData();
            newUserRequestsMetaData.setRequestCount(1L);
            newUserRequestsMetaData.setLogin(login);
            return userRequestsMetaDataRepository.save(newUserRequestsMetaData);
        }
    }

}
