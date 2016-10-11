package org.hpccsystems.eclBuilder.service.impl;

import org.hpccsystems.eclBuilder.dao.EClBuilderDao;
import org.hpccsystems.eclBuilder.service.DBLogger;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

@Service("dbLogger")
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class DBLoggerImpl implements DBLogger {
    
    private EClBuilderDao eclBuilderDao;
    
}
