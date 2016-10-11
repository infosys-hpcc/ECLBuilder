package org.hpccsystems.eclBuilder.entity;

public class ClusterConfig implements java.io.Serializable {
   
	private static final long serialVersionUID = 1L;
    
    private String ip;
    private String port;
    private String userName;
    private String password;
    private String protocol;
    
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
    
	public ClusterConfig(String ip, String port, String userName, String password, String protocol) {
		super();
		this.ip = ip;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.protocol = protocol;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}


}
