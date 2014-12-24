package org.thinkbigthings.boot.dto;

public class UserRegistration {
    
    private String userName;
    private String displayName;
    private String plaintextPassword;

    public UserRegistration() {
        
    }
    
    public String getUserName() {
        return userName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPlaintextPassword() {
        return plaintextPassword;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setPlaintextPassword(String plaintextPassword) {
        this.plaintextPassword = plaintextPassword;
    }
}
