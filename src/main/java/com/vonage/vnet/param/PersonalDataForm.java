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
 * Form bean class for Personal Data Component.
 */

@Component("personaldata")
@Scope("prototype")
public class PersonalDataForm extends GenericForm {

	@Autowired
	private LookupService lookupService;

	@NotBlank(message = "Full Name is required.")
	@Size(max = 500, message = "Full Name must be {max} or less characters.")
	private String dealerCandidateName;

    @ValidDate(isRequired = true, dateValidationType = DateValidationType.PAST_DATE, regex = "^[0-9]{2}/[0-9]{2}/[0-9]{4}$", message = "Date of Birth must be a valid date with format MM/DD/YYYY and must be less than today's date.")
    private String candDOB;

	@NotBlank(message = "Married is required.")
	private String candMS;

	@NotBlank(message = "Number of Dependents is required.")
	@Size(max = 3, message = "Number of Dependents must be {max} or less digits.")
	private String candNoDep;

	private String candHealth;

	@NotBlank(message = "Street Address 1 is required.")
	@Size(max = 500, message = "Street Address 1 must be {max} or less characters.")
	private String candStreet;

	@NotBlank(message = "City 1 is required.")
	@Size(max = 500, message = "City 1 must be {max} or less characters.")
	private String candCity;

	@NotBlank(message = "State 1 is required.")
	private String candST;

	@NotBlank(message = "Zip 1 is required.")
	@Pattern(regexp = "^\\d{5}$", message = "Please enter 5 digit Zip 1.")
	private String candZIP;

	@NotBlank(message = "Number of Years 1 is required.")
	@Size(max = 2, message = "Number of Years 1 must be {max} or less digits.")
	private String candYrs;

	@Size(max = 500, message = "Street Address 2 must be {max} or less characters.")
	private String candStreetPr;
	
	@Size(max = 500, message = "City 2 must be {max} or less characters.")
	private String candCityPr;
	
	private String candSTPr;
	
	@Pattern(regexp = "^(\\d{5})?$", message = "Please enter 5 digit Zip 2.")
	private String candZIPPr;
	
	@Size(max = 2, message = "Number of Years 2 must be {max} or less digits.")
	private String candYrsPr;
	
	@Size(max = 500, message = "Street Address 3 must be {max} or less characters.")
	private String candStreetP2;
	
	@Size(max = 500, message = "City 3 must be {max} or less characters.")
	private String candCityP2;
	
	private String candSTP2;
	
	@Pattern(regexp = "^(\\d{5})?$", message = "Please enter 5 digit Zip 3.")
	private String candZIPP2;
	
	@Size(max = 2, message = "Number of Years 3 must be {max} or less digits.")
	private String candYrsP2;

	@NotBlank(message = "Current Home Phone is required.")
	@Pattern(regexp = "^[0-9]{3}-[0-9]{3}-[0-9]{4}$", message = "Please enter Current Home Phone with format xxx-xxx-xxxx.")
	private String candHomePhone;

	@NotBlank(message = "Current Business Phone is required.")
	@Pattern(regexp = "^[0-9]{3}-[0-9]{3}-[0-9]{4}$", message = "Please enter Current Business Phone with format xxx-xxx-xxxx.")
	private String candBusPhone;

	public String getDealerCandidateName() {
		return dealerCandidateName;
	}

	public void setDealerCandidateName(String dealerCandidateName) {
		this.dealerCandidateName = dealerCandidateName;
	}

	public String getCandDOB() {
		return candDOB;
	}

	public void setCandDOB(String candDOB) {
		this.candDOB = candDOB;
	}

	public String getCandMS() {
		return candMS;
	}

	public void setCandMS(String candMS) {
		this.candMS = candMS;
	}

	public String getCandNoDep() {
		return candNoDep;
	}

	public void setCandNoDep(String candNoDep) {
		this.candNoDep = candNoDep;
	}

	public String getCandHealth() {
		return candHealth;
	}

	public void setCandHealth(String candHealth) {
		this.candHealth = candHealth;
	}

	public String getCandStreet() {
		return candStreet;
	}

	public void setCandStreet(String candStreet) {
		this.candStreet = candStreet;
	}

	public String getCandCity() {
		return candCity;
	}

	public void setCandCity(String candCity) {
		this.candCity = candCity;
	}

