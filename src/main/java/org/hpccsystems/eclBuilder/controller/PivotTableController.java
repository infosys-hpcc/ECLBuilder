package org.hpccsystems.eclBuilder.controller;

import org.hpccsystems.eclBuilder.util.PasswordUtil;
import org.hpccsystems.ws.client.platform.Platform;
import org.json.JSONObject;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.util.Clients;


public class PivotTableController extends SelectorComposer<Component> implements EventListener<Event> {
	
	String wuID;
	
	String resultName;
	
	String hpccId;
	
	Platform platform;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		
		
		
		
		wuID = (String) Executions.getCurrent().getArg().get("wuId");
		
		resultName = (String) Executions.getCurrent().getArg().get("resultName");
		
		hpccId = (String) Executions.getCurrent().getArg().get("hpccId");
		
		platform = TreeController.getPlatformForCluster();
		
		
		JSONObject platformJson = new JSONObject();
		platformJson.put("ip", platform.getHPCCWSClient().getHost());
		platformJson.put("port", Integer.toString(platform.getHPCCWSClient().getPortInt()));
		platformJson.put("protocol", platform.getHPCCWSClient().getProtocol());
		platformJson.put("username", platform.getHPCCWSClient().getUserName() == null ? "" : platform.getHPCCWSClient().getUserName());
		platformJson.put("password", platform.getHPCCWSClient().getPassword() == null ? "" : PasswordUtil.getEncryptedPassword(platform.getHPCCWSClient().getPassword()));
		String platformJsonString = platformJson.toString();
		
//		Clients.evalJavaScript("loadContent2(\"" + wuID	+ "\", \""+ hpccId  + "\", \""+ resultName + "\", \""+ platformJsonString + "\")");

		Clients.evalJavaScript("loadContent2(\"" + wuID	+ "\", \""+ hpccId  + "\", \""+ resultName + "\", "+ platformJsonString + ")");
	}

	@Override
	public void onEvent(Event arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	
}


