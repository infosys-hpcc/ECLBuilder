package org.hpccsystems.eclBuilder.entity;

public class User implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String password;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
