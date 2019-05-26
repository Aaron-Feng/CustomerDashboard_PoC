package com.vonage.vnet.servicesImpl;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * OverviewService class contains methods for all functionality on Overview Component.
 */
@Service
public class OverviewService {
    private static final Logger log = LoggerFactory.getLogger(OverviewService.class);

	@Autowired
	private PackageService packageService;

	@Autowired
	private ExhibitService exhibitService;

	@Autowired
	private DocumentManagementService documentManagementServices;

	@Autowired
	private ExhibitRequestRepository exhibitRequestRepository;

	@Autowired
	private PackageRequestRepository packageRequestRepository;

	@Autowired
	private SNETUserContext userContext;

    /**
     * addExhibitToDealerDashboard(Long, Long) method is responsible for updating Dealer Visible flag.
     * 
     * @param exhibitRequestId
     * @param packageRequestId
     */
	public void addExhibitToDealerDashboard(Long exhibitRequestId, Long packageRequestId) {
		log.debug("Modifying exhibit request [" + exhibitRequestId + "] to Dealer Visible");
        Date currentDate = new Date();
        exhibitRequestRepository.updateDealerVisibleInd(exhibitRequestId, true, userContext.getUser().getUsername(), currentDate);

        // Update PackageRequest with recent activity(Add to Dealer Dashboard) performed on the exhibit.
        packageRequestRepository.updateLastSubmitDate(packageRequestId, currentDate);
    }

	/**
	 * approveExhibit(Long, Long) method is responsible for updating Exhibit Status based on the user role.
	 * 
	 * @param packageRequestId
	 * @param exhibitRequestId
	 */
	public void approveExhibit(Long packageRequestId, Long exhibitRequestId) {
		log.debug("Modifying exhibit request [" + exhibitRequestId + "] to update status as Approved or Under Review");
        Date currentDate = new Date();
        if (userContext.hasRole("NATIONAL")) {
            exhibitRequestRepository.updateSOAReviewStatus(exhibitRequestId, ExhibitStatusType.APPROVED, userContext.getUser().getUsername(), currentDate);
        } else {
            exhibitRequestRepository.updateRegionReviewStatus(exhibitRequestId, ExhibitStatusType.REVIEW, userContext.getUser().getUsername(), currentDate);
        }
        // Update PackageRequest with recent activity(Approve) performed on the exhibit.
        packageRequestRepository.updateLastSubmitDate(packageRequestId, currentDate);

		// Check to see if next exhibit group needs to be created
		packageService.checkExhibitGroup(packageRequestId, true);

		// TODO: Update web center to update meta-data with approval info (TBD)
	}

	/**
	 * rejectExhibit(Long) method is responsible for updating Exhibit Status.
	 * 
	 * @param exhibitRequestId
	 */
	public void rejectExhibit(Long exhibitRequestId) {
		exhibitService.rejectExhibitRequest(exhibitRequestId);
	}

	/**
	 * waiveExhibit(Long, Long) method is responsible for updating Exhibit Status, Dealer Visible flag and Region Require flag.
	 * 
	 * @param packageRequestId
	 * @param exhibitRequestId
	 */
    public void waiveExhibit(Long packageRequestId, Long exhibitRequestId) {
        log.debug("Modifying exhibit request [" + exhibitRequestId + "] to update status as Waived, setting Dealer Visible flag to false and Region Required flag to false");
        Date currentDate = new Date();
        String currentUser = userContext.getUser().getUsername();
        ExhibitRequest exhibitRequest = exhibitRequestRepository.findOne(exhibitRequestId);
        if (exhibitRequest != null) {
            exhibitRequest.setStatus(ExhibitStatusType.WAIVED);
            exhibitRequest.setDealerVisible(false);
            exhibitRequest.setRegionReviewUser(null);
            exhibitRequest.setRegionReviewDate(null);
            exhibitRequest.setSoaReviewUser(currentUser);
            exhibitRequest.setSoaReviewDate(currentDate);
            exhibitRequest.setUpdateDate(currentDate);
            exhibitRequest.setUpdateUser(currentUser);
            Boolean regionRequired = exhibitRequest.getRegionRequired();
            if (regionRequired != null && regionRequired) {
                exhibitRequest.setRegionRequired(false);
            }
            exhibitRequestRepository.save(exhibitRequest);
        }
        // Update PackageRequest with recent activity(Waive) performed on the exhibit.
        packageRequestRepository.updateLastSubmitDate(packageRequestId, currentDate);

        // Check to see if next exhibit group needs to be created
        packageService.checkExhibitGroup(packageRequestId, true);
    }

