package com.example.jaipurduss_client.dto;


public class RequestDto {

    private String deviceDateTime;
    private String deviceId;
    private String eiplCode;
    private String imeiNo;
    private String latLong;
    private String versionNo;

    public RequestDto(String deviceDateTime, String deviceId, String eiplCode,
                      String imeiNo, String latLong, String versionNo) {
        this.deviceDateTime = deviceDateTime;
        this.deviceId = deviceId;
        this.eiplCode = eiplCode;
        this.imeiNo = imeiNo;
        this.latLong = latLong;
        this.versionNo = versionNo;
    }

    public RequestDto(){
    }

    public String getDeviceDateTime() {
        return deviceDateTime;
    }

    public void setDeviceDateTime(String deviceDateTime) {
        this.deviceDateTime = deviceDateTime;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getEiplCode() {
        return eiplCode;
    }

    public void setEiplCode(String eiplCode) {
        this.eiplCode = eiplCode;
    }

    public String getImeiNo() {
        return imeiNo;
    }

    public void setImeiNo(String imeiNo) {
        this.imeiNo = imeiNo;
    }

    public String getLatLong() {
        return latLong;
    }

    public void setLatLong(String latLong) {
        this.latLong = latLong;
    }

    public String getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(String versionNo) {
        this.versionNo = versionNo;
    }
}