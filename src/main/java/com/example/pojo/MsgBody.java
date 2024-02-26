package com.example.pojo;

public class MsgBody {
    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "MsgBody{" +
                ", msg='" + msg + '\'' +
                '}';
    }
}
