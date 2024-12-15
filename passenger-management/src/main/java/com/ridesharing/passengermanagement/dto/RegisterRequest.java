package com.ridesharing.passengermanagement.dto;

import lombok.Data;

@Data
public class RegisterRequest {
        private String username;
        private String password;
        public RegisterRequest(){}
        public RegisterRequest(String username, String password){
            this.username = username;
            this.password = password;
        }
}
