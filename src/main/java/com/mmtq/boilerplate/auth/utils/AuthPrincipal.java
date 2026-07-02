package com.mmtq.boilerplate.auth.utils;

import com.mmtq.boilerplate.auth.models.Session;
import com.mmtq.boilerplate.auth.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthPrincipal {

    private User user;
    private Session session;

}
