package org.hpccsystems.eclBuilder.controller;

import org.hpccsystems.eclBuilder.HomeComposer;
import org.hpccsystems.eclBuilder.entity.User;
import org.hpccsystems.eclBuilder.service.AuthenticationService;
import org.hpccsystems.eclBuilder.service.ClusterConfigurationService;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class LoginController extends HomeComposer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Wire
	private Textbox userName;

	@Wire
	private Textbox password;
	
	@Wire
	private Label ErrorMsg;

	@Wire
	private Button loginBtn;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);

	}

	@Listen("onOK=#password; onClick=#loginBtn")
	public void onLogin() throws Exception {

		AuthenticationService authenticationService = ((AuthenticationService) SpringUtil
				.getBean("authenticationService"));

		User user;
		try {
			user = authenticationService.fetchUser(userName.getValue(), password.getValue());
			if (user != null) {
				Executions.sendRedirect("/eclBuilder/home.zul");
			} else {
				ErrorMsg.setValue("Invalid Credentials provided! Please check!");
				ErrorMsg.setVisible(true);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		
		ClusterConfigurationService clusterConfigurationService = (ClusterConfigurationService) SpringUtil
				.getBean("clusterConfigService");
		
		clusterConfigurationService.clusterConfigDetails(Labels.getLabel("eclBuilderPlatformIp"),
				Labels.getLabel("eclBuilderPlatformPort"), Labels.getLabel("eclBuilderPlatformUserName"),
				Labels.getLabel("eclBuilderPlatformPassWord"), Labels.getLabel("eclBuilderPlatformProtocol"));
		
	}
}
