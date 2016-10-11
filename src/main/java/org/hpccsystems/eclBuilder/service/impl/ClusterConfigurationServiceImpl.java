package org.hpccsystems.eclBuilder.service.impl;

import org.hpccsystems.eclBuilder.entity.ClusterConfig;
import org.hpccsystems.eclBuilder.service.ClusterConfigurationService;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

@Service("clusterConfigService")
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ClusterConfigurationServiceImpl implements ClusterConfigurationService {

	@Override
	public ClusterConfig getCurrentConfigDetails() {
		// TODO Auto-generated method stub
		return (ClusterConfig) getCurrentSession().getAttribute("clusterConfigPreferences");
	}

	@Override
	public ClusterConfig clusterConfigDetails(String ip, String port, String username, String password,
			String protocol) {
		// TODO Auto-generated method stub
		ClusterConfig clusterConfig = new ClusterConfig(ip, port, username, password, protocol);
		
		getCurrentSession().setAttribute("clusterConfigPreferences", clusterConfig);
		
		return clusterConfig;
	}
	
	private Session getCurrentSession() {
        return Sessions.getCurrent();
    }

}
