package org.hpccsystems.eclBuilder.service;

import org.hpccsystems.eclBuilder.entity.ClusterConfig;

public interface ClusterConfigurationService {

    
    ClusterConfig getCurrentConfigDetails();

    ClusterConfig clusterConfigDetails(String ip,String port,String username,String password,String protocol);
    
    
}
