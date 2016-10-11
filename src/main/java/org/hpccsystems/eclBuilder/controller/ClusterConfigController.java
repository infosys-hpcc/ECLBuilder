package org.hpccsystems.eclBuilder.controller;

import org.hpccsystems.eclBuilder.entity.ClusterConfig;
import org.hpccsystems.eclBuilder.service.ClusterConfigurationService;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class ClusterConfigController extends SelectorComposer<Component> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Wire
	private Textbox eclBuilderPlatformIp;
	
	@Wire
	private Textbox eclBuilderPlatformPort;
	
	@Wire
	private Textbox eclBuilderPlatformUserID;
	
	@Wire
	private Textbox eclBuilderPlatformPassWord;
	
	@Wire
	private Radiogroup isSSLEnabled;
	
	@Wire
	private Radio trueRadio;
	
	@Wire
	private Radio falseRadio;
	
	@Wire
	private Window clusterConfigWindow;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		ClusterConfigurationService clusterConfigurationService = (ClusterConfigurationService) SpringUtil
				.getBean("clusterConfigService");
		
		ClusterConfig obj = clusterConfigurationService.getCurrentConfigDetails();
		
		eclBuilderPlatformIp.setValue(obj.getIp());
		eclBuilderPlatformPassWord.setValue(obj.getPassword());
		eclBuilderPlatformPort.setValue(obj.getPort());
		eclBuilderPlatformUserID.setValue(obj.getUserName());

		if(obj.getProtocol().equals("http")){
			isSSLEnabled.setSelectedItem(falseRadio);
		}
		else if(obj.getProtocol().equals("https")){
			isSSLEnabled.setSelectedItem(trueRadio);
		}
	}
	
	@Listen("onClick=#submitButton")
	public void onClickSubmitPreferences(){
		
		ClusterConfigurationService clusterConfigurationService = (ClusterConfigurationService) SpringUtil
				.getBean("clusterConfigService");
		
		String protocol = "";
		
		if(isSSLEnabled.getSelectedItem().getLabel().equals("true"))
			protocol = "https";
		else
			protocol = "http";
		
		clusterConfigurationService.clusterConfigDetails(eclBuilderPlatformIp.getText(), eclBuilderPlatformPort.getText(), eclBuilderPlatformUserID.getText(), eclBuilderPlatformPassWord.getText(), protocol);
		
		clusterConfigWindow.detach();
	}
	
	@Listen("onClick=#cancelBtn")
	public void onCancel(){
		clusterConfigWindow.detach();
	}
}
