package com.vonage.vnet.param;

import org.springframework.stereotype.Component;

import com.subaru.snet.dpt.domain.GenericForm;

@Component("overview")
public class OverviewForm extends GenericForm {
	boolean addToDashboardAvailable;
	boolean waiveAvailable;
	boolean requireAvailable;
	boolean approveAvailable;
	boolean unapproveAvailable;
	boolean rejectAvailable;
	boolean generateZipAvailable;

	public boolean isAddToDashboardAvailable() {
		return addToDashboardAvailable;
	}

	public void setAddToDashboardAvailable(boolean addToDashboardAvailable) {
		this.addToDashboardAvailable = addToDashboardAvailable;
	}

	public boolean isWaiveAvailable() {
		return waiveAvailable;
	}

	public void setWaiveAvailable(boolean waiveAvailable) {
		this.waiveAvailable = waiveAvailable;
	}

	public boolean isRequireAvailable() {
		return requireAvailable;
	}

	public void setRequireAvailable(boolean requireAvailable) {
		this.requireAvailable = requireAvailable;
	}

	public boolean isApproveAvailable() {
		return approveAvailable;
	}

	public void setApproveAvailable(boolean approveAvailable) {
		this.approveAvailable = approveAvailable;
	}

	public boolean isUnapproveAvailable() {
		return unapproveAvailable;
	}

	public void setUnapproveAvailable(boolean unapproveAvailable) {
		this.unapproveAvailable = unapproveAvailable;
	}

	public boolean isRejectAvailable() {
		return rejectAvailable;
	}

	public void setRejectAvailable(boolean rejectAvailable) {
		this.rejectAvailable = rejectAvailable;
	}

	public boolean isGenerateZipAvailable() {
		return generateZipAvailable;
	}

	public void setGenerateZipAvailable(boolean generateZipAvailable) {
		this.generateZipAvailable = generateZipAvailable;
	}
}