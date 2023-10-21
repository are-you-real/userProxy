package com.example.usersProxy.response.userRequestsMetaData;

import lombok.Data;

@Data
public class UserRequestsMetaDataResponse {
    private Long id;
    private String login;
    private Long requestCount;
}
