package com.linjingc.top.imwebsocketdemo.webService;

/**
 * 消息
 * @author cxc
 * @date 2019/11/10 20:48
 */
public class MessageDto {
    private String id;
    private String message;
    private String orgCode;
    private String departId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getDepartId() {
        return departId;
    }

    public void setDepartId(String departId) {
        this.departId = departId;
    }
}
