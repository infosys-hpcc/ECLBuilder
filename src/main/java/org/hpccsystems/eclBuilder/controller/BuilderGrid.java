package org.hpccsystems.eclBuilder.controller;

import java.util.List;

import org.hpccsystems.eclBuilder.Constants;
import org.hpccsystems.eclBuilder.dao.EClBuilderDao;
import org.hpccsystems.eclBuilder.entity.User;
import org.hpccsystems.eclBuilder.service.AuthenticationService;
import org.zkoss.zkplus.spring.SpringUtil;


public class BuilderGrid {
	
	   private List<Builder> builders;

		public List<Builder> getBuilders(){
	    	try {
	    		
	    		String userID = ((User) ((AuthenticationService) SpringUtil.getBean(Constants.AUTHENTICATION_SERVICE))
	    				.getCurrentUser()).getId();

				builders = ((EClBuilderDao)SpringUtil.getBean("EClBuilderDao")).getECLBuilders(userID);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return builders;

		}
}
