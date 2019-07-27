package com.haojg.id.node;

import java.util.Date;


public class WorkNode {
    private Long id;
    private String hostnameIpPort;

    private Date createDateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHostnameIpPort() {
        return hostnameIpPort;
    }

    public void setHostnameIpPort(String hostnameIpPort) {
        this.hostnameIpPort = hostnameIpPort;
    }

    public Date getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(Date createDateTime) {
        this.createDateTime = createDateTime;
    }
}
