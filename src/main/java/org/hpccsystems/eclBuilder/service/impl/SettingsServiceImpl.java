package org.hpccsystems.eclBuilder.service.impl;

import org.hpccsystems.eclBuilder.dao.EClBuilderDao;
import org.hpccsystems.eclBuilder.service.SettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

@Service("settingsService")
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SettingsServiceImpl implements SettingsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsServiceImpl.class);
    
    private EClBuilderDao eclBuilderDao;
    
}
