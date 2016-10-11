
package org.hpccsystems.eclBuilder.dao.impl;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.hpccsystems.eclBuilder.controller.Builder;
import org.hpccsystems.eclBuilder.controller.ECLBuilder;
import org.hpccsystems.eclBuilder.dao.EClBuilderDao;
import org.hpccsystems.eclBuilder.entity.User;
import org.hpccsystems.eclBuilder.exceptions.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;
import org.zkoss.util.resource.Labels;

/**
 * Dao class to do widget related DB hits
 * 
 * @author
 * 
 */
@Service("EClBuilderDao")
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class EClBuilderDaoImpl implements EClBuilderDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(EClBuilderDaoImpl.class);

    private JdbcTemplate jdbcTemplate;
    

    private ResultSetExtractor<User> fetchUserExtactor = new ResultSetExtractor<User>() {
        @Override
        public User extractData(ResultSet resultset) throws SQLException, DataAccessException {
            User user = null;
            while (resultset.next()) {
            	user = new User();
            	user.setId(resultset.getString("UserID"));
            	user.setPassword(resultset.getString("password"));
            }

            return user;
        }
    };
    
    private ResultSetExtractor<List<Builder>> eclBuilderExtractor = new ResultSetExtractor<List<Builder>>() {
        @Override
        public List<Builder> extractData(ResultSet resultset) throws SQLException, DataAccessException {
            List<Builder> builders = new ArrayList<Builder>();
            while (resultset.next()) {
            	Builder build = new Builder();
            	build.setAuthor(resultset.getString("author"));
            	build.setLogicalFiles(resultset.getString("logicalFiles"));
            	build.setName(resultset.getString("name"));
//            	build.setLastmodifieddate(resultset.getDate("lastmodifieddate"));
            	build.setTimestamp(resultset.getTimestamp("lastmodifieddate"));
            	
            	Blob blob = ((Blob)resultset.getBlob("eclbuildercode"));
            	byte[] bdata = blob.getBytes(1, (int) blob.length());
            	build.setEclbuildercode(new String(bdata));
            	blob = (Blob)resultset.getBlob("datasetFields");
            	if(blob != null){
            		byte[] bdataNew = blob.getBytes(1, (int) blob.length());
            		build.setDsFields(new String(bdataNew));
            	}else{
            		build.setDsFields("");
            	}
            	
            	build.setHpccId(resultset.getString("hpccConnId"));
            	build.setWuID(resultset.getString("wuid"));
                LOGGER.debug("derm ----{}", build);
                builders.add(build);
            }

            return builders;
        }
    };

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    @Autowired
    @Qualifier("mySQLDataSource")
    public void setDataSourceToJdbcTemplate(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @PostConstruct
    public void initialize() {
        LOGGER.info("Testing HPCCBuilder Database connection.");
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            LOGGER.info("Test connection success.");
        } catch (Exception e) {
            LOGGER.info("Test connection failed.");
            LOGGER.error("Exception", e);
        }
    }

	@Override
	public List<Builder> getECLBuilders(String userId) throws DatabaseException {
		return getECLBuildersByName(false, userId, null);
	}

	@Override
	public List<Builder> getECLBuildersByName(boolean byName, String userId,  String name) throws DatabaseException {
        try {
            LOGGER.debug("Getting layouts for Builders");
            return byName ?  getJdbcTemplate().query(GET_ECL_BUILDERS_By_Name,new Object[] {userId, name}, eclBuilderExtractor) :
            	getJdbcTemplate().query(GET_ECL_BUILDERS,new Object[] {userId}, eclBuilderExtractor) ;
        } catch (EmptyResultDataAccessException e) {
            LOGGER.error("Layout not found. Error -  {}", e);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DatabaseException(Labels.getLabel("Database Connection Failed"), e);
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        }
		return null;
    }

	@Override
	public List<Builder> getECLBuilder(String author, String name, String hpccId) throws DatabaseException {
        try {
            LOGGER.debug("Getting layouts for Builders");
            return getJdbcTemplate().query(GET_ECL_BUILDER,new Object[] {name}, eclBuilderExtractor);
        } catch (EmptyResultDataAccessException e) {
            LOGGER.error("Layout not found. Error -  {}", e);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DatabaseException(Labels.getLabel("Database Connection Failed"), e);
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        }
		return null;
    }

	@Override
	public int addOrUpdateECLBuilders(ECLBuilder eclBuilderDetails, boolean addOrUpdate) throws DatabaseException {
        try {
            LOGGER.debug("Getting layouts for Builders");
            if(addOrUpdate){
            	return getJdbcTemplate().update(ADD_ECL_BUILDERS,new Object[] {eclBuilderDetails.getUser_id(),
            				eclBuilderDetails.getName(), eclBuilderDetails.getLogicalFiles(), eclBuilderDetails.getModified_date(),
            					eclBuilderDetails.getEclbuildercode(), eclBuilderDetails.getHpccConnId(), eclBuilderDetails.getWuID(), eclBuilderDetails.getDsFields()});
            }else{
            	return getJdbcTemplate().update(UPDATE_ECL_BUILDERS,new Object[] {eclBuilderDetails.getModified_date(),
        					eclBuilderDetails.getEclbuildercode(), eclBuilderDetails.getLogicalFiles(),
            				eclBuilderDetails.getName()});

            }
        } catch (EmptyResultDataAccessException e) {
            LOGGER.error("Layout not found. Error -  {}", e);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DatabaseException(Labels.getLabel("Database Connection Failed"), e);
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        }
		return 0;
    }

	@Override
	public int deleteECLBuilder(String author, String name) throws DatabaseException {
		// TODO Auto-generated method stub
		return getJdbcTemplate().update(DELETE_ECL_BUILDERS, new Object[] {name});
	}

	@Override
	public String getECLQueryByWUID(String wuid) throws DatabaseException {
		// TODO Auto-generated method stub
		return (getJdbcTemplate().query(GET_QUERY_BY_WUID, new Object[] {wuid }, eclBuilderExtractor)).get(0).getEclbuildercode();
	}

	@Override
	public User fetchUser(String userId, String password) throws DatabaseException {
		// TODO Auto-generated method stub
		try{
			return (getJdbcTemplate().query(FETCH_USER, new Object[] {userId, password }, fetchUserExtactor));
		}catch(Exception e){
			return null;
		}
	}

}
