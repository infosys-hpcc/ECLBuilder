package org.hpccsystems.eclBuilder.service;

import javax.servlet.ServletContext;

import org.hpccsystems.eclBuilder.entity.User;
import org.hpccsystems.eclBuilder.exceptions.AuthenticationException;


public interface AuthenticationService {

    static final String URL = "redirectURL";
    static final String PARAMS = "redirectParams";
    
    User getCurrentUser();

    void logout(Object object);

    User fetchUser(String account, String password) throws AuthenticationException;
    
    User fetchUser(String account, String password, ServletContext servletContext) throws AuthenticationException;
    
}