	/**
	 * requireExhibit(Long) method is responsible for updating exhibit request and remove esign document if esigned.
	 * 
	 * @param exhibitRequestId
	 */
    public void requireExhibit(Long exhibitRequestId) {
        log.debug("Modifying exhibit request [" + exhibitRequestId + "] to update status as Open, setting Dealer Visible flag to true and Region Require flag to true");
        Date currentDate = new Date();
        String userName = userContext.getUser().getUsername();
        ExhibitRequest exhibitRequest = exhibitRequestRepository.findOne(exhibitRequestId);
        if (exhibitRequest != null) {
            exhibitRequest.setStatus(ExhibitStatusType.OPEN);
            exhibitRequest.setSoaReviewUser(null);
            exhibitRequest.setSoaReviewDate(null);
            exhibitRequest.setRegionReviewUser(null);
            exhibitRequest.setRegionReviewDate(null);
            exhibitRequest.setUpdateDate(currentDate);
            exhibitRequest.setUpdateUser(userName);
            if (exhibitRequest.getPackageExhibitXref().getEsignRequired()) {
                exhibitRequest.incrementReviewCycle();
            }
            if (exhibitRequest.getPackageExhibitXref().getAllowDealerDashboard()) {
                if (!exhibitRequest.getDealerVisible()) {
                    exhibitRequest.setDealerVisible(true);
                }
            } else {
                exhibitRequest.setRegionRequired(true);
            }
            exhibitRequestRepository.save(exhibitRequest);

            // Update PackageRequest with recent activity(Require) performed on the exhibit.
            packageRequestRepository.updateLastSubmitDate(exhibitRequest.getPackageRequestId(), currentDate);

            documentManagementServices.deleteNonAttachments(exhibitRequestId);
            exhibitService.updateSubmitCompletedInd(exhibitRequestId);
            if (PackageStatusType.COMPLETE.equals(packageRequestRepository.findOne(exhibitRequest.getPackageRequestId()).getStatus())) {
                packageRequestRepository.updatePackageStatus(exhibitRequest.getPackageRequestId(), PackageStatusType.PENDING, userName, currentDate);
            }
        }
    }

	/**
	 * unapproveExhibit(Long, Long) method is responsible for updating Exhibit Status.
	 * 
	 * @param exhibitRequestId
	 * @param packageRequestId
	 */
	public void unapproveExhibit(Long exhibitRequestId, Long packageRequestId) {
		exhibitService.rejectExhibitRequest(exhibitRequestId);
		// Added to change package status back to pending when an exhibit is un-approved after package is complete and not activated.
		if (PackageStatusType.COMPLETE.equals(packageRequestRepository.findOne(packageRequestId).getStatus())) {
			packageRequestRepository.updatePackageStatus(packageRequestId, PackageStatusType.PENDING, userContext.getUser().getUsername(), new Date());
		}
	}

	/**
	 * generateZip(Long, String, String) method is responsible for generating a zip file with all documents which have been uploaded to web center, for a given exhibitRequestId.
	 * 
	 * @param exhibitRequestId
	 * @param dealerNumber
	 * @param exhibitName
	 * @return
	 * @throws IOException
	 */
	public String generateZip(Long exhibitRequestId, String dealerNumber, String exhibitName) throws IOException {
		return documentManagementServices.generateZip(exhibitRequestId, dealerNumber, exhibitName);
	}

    /**
     * populateForm(OverviewForm, Long, List<Attachment>) method is responsible for setting flags that would determine whether or not to show the set of buttons (Add to Dealer Dashboard, Waive, Do Not
     * Waive, Approve, Reject, Unapprove, Generate Zip) on the Overview screen.
     * 
     * @param form
     * @param exhibitRequestId
     * @param exhibitAttachments
     */
	public void populateForm(OverviewForm form, Long exhibitRequestId, List<Attachment> exhibitAttachments) {
		ExhibitRequest exhibitRequest = exhibitRequestRepository.findOne(exhibitRequestId);
        if (exhibitRequest != null) {
            Boolean dealerVisible = exhibitRequest.getDealerVisible();
            Boolean allowDealerDashboard = exhibitRequest.getPackageExhibitXref().getAllowDealerDashboard();
            Boolean override = exhibitRequest.getPackageExhibitXref().getOverride();
            Boolean zoneRegionUser = userContext.hasAnyRole("ZONE_OFFICE", "REGION_OFFICE");
            Boolean nationalUser = userContext.hasRole("NATIONAL");
            ExhibitStatusType exhibitStatus = exhibitRequest.getStatus();
            form.setAddToDashboardAvailable(zoneRegionUser && !dealerVisible && allowDealerDashboard && !override);
            form.setWaiveAvailable(nationalUser && override && exhibitStatus != ExhibitStatusType.WAIVED && exhibitRequest.getPackageRequest().getStatus() != PackageStatusType.ACTIVATED);
            form.setRequireAvailable((nationalUser || (zoneRegionUser && allowDealerDashboard && exhibitStatus != ExhibitStatusType.WAIVED)) && override && !dealerVisible
                                            && exhibitRequest.getPackageRequest().getStatus() != PackageStatusType.ACTIVATED);
            form.setApproveAvailable((zoneRegionUser && exhibitStatus == ExhibitStatusType.SUBMITTED && exhibitService.isRegionEsignCompleted(exhibitRequest))
                                            || (nationalUser && exhibitStatus == ExhibitStatusType.REVIEW && exhibitService.isSOAEsignCompleted(exhibitRequest)));
            form.setRejectAvailable((zoneRegionUser && exhibitStatus == ExhibitStatusType.SUBMITTED) || (nationalUser && exhibitStatus == ExhibitStatusType.REVIEW));
            form.setUnapproveAvailable(nationalUser && exhibitStatus == ExhibitStatusType.APPROVED && exhibitRequest.getPackageRequest().getStatus() != PackageStatusType.ACTIVATED);
            form.setGenerateZipAvailable((exhibitStatus == ExhibitStatusType.SUBMITTED || exhibitStatus == ExhibitStatusType.REVIEW || exhibitStatus == ExhibitStatusType.APPROVED)
                                            && exhibitAttachments != null && !exhibitAttachments.isEmpty());
        }
	}

}