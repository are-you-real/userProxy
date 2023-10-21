package com.example.usersProxy.controller;

import com.example.usersProxy.response.userProxy.UserProxyResponse;
import com.example.usersProxy.service.UserProxyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserProxyController {

    private final UserProxyService userProxyService;

    @GetMapping("/{login}")
    UserProxyResponse findByLogin(@PathVariable String login) {
        return userProxyService.findByLogin(login);
    }
}
