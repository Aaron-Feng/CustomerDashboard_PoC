package com.vonage.vnet.param;

import java.util.List;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.subaru.snet.csf.annotation.IgnoreField;
import com.subaru.snet.dpt.domain.DateValidationType;
import com.subaru.snet.dpt.domain.GenericForm;
import com.subaru.snet.dpt.domain.LookupType;
import com.subaru.snet.dpt.domain.ValidDate;
import com.subaru.snet.dpt.service.LookupService;

/**
 * Form bean class for Location Component.
 */

@Component("location")
@Scope("prototype")
public class LocationForm extends GenericForm {

	@Autowired
	private LookupService lookupService;

	@NotBlank(message = "Will the proposed dealership operate at the location of an existing dealership is required.")
	private String existDlrOpYN;

	@Size(max = 500, message = "Dealership Name must be {max} or less characters.")
	private String dlrName;
	
	@Size(max = 500, message = "Makes Represented must be {max} or less characters.")
	private String makesRep;
	
	private String facAvailableYN;
	
	@Size(max = 9, message = "Total Building Size must be a numeric value between 0 and 9999999.")
	private String bldingSize;
	
	@Size(max = 9, message = "Total Land Area must be a numeric value between 0 and 9999999.")
	private String landArea;
	
	@Size(max = 9, message = "New Vehicle Lot must be a numeric value between 0 and 9999999.")
	private String newVhclLotSize;
	
	@Size(max = 9, message = "Used Vehicle Lot must be a numeric value between 0 and 9999999.")
	private String usedVhclLotSize;
	
	private String ownedOrLeased;
	
	@Size(max = 7, message = "Annual Rent must be a numeric value between 0 and 9999999.")
	private String leasedAnnRent;

    @ValidDate(isRequired = false, dateValidationType = DateValidationType.FUTURE_DATE, regex = "^[0-9]{2}/[0-9]{2}/[0-9]{4}$", message = "Lease Expiration Date must be a valid date with format MM/DD/YYYY and must be greater than today's date.")
    private String leasedExpDate;
	
	@Size(max = 1, message = "Period of Renewal Options must be a numeric value between 0 and 9.")
	private String periodRenewOpt;

	@NotBlank(message = "Name of lessor or owner is required.")
	@Size(max = 500, message = "Name of lessor or owner must be {max} or less characters.")
	private String lessorName;

	@NotBlank(message = "Address is required.")
	@Size(max = 500, message = "Address must be {max} or less characters.")
	private String lessorStreet;

	@NotBlank(message = "City is required.")
	@Size(max = 500, message = "City must be {max} or less characters.")
	private String lessorCity;

	@NotBlank(message = "State is required.")
	private String lesssorSt;

	@NotBlank(message = "Phone is required.")
	@Pattern(regexp = "^[0-9]{3}-[0-9]{3}-[0-9]{4}$", message = "Please enter Phone with format xxx-xxx-xxxx.")
	private String lessorPhone;

	public String getExistDlrOpYN() {
		return existDlrOpYN;
	}

	public void setExistDlrOpYN(String existDlrOpYN) {
		this.existDlrOpYN = existDlrOpYN;
	}

	public String getDlrName() {
		return dlrName;
	}

	public void setDlrName(String dlrName) {
		this.dlrName = dlrName;
	}

	public String getMakesRep() {
		return makesRep;
	}

	public void setMakesRep(String makesRep) {
		this.makesRep = makesRep;
	}

	public String getFacAvailableYN() {
		return facAvailableYN;
	}

	public void setFacAvailableYN(String facAvailableYN) {
		this.facAvailableYN = facAvailableYN;
	}

	public String getBldingSize() {
		return bldingSize;
	}

	public void setBldingSize(String bldingSize) {
		this.bldingSize = bldingSize;
	}

	public String getLandArea() {
		return landArea;
	}

	public void setLandArea(String landArea) {
		this.landArea = landArea;
	}

	public String getNewVhclLotSize() {
		return newVhclLotSize;
	}

	public void setNewVhclLotSize(String newVhclLotSize) {
		this.newVhclLotSize = newVhclLotSize;
	}

	public String getUsedVhclLotSize() {
		return usedVhclLotSize;
	}

	public void setUsedVhclLotSize(String usedVhclLotSize) {
		this.usedVhclLotSize = usedVhclLotSize;
	}
	
	public String getOwnedOrLeased() {
		return ownedOrLeased;
	}

	public void setOwnedOrLeased(String ownedOrLeased) {
		this.ownedOrLeased = ownedOrLeased;
	}

	public String getLeasedAnnRent() {
		return leasedAnnRent;
	}

	public void setLeasedAnnRent(String leasedAnnRent) {
		this.leasedAnnRent = leasedAnnRent;
	}

	public String getLeasedExpDate() {
		return leasedExpDate;
	}

	public void setLeasedExpDate(String leasedExpDate) {
		this.leasedExpDate = leasedExpDate;
	}

	public String getPeriodRenewOpt() {
		return periodRenewOpt;
	}

	public void setPeriodRenewOpt(String periodRenewOpt) {
		this.periodRenewOpt = periodRenewOpt;
	}

	public String getLessorName() {
		return lessorName;
	}

	public void setLessorName(String lessorName) {
		this.lessorName = lessorName;
	}

	public String getLessorStreet() {
		return lessorStreet;
	}

	public void setLessorStreet(String lessorStreet) {
		this.lessorStreet = lessorStreet;
	}

	public String getLessorCity() {
		return lessorCity;
	}

	public void setLessorCity(String lessorCity) {
		this.lessorCity = lessorCity;
	}

	public String getLesssorSt() {
		return lesssorSt;
	}

	public void setLesssorSt(String lesssorSt) {
		this.lesssorSt = lesssorSt;
	}

	public String getLessorPhone() {
		return lessorPhone;
	}

	public void setLessorPhone(String lessorPhone) {
		this.lessorPhone = lessorPhone;
	}
	
	@IgnoreField
	public List<String> getStateOptions() {
		return lookupService.getLookupValues(LookupType.STATE_CODE);
	}

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LocationForm [existDlrOpYN=");
        builder.append(existDlrOpYN);
        builder.append(", dlrName=");
        builder.append(dlrName);
        builder.append(", makesRep=");
        builder.append(makesRep);
        builder.append(", facAvailableYN=");
        builder.append(facAvailableYN);
        builder.append(", bldingSize=");
        builder.append(bldingSize);
        builder.append(", landArea=");
        builder.append(landArea);
        builder.append(", newVhclLotSize=");
        builder.append(newVhclLotSize);
        builder.append(", usedVhclLotSize=");
        builder.append(usedVhclLotSize);
        builder.append(", ownedOrLeased=");
        builder.append(ownedOrLeased);
        builder.append(", leasedAnnRent=");
        builder.append(leasedAnnRent);
        builder.append(", leasedExpDate=");
        builder.append(leasedExpDate);
        builder.append(", periodRenewOpt=");
        builder.append(periodRenewOpt);
        builder.append(", lessorName=");
        builder.append(lessorName);
        builder.append(", lessorStreet=");
        builder.append(lessorStreet);
        builder.append(", lessorCity=");
        builder.append(lessorCity);
        builder.append(", lesssorSt=");
        builder.append(lesssorSt);
        builder.append(", lessorPhone=");
        builder.append(lessorPhone);
        builder.append("]");
        return builder.toString();
    }
	
}
