package com.vonage.vnet.domain;

/**
 * DataAccessInfoResponse bean for holding 'GET_DATA_ACCESS_INFO' stored procedure response data.
 *
 */
public class DataAccessInfoResponse {

    private String dmsProvSystem;
    private String regionCode;
    private String zoneCode;
    private String districtCode;
    private String dealerNumber;
    private String dbaName;
    private Long dealerId;

    public String getDmsProvSystem() {
        return dmsProvSystem;
    }

    public void setDmsProvSystem(String dmsProvSystem) {
        this.dmsProvSystem = dmsProvSystem;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getZoneCode() {
        return zoneCode;
    }

    public void setZoneCode(String zoneCode) {
        this.zoneCode = zoneCode;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public String getDealerNumber() {
        return dealerNumber;
    }

    public void setDealerNumber(String dealerNumber) {
        this.dealerNumber = dealerNumber;
    }

    public String getDbaName() {
        return dbaName;
    }

    public void setDbaName(String dbaName) {
        this.dbaName = dbaName;
    }

    public Long getDealerId() {
        return dealerId;
    }

    public void setDealerId(Long dealerId) {
        this.dealerId = dealerId;
    }

}
