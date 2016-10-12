package org.hpccsystems.eclBuilder.init;

import java.io.File;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
public class H2DBConfig {
	
    @Bean(name = "h2Datasource")
    public DataSource getDataSource(){
    	DataSource dataSource = null;
    	
    	try {
	        dataSource = createDataSource();
	        DatabasePopulatorUtils.execute(createDatabasePopulator(), dataSource);
	        return dataSource;
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	return dataSource;
    }

    private DatabasePopulator createDatabasePopulator() {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        databasePopulator.setContinueOnError(true);
		
        if(!new File("hpccbuilder.mv.db").exists()) {
        	databasePopulator.addScript(new ClassPathResource("scripts.sql"));
        }
		
        return databasePopulator;
    }

    private SimpleDriverDataSource createDataSource() {
        SimpleDriverDataSource simpleDriverDataSource = new SimpleDriverDataSource();
        simpleDriverDataSource.setDriverClass(org.h2.Driver.class);
        simpleDriverDataSource.setUrl("jdbc:h2:./hpccbuilder;AUTO_SERVER=TRUE;AUTO_SERVER_PORT=9090");
        simpleDriverDataSource.setUsername("root");
        simpleDriverDataSource.setPassword("xCl@m@t10n!!");
        return simpleDriverDataSource;      
    }
}
