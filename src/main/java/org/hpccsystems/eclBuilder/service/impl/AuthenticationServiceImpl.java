package org.hpccsystems.eclBuilder.service.impl;

import static org.apache.commons.lang3.StringUtils.isBlank;

import javax.servlet.ServletContext;

import org.hpccsystems.eclBuilder.Constants;
import org.hpccsystems.eclBuilder.dao.EClBuilderDao;
import org.hpccsystems.eclBuilder.entity.User;
import org.hpccsystems.eclBuilder.exceptions.AuthenticationException;
import org.hpccsystems.eclBuilder.exceptions.DatabaseException;
import org.hpccsystems.eclBuilder.service.AuthenticationService;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zkplus.spring.SpringUtil;

@Service("authenticationService")
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AuthenticationServiceImpl implements AuthenticationService {

    private Session getCurrentSession() {
        return Sessions.getCurrent();
    }
    
    @Override
    public User getCurrentUser() {
        return (User) getCurrentSession().getAttribute(Constants.USER);
    }

    @Override
    public void logout(Object object) {
        getCurrentSession().invalidate();
    }

    /**
     * Fetches the user for the specified account. In case of a the stub
     * authentication, returns a new User object populated with the user id and
     * password
     * 
     * @param account
     *            The id of the user
     * @param password
     *            The password
     * @param context
     *            Servlet context. If this is null, current application context
     *            is used
     * @return A User object
     * @throws AuthenticationException
     * @throws DatabaseException 
     * @throws Exception
     */
    @Override
    public User fetchUser(String userId, String password, ServletContext context) throws AuthenticationException {
        if (isBlank(userId)) {
            return null;
        }
        User user = null;
        
        try{
        
        user = ((EClBuilderDao) SpringUtil.getBean("EClBuilderDao")).fetchUser(userId, password);
        
        }catch(Exception e){
        	user = null;
        }
        
        if(user != null){
        	Sessions.getCurrent().setAttribute(Constants.USER, user);
        }
        
        return user;
    }

    @Override
    public User fetchUser(String account, String password) throws AuthenticationException {
        return fetchUser(account, password, null);
    }


}