	public String getCandST() {
		return candST;
	}

	public void setCandST(String candST) {
		this.candST = candST;
	}

	public String getCandZIP() {
		return candZIP;
	}

	public void setCandZIP(String candZIP) {
		this.candZIP = candZIP;
	}

	public String getCandYrs() {
		return candYrs;
	}

	public void setCandYrs(String candYrs) {
		this.candYrs = candYrs;
	}

	public String getCandStreetPr() {
		return candStreetPr;
	}

	public void setCandStreetPr(String candStreetPr) {
		this.candStreetPr = candStreetPr;
	}

	public String getCandCityPr() {
		return candCityPr;
	}

	public void setCandCityPr(String candCityPr) {
		this.candCityPr = candCityPr;
	}

	public String getCandSTPr() {
		return candSTPr;
	}

	public void setCandSTPr(String candSTPr) {
		this.candSTPr = candSTPr;
	}

	public String getCandZIPPr() {
		return candZIPPr;
	}

	public void setCandZIPPr(String candZIPPr) {
		this.candZIPPr = candZIPPr;
	}

	public String getCandYrsPr() {
		return candYrsPr;
	}

	public void setCandYrsPr(String candYrsPr) {
		this.candYrsPr = candYrsPr;
	}

	public String getCandStreetP2() {
		return candStreetP2;
	}

	public void setCandStreetP2(String candStreetP2) {
		this.candStreetP2 = candStreetP2;
	}

	public String getCandCityP2() {
		return candCityP2;
	}

	public void setCandCityP2(String candCityP2) {
		this.candCityP2 = candCityP2;
	}

	public String getCandSTP2() {
		return candSTP2;
	}

	public void setCandSTP2(String candSTP2) {
		this.candSTP2 = candSTP2;
	}

	public String getCandZIPP2() {
		return candZIPP2;
	}

	public void setCandZIPP2(String candZIPP2) {
		this.candZIPP2 = candZIPP2;
	}

	public String getCandYrsP2() {
		return candYrsP2;
	}

	public void setCandYrsP2(String candYrsP2) {
		this.candYrsP2 = candYrsP2;
	}

	public String getCandHomePhone() {
		return candHomePhone;
	}

	public void setCandHomePhone(String candHomePhone) {
		this.candHomePhone = candHomePhone;
	}

	public String getCandBusPhone() {
		return candBusPhone;
	}

	public void setCandBusPhone(String candBusPhone) {
		this.candBusPhone = candBusPhone;
	}
	
	@IgnoreField
	public List<String> getStateOptions() {
		return lookupService.getLookupValues(LookupType.STATE_CODE);
	}

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PersonalDataForm [dealerCandidateName=");
        builder.append(dealerCandidateName);
        builder.append(", candDOB=");
        builder.append(candDOB);
        builder.append(", candMS=");
        builder.append(candMS);
        builder.append(", candNoDep=");
        builder.append(candNoDep);
        builder.append(", candHealth=");
        builder.append(candHealth);
        builder.append(", candStreet=");
        builder.append(candStreet);
        builder.append(", candCity=");
        builder.append(candCity);
        builder.append(", candST=");
        builder.append(candST);
        builder.append(", candZIP=");
        builder.append(candZIP);
        builder.append(", candYrs=");
        builder.append(candYrs);
        builder.append(", candStreetPr=");
        builder.append(candStreetPr);
        builder.append(", candCityPr=");
        builder.append(candCityPr);
        builder.append(", candSTPr=");
        builder.append(candSTPr);
        builder.append(", candZIPPr=");
        builder.append(candZIPPr);
        builder.append(", candYrsPr=");
        builder.append(candYrsPr);
        builder.append(", candStreetP2=");
        builder.append(candStreetP2);
        builder.append(", candCityP2=");
        builder.append(candCityP2);
        builder.append(", candSTP2=");
        builder.append(candSTP2);
        builder.append(", candZIPP2=");
        builder.append(candZIPP2);
        builder.append(", candYrsP2=");
        builder.append(candYrsP2);
        builder.append(", candHomePhone=");
        builder.append(candHomePhone);
        builder.append(", candBusPhone=");
        builder.append(candBusPhone);
        builder.append("]");
        return builder.toString();
    }

}