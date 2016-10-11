package org.hpccsystems.eclBuilder.dao;

import java.util.Date;
import java.util.List;

import org.hpccsystems.eclBuilder.controller.Builder;
import org.hpccsystems.eclBuilder.controller.ECLBuilder;
import org.hpccsystems.eclBuilder.entity.User;
import org.hpccsystems.eclBuilder.exceptions.DatabaseException;


/**
 * Dao class,has abstract methods for composition-hpcc connection details
 * related DB hits
 * 
 * @author
 * 
 */
public interface EClBuilderDao {

    
    String FETCH_USER = "SELECT * FROM ECLBUILDERUSER WHERE USERID = ? AND PASSWORD = ?";
    


    String GET_ECL_BUILDERS = "SELECT * FROM (SELECT author,name,logicalFiles,lastmodifieddate, eclbuildercode, datasetFields, hpccConnId,wuid, @builderRank := IF(@curr_builder = name, @builderRank + 1, 1)" +
    		"AS builderRank, @curr_builder := name FROM hpccBuilder.eclbuilder  ORDER BY name, lastmodifieddate DESC ) ranked    WHERE author = ? and builderRank = 1;";
    
    String GET_ECL_BUILDERS_By_Name = "SELECT * FROM (SELECT author,name,logicalFiles,lastmodifieddate, eclbuildercode, datasetFields, hpccConnId,wuid, @builderRank := IF(@curr_builder = name, @builderRank + 1, 1)" +
    		"AS builderRank, @curr_builder := name FROM hpccBuilder.eclbuilder where author = ? and  name = ? ORDER BY name, lastmodifieddate DESC ) ranked    WHERE builderRank = 1;";

    String GET_ECL_BUILDER = "SELECT * FROM hpccBuilder.eclbuilder WHERE name = ? and wuid != '' order by lastmodifieddate desc";
    
    String ADD_ECL_BUILDERS = "INSERT INTO hpccBuilder.eclbuilder VALUES (?,?,?,?,?,?,?, ?)";
    
    String GET_QUERY_BY_WUID = "SELECT * from hpccBuilder.eclbuilder WHERE wuid = ?";
    
    String UPDATE_ECL_BUILDERS = "UPDATE `hpccBuilder`.`eclbuilder` SET lastmodifieddate = ? ,eclbuildercode = ? , datasetFields = ?  WHERE author = ? and name = ?" ;
    
    String DELETE_ECL_BUILDERS = "DELETE from `hpccBuilder`.`eclbuilder` where name = ?" ;
    
    /**
     * DAO query and other constants ends
     */
    

//    void log(DBLog log);
//
//    List<UserLog> getUserLog() throws DatabaseException;

	
	List<Builder> getECLBuilders(String userId) throws DatabaseException;
	
	User fetchUser(String userId, String password) throws DatabaseException;
    
    List<Builder> getECLBuildersByName(boolean byName, String userId,  String name) throws DatabaseException;
    
    List<Builder> getECLBuilder(String userId, String builderName, String hpccId) throws DatabaseException;

	int addOrUpdateECLBuilders(ECLBuilder eclBuilderDetails, boolean addOrUpdate) throws DatabaseException;
	
	int deleteECLBuilder(String author, String name) throws DatabaseException;
	
	String getECLQueryByWUID(String wuid) throws DatabaseException;
}
