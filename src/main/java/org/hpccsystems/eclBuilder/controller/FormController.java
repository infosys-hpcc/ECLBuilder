package org.hpccsystems.eclBuilder.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hpccsystems.eclBuilder.util.PasswordUtil;
import org.hpccsystems.ws.client.HPCCWsClient;
import org.hpccsystems.ws.client.platform.Platform;
import org.json.CDL;
import org.json.JSONArray;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zkoss.json.JSONObject;

/**
 * Controller to handle forms
 * 
 */
@Controller
@RequestMapping("*.do")
public class FormController {
	private static final Logger LOGGER = LoggerFactory.getLogger(FormController.class);
	private static final String STATUS = "status";

	@RequestMapping(value = "/jsonECLRun.do")
	public void getJSONdata(HttpServletRequest request, HttpServletResponse response) {

		try {

			String wuId = request.getParameter("wuId");

			String resultName = request.getParameter("resultName");

			String hpccID = request.getParameter("hpccId");

//			String platformJsonString = request.getParameter("platformJsonString[protocol]");
			
			
			Platform platform = Platform.get(request.getParameter("platformJsonString[protocol]"),
					request.getParameter("platformJsonString[ip]"),
					Integer.parseInt(request.getParameter("platformJsonString[port]")),
					request.getParameter("platformJsonString[username]"), PasswordUtil.decrypt(request.getParameter("platformJsonString[password]")));
			
			HPCCWsClient connector = platform.getWsClient();
	        
	        JSONArray jsonArr = new JSONArray();
	        
	        org.json.JSONObject resJsonRs = new org.json.JSONObject();
	        
	        for (int i=0 ; i< connector.getWsWorkunitsClient().getWUInfo(wuId).getResultCount(); i++) {
	            
	            if(resultName.equals(connector.getWsWorkunitsClient().getWUInfo(wuId).getResults()[i].getName())){
	                String wuresults = connector.getWsWorkunitsClient().fetchResults(wuId, i, connector.getAvailableClusterGroups()[1], true, 0, 1000);
	                resJsonRs = XML.toJSONObject(wuresults);
//	                jsonArrO = (JSONArray) ((JSONObject) resJsonRs.get("Dataset")).get("Row");
	                break;
	            }
	        }    
	            
	        	jsonArr = (JSONArray)(((org.json.JSONObject) resJsonRs.get("Dataset")).get("Row"));
	            String content = CDL.toString(jsonArr);


			JSONObject responseJSON = new JSONObject();

			responseJSON.put(STATUS, "success");

			responseJSON.put("formHtml", jsonArr.toString());

			response.getWriter().write(responseJSON.toString());

		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}

	}
}
