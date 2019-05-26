package com.vonage.vnet.servicesImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;



/**
 * ExhibitService class contains methods for handling operations related to Exhibit functionality, including getting data of forms that are already submitted.
 */

@Service
public class ExhibitService {
    private static final Logger logger = LoggerFactory.getLogger(ExhibitService.class);

    @Autowired
    private ExhibitRepository exhibitRepository;

    @Autowired
    private ExhibitRequestRepository exhibitRequestRepository;

    @Autowired
    private ComponentRepository componentRepository;

    @Autowired
    private ComponentRequestRepository componentRequestRepository;

    @Autowired
    private ComponentRequestAttributeRepository componentRequestAttributeRepository;

    @Autowired
    private PackageExhibitXrefRepository packageExhibitXrefRepository;

    @Autowired
    private ExhibitComponentXrefRepository exhibitComponentXrefRepository;

    @Autowired
    private PackageRequestRepository packageRequestRepository;

    @Autowired
    private PackageSetRequestRepository packageSetRequestRepository;
    
    @Autowired
    private TrackingTypeRepository trackingTypeRepository;

    @Autowired
    private ExhibitPDFService exhibitPDFService;

    @Autowired
    private DocumentManagementService documentManagementService;

    @Autowired
    private PackageServicesService packageServicesService;
    
    @Autowired
    private PackageService packageService;

    @Resource(name = "profileAttributeNames")
    private List<String> profileAttributeNames;

    @Autowired
    private SNETUserContext userContext;
    
    @Value("${profile.exhibitname:Profile}")
    private String profileExhibit;
    
    @Value("${dealeragreement.exhibitname:Dealer Agreement}")
    private String dealerAgreementExhibit;

    /**
     * getExhibitTemplate(Long, Long, Long) method is responsible for retrieving exhibit and component data from database, for the given packageRequestId, exhibitId and mapping the data returned to
     * ExhibitTemplate.
     * 
     * @param packageRequestId
     * @param packageSetId
     * @param packageExhibitXrefId
     * @return exhibitTemplate
     */
    public ExhibitTemplate getExhibitTemplate(Long packageRequestId, Long packageSetId, Long packageExhibitXrefId) {
        PackageExhibitXref packageExhibitXref = packageExhibitXrefRepository.findOne(packageExhibitXrefId);
        if (packageExhibitXref == null) {
            logger.error("PackageExhibitXref returned is null for packageExhibitXrefId [{}]", packageExhibitXrefId);
            return null;
        }
        
        ExhibitRequest exhibitRequest = exhibitRequestRepository.findByPackageRequestIdAndPackageExhibitXrefId(packageRequestId, packageExhibitXref.getId());
        Exhibit exhibit = packageExhibitXref.getExhibit();

        // Populate the exhibit data
        ExhibitTemplate exhibitTemplate = new ExhibitTemplate();
        exhibitTemplate.setId(exhibit.getId());
        exhibitTemplate.setDescription(exhibit.getDescription());
        exhibitTemplate.setName(exhibit.getName());
        exhibitTemplate.setCode(exhibit.getCode());
        exhibitTemplate.setAmendmentExhibitCode(exhibit.getAmendmentExhibitCode());
        exhibitTemplate.setTemplateAvailable(StringUtils.isNotBlank(exhibit.getTemplateName()));
        exhibitTemplate.setPackageExhibitXrefId(packageExhibitXref.getId());
        if (exhibitRequest != null) {
            exhibitTemplate.setExhibitRequestId(exhibitRequest.getId());
            exhibitTemplate.setStatus(exhibitRequest.getStatus());
        } else {
            exhibitTemplate.setStatus(ExhibitStatusType.OPEN);
        }
        exhibitTemplate.setEsignRequired(packageExhibitXref.getEsignRequired());
        exhibitTemplate.setRegionEsignRequired(isRegionEsignCompleted(exhibitRequest) ? false : true);
        exhibitTemplate.setSoaEsignRequired(isSOAEsignCompleted(exhibitRequest) ? false : true);
        exhibitTemplate.setPackageSetId(packageSetId);
        exhibitTemplate.setPackageSetRequestId(exhibitRequest.getPackageSetRequestId());
        // Load the relevant xrefs from the created requests
        List<ExhibitComponentXref> exhibitComponentXrefList = (userContext.hasRole("DEALER_EXECUTIVE")) ? componentRequestRepository.findDealerVisibleExhibitComponentXrefsByExhibitRequestId(exhibitRequest.getId()) : componentRequestRepository.findExhibitComponentXrefsByExhibitRequestId(exhibitRequest.getId());

        // Load the relevant components
        List<Component> components = componentRepository.findComponentsById(getComponentIdList(exhibitComponentXrefList));

        List<ComponentTemplate> componentTemplates = new ArrayList<ComponentTemplate>();
        for (ExhibitComponentXref exhibitComponentXref : exhibitComponentXrefList) {
            ComponentTemplate componentTemplate = new ComponentTemplate();
            componentTemplate.setId(exhibitComponentXref.getComponentId());
            componentTemplate.setRequired(exhibitComponentXref.getRequired());

            Component component = getComponent(exhibitComponentXref.getComponentId(), components);
            if (component != null) {
                componentTemplate.setName(component.getName());
                componentTemplate.setReference(getReference(component.getCode()));
                componentTemplate.setText(component.getText());
                componentTemplate.setType(component.getType());
                componentTemplate.setExhibitComponentXrefId(exhibitComponentXref.getId());
                componentTemplate.setInitialFilename(component.getInitialFilename());
                ComponentRequest componentRequest = componentRequestRepository.findByPackageRequestIdAndExhibitRequestIdAndExhibitComponentXrefId(packageRequestId, exhibitRequest.getId(), exhibitComponentXref.getId());
                if (componentRequest != null) {
                    componentTemplate.setCompleted(componentRequest.getCompleted());
                }
            }
            componentTemplates.add(componentTemplate);
        }
        exhibitTemplate.setComponentTemplates(componentTemplates);
        return exhibitTemplate;
    }

    /**
     * isSOAEsignCompleted(ExhibitRequest) method checks to see if SOA user has eSigned the respective exhibit whose requires_soa_esign_ind flag is 'y', at least once.
     * 
     * @param exhibitRequest
     * @return
     */
    public boolean isSOAEsignCompleted(ExhibitRequest exhibitRequest) {
        boolean soaEsigned = false;
        if (userContext.hasRole("NATIONAL") && exhibitRequest.getPackageExhibitXref().getSoaEsignRequired() != null && exhibitRequest.getPackageExhibitXref().getSoaEsignRequired()) {
            List<Signature> signatureList = documentManagementService.getSignatures(exhibitRequest.getId());
            if (signatureList != null && !signatureList.isEmpty()) {
                for (Signature signature : signatureList) {
                    if ("soa".equals(signature.getRole()) && exhibitRequest.getReviewCycle().equals(Integer.valueOf(signature.getReviewCycle()))) {
                        soaEsigned = true;
                        break;
                    }
                }
            }
        } else {
            soaEsigned = true;
        }
        return soaEsigned;
    }

    /**
     * isRegionEsignCompleted(ExhibitRequest) method checks to see if the Region or Zone user has eSigned the respective exhibit whose requires_reg_esign_ind flag is 'y', at least once.
     * 
     * @param exhibitRequest
     * @return
     */
    public boolean isRegionEsignCompleted(ExhibitRequest exhibitRequest) {
        boolean regionEsigned = false;
        if (userContext.hasAnyRole("ZONE_OFFICE", "REGION_OFFICE") && exhibitRequest.getPackageExhibitXref().getRegionEsignRequired() != null
                                        && exhibitRequest.getPackageExhibitXref().getRegionEsignRequired()) {
            List<Signature> signatureList = documentManagementService.getSignatures(exhibitRequest.getId());
            if (signatureList != null && !signatureList.isEmpty()) {
                for (Signature signature : signatureList) {
                    if ("region".equals(signature.getRole()) && exhibitRequest.getReviewCycle().equals(Integer.valueOf(signature.getReviewCycle()))) {
                        regionEsigned = true;
                        break;
                    }
                }
            }
        } else {
            regionEsigned = true;
        }
        return regionEsigned;
    }

    /**
     * getComponentTemplate(Long, ExhibitTemplate, Long) method is responsible for returning selected component.
     * 
     * @param packageRequestId
     * @param exhibitTemplate
     * @param componentId
     * @return ComponentTemplate
     */
    public ComponentTemplate getComponentTemplate(Long packageRequestId, ExhibitTemplate exhibitTemplate, Long componentId) {
        if (exhibitTemplate == null || exhibitTemplate.getComponentTemplates() == null || exhibitTemplate.getComponentTemplates().isEmpty()) {
            logger.error("ExhibitTemplate is null or ComponentTemplates for the given ExhibitTemplate are empty");
            return null;
        }

        for (ComponentTemplate componentTemplate : exhibitTemplate.getComponentTemplates()) {
            // If component id is null or 0, use the first found template
            // Changed '==' to 'equals' for comparing 'componentId' as the comparison with '==' doesn't hold good for values greater than 127
            if (componentId == null || componentId == 0 || componentId.equals(componentTemplate.getId())) {
                if (exhibitTemplate.getExhibitRequestId() != null) {
                    ComponentRequest componentRequest = componentRequestRepository.findByPackageRequestIdAndExhibitRequestIdAndExhibitComponentXrefId(packageRequestId,
                                                    exhibitTemplate.getExhibitRequestId(), componentTemplate.getExhibitComponentXrefId());
                    if (componentRequest != null) {
                        componentTemplate.setComponentRequestId(componentRequest.getId());
                    }
                }
                return componentTemplate;
            }
        }

        return null;
    }

    /**
     * submitExhibit(Long, Long, Long) method is responsible for submitting an exhibit with requires_esign_ind = n.
     * 
     * @param packageRequestId
     * @param exhibitRequestId
     * @param componentRequestId
     * @throws CSFException
     */
    public void submitExhibit(Long packageRequestId, Long exhibitRequestId, Long componentRequestId) throws CSFException {
        submitExhibit(packageRequestId, exhibitRequestId, componentRequestId, null, null);
    }

    /**
     * submitExhibit(Long, Long, Long, String, String) method is responsible for submitting an exhibit.
     * 
     * @param packageRequestId
     * @param exhibitRequestId
     * @param componentRequestId
     * @param signatureText
     * @param password
     * @throws CSFException
     */
    public void submitExhibit(Long packageRequestId, Long exhibitRequestId, Long componentRequestId, String signatureText, String password) throws CSFException {
        ExhibitRequest exhibitRequest = exhibitRequestRepository.findOne(exhibitRequestId);
        if (exhibitRequest == null) {
            logger.error("Exhibit request [{}] not found " + exhibitRequestId);
            throw new CSFException("An error was encountered processing the exhibit", CSFErrorCode.CSF_INVALID_CONFIGURATION);
        }

        if (exhibitRequest.getPackageExhibitXref().getEsignRequired() && StringUtils.isBlank(password)) {
            throw new CSFException("Password is required", CSFErrorCode.CSF_VALIDATION);
        }

        String documentId = null;
        String filename = exhibitRequest.getPackageExhibitXref().getExhibit().getTemplateName();
        if (StringUtils.isNotBlank(filename) && !isDocumentGenerated(exhibitRequest.getId())) {
            documentId = exhibitPDFService.createExhibitPDF(exhibitRequest, componentRequestId);
            if (documentId == null) {
                throw new CSFException("Document was not able to be signed", CSFErrorCode.CSF_NOT_FOUND);
            }
        }
        
        boolean bypassApproval = packageRequestRepository.findOne(packageRequestId).getPack().getBypassApproval();
        if (exhibitRequest.getPackageExhibitXref().getEsignRequired()) {
            if (signatureText == null) {
                signatureText = userContext.getUser().getUsername();
            }
            try {
                documentManagementService.signExhibit(exhibitRequestId, (bypassApproval ? ExhibitStatusType.APPROVED : ExhibitStatusType.SUBMITTED), exhibitRequest.getReviewCycle(), signatureText, password);
            } catch (CSFException csfe) {
                // Attempt to delete the generated document
                if (StringUtils.isNotBlank(documentId)) {
                    try {
                        documentManagementService.deleteFile(documentId);
                    } catch (IOException ioe) {
                        // Ignore
                    }
                }
                throw (csfe);
            }

            try {
                documentManagementService.deleteAllAttachmentsByExhibitRequestId(exhibitRequestId, AttachmentType.SIGNATURE);
            } catch (IOException ioe) {
                logger.error("Error deleting signature attachments", ioe);
                return;
            }

            List<Signature> signatures = documentManagementService.getSignatures(exhibitRequestId);
            exhibitPDFService.createSignaturePDF(exhibitRequest, componentRequestId, signatures);
            
            // Skip Region/Zone and SOA approval and approve the exhibit
            if(bypassApproval) {
                approveExhibit(packageRequestId, exhibitRequestId, componentRequestId);
                return;
            }
        }
        Date currentDate = new Date();
        if (userContext.hasRole("DEALER_EXECUTIVE") || (userContext.hasAnyRole("ZONE_OFFICE", "REGION_OFFICE") && !exhibitRequest.getPackageExhibitXref().getAllowDealerDashboard())) {
            componentRequestRepository.updateCompletedInd(componentRequestId, true, userContext.getUser().getUsername(), currentDate);
            if (exhibitRequest.getStatus() == ExhibitStatusType.REJECTED) {
                exhibitRequestRepository.updateRejectedExhibitStatus(exhibitRequestId, ExhibitStatusType.SUBMITTED, userContext.getUser().getUsername(), currentDate);
            } else {
                exhibitRequestRepository.updateStatus(exhibitRequestId, ExhibitStatusType.SUBMITTED, userContext.getUser().getUsername(), currentDate);
            }
            packageRequestRepository.updateLastSubmitDate(packageRequestId, currentDate);
        }
    }
    
    /**
     * approveExhibit(Long, Long, Long) method is responsible for updating Component, Exhibit and Package status for the packages having 'BYPASS_APPROVAL_IND = y'.
     * 
     * @param packageRequestId
     * @param exhibitRequestId
     * @param componentRequestId
     */
    public void approveExhibit(Long packageRequestId, Long exhibitRequestId, Long componentRequestId) {
        logger.debug("Updating Component, Exhibit and Package status for package request {}", packageRequestId);
        Date currentDate = new Date();
        String currentUser = userContext.getUser().getUsername();
        // Update completed indicator for the submitted component
        componentRequestRepository.updateCompletedInd(componentRequestId, true, currentUser, currentDate);
        // Update Exhibit status to 'Approved'
        exhibitRequestRepository.updateExhibitStatusAndSubmitDate(exhibitRequestId, ExhibitStatusType.APPROVED, currentUser, currentDate);
        // Check to see if next exhibit group needs to be created
        packageService.checkExhibitGroup(packageRequestId, true);
        packageRequestRepository.updateLastSubmitDate(packageRequestId, currentDate);
    }

    /**
     * getComponentIdList(List<ExhibitComponentXref>) method returns a set of component id's from a given list of ExhibitComponentXref.
     * 
     * @param exhibitComponentXrefList
     * @return
     */
    private Set<Long> getComponentIdList(List<ExhibitComponentXref> exhibitComponentXrefList) {
        Set<Long> componentIds = new HashSet<Long>();
        for (ExhibitComponentXref exhibitComponentXref : exhibitComponentXrefList) {
            componentIds.add(exhibitComponentXref.getComponentId());
        }
        return componentIds;
    }

    /**
     * getComponent(Long, List<Component>) method returns component object from the list of components whose componentId equals the Id passed.
     * 
     * @param componentId
     * @param components
     * @return
     */
    private Component getComponent(Long componentId, List<Component> components) {
        if (componentId == null) {
            return null;
        }

        for (Component component : components) {
            if (componentId.equals(component.getId())) {
                return component;
            }
        }

        return null;
    }

    /**
     * getReference(String) method returns component description by removing whitespace, _ and converts it to lower case.
     * 
     * @param componentCode
     * @return string
     */
    private String getReference(String componentCode) {
        if (componentCode == null) {
            return null;
        }

        String reference = StringUtils.deleteWhitespace(componentCode);
        return StringUtils.remove(reference, '_').toLowerCase();
    }

    /**
     * createRequestComponents(GenericForm, boolean) method is invoked to create and insert exhibitRequest/componentRequest entries into database for the exhibits/components which are added/associated
     * post generation of exhibit/component request.
     * 
     * @param form
     * @param createComponentRequest
     */
    public void createRequestComponents(GenericForm form, boolean createComponentRequest) {
        // If an exhibit request does not exist, create it
        Date currentDate = new Date();
        if (form.getExhibitRequestId() == null || form.getExhibitRequestId() == 0) {
            ExhibitRequest exhibitRequest = new ExhibitRequest();
            exhibitRequest.setPackageRequestId(form.getPackageRequestId());
            exhibitRequest.setPackageSetRequestId(form.getPackageSetRequestId());
            exhibitRequest.setPackageExhibitXrefId(form.getPackageExhibitXrefId());
            exhibitRequest.setCreateDate(currentDate);
            exhibitRequest.setCreateUser(userContext.getUser().getUsername());
            exhibitRequest.setUpdateDate(currentDate);
            exhibitRequest.setUpdateUser(userContext.getUser().getUsername());
            PackageExhibitXref packageExhibitXref = packageExhibitXrefRepository.findOne(form.getPackageExhibitXrefId());
            exhibitRequest.setStatus(ExhibitStatusType.OPEN);
            exhibitRequest.setDealerVisible(packageExhibitXref.getDealerVisible());
            exhibitRequest.setReviewCycle(1);
            exhibitRequest.setPackageRequest(packageRequestRepository.findOne(form.getPackageRequestId()));
            exhibitRequest.setPackageSetRequest(packageSetRequestRepository.findOne(form.getPackageSetRequestId()));
            exhibitRequest.setPackageExhibitXref(packageExhibitXref);
            exhibitRequestRepository.save(exhibitRequest);
            form.setExhibitRequestId(exhibitRequest.getId());
        }

        // If a component request does not exist, create it
        if (createComponentRequest && (form.getComponentRequestId() == null || form.getComponentRequestId() == 0)) {
            ComponentRequest componentRequest = new ComponentRequest();
            componentRequest.setPackageRequestId(form.getPackageRequestId());
            componentRequest.setPackageSetRequestId(form.getPackageSetRequestId());
            componentRequest.setExhibitRequestId(form.getExhibitRequestId());
            componentRequest.setExhibitComponentXrefId(form.getExhibitComponentXrefId());
            Component component = componentRepository.findOne(form.getComponentId());
            componentRequest.setCompleted((component.getType().equals(ComponentType.OVERVIEW) || component.getType().equals(ComponentType.MEMO) ? true : false));
            componentRequest.setInitialFile(false);
            componentRequest.setCreateDate(currentDate);
            componentRequest.setCreateUser(userContext.getUser().getUsername());
            componentRequest.setUpdateDate(currentDate);
            componentRequest.setUpdateUser(userContext.getUser().getUsername());
            componentRequest.setExhibitComponentXref(exhibitComponentXrefRepository.findOne(form.getExhibitComponentXrefId()));
            componentRequest.setExhibitRequest(exhibitRequestRepository.findOne(form.getExhibitRequestId()));
            componentRequest.setPackageRequest(packageRequestRepository.findOne(form.getPackageRequestId()));
            componentRequest.setPackageSetRequest(packageSetRequestRepository.findOne(form.getPackageSetRequestId()));
            componentRequestRepository.save(componentRequest);
            form.setComponentRequestId(componentRequest.getId());
        }
    }

    /**
     * setReadOnlyForSubmit(GenericForm) method is used to determine readOnly flag for all user types based on user role and exhibit status.
     * 
     * @param form
     */
    public void setReadOnlyForSubmit(GenericForm form) {
        ExhibitRequest exhibitRequest = exhibitRequestRepository.findOne(form.getExhibitRequestId());
        if (exhibitRequest != null) {
            Boolean allowDealerDashboard = exhibitRequest.getPackageExhibitXref().getAllowDealerDashboard();
            ExhibitStatusType exhibitStatus = exhibitRequest.getStatus();
            form.setReadOnly((userContext.hasRole("DEALER_EXECUTIVE") && ((allowDealerDashboard && !(exhibitStatus == ExhibitStatusType.OPEN || exhibitStatus == ExhibitStatusType.REJECTED)) || !allowDealerDashboard))
                                            || (userContext.hasAnyRole("ZONE_OFFICE", "REGION_OFFICE") && ((allowDealerDashboard && exhibitStatus != ExhibitStatusType.SUBMITTED) || (!allowDealerDashboard && (exhibitStatus != ExhibitStatusType.OPEN && exhibitStatus != ExhibitStatusType.REJECTED))))
                                            || (userContext.hasRole("NATIONAL") && exhibitStatus != ExhibitStatusType.REVIEW));
        }
    }

    /**
     * checkSubmitEligible(Long) method is responsible for checking if all the required component's associated with the given exhibitRequestId are completed.
     * 
     * @param exhibitRequestId
     * @return
     */
    public boolean checkSubmitEligible(Long exhibitRequestId) {
        List<ComponentRequest> componentRequestList = componentRequestRepository.findByExhibitRequestId(exhibitRequestId);
        for (ComponentRequest componentRequest : componentRequestList) {
            // Ignore the completed on the following component types
            if (!componentRequest.getExhibitComponentXref().getComponent().getType().isCompletedApplicable()) {
                continue;
            }

            if (componentRequest.getExhibitComponentXref().getRequired() && !componentRequest.getCompleted())
                return false;
        }
        return true;
    }

    /**
     * isDocumentGenerated(Long) method is responsible to check if attachment of type document is available in web center.
     * 
     * @param exhibitRequestId
     * @return
     */
    private boolean isDocumentGenerated(Long exhibitRequestId) {
        List<Attachment> attachments = packageServicesService.getAttachmentsByExhibitRequest(exhibitRequestId);
        if (attachments == null || attachments.isEmpty()) {
            return false;
        }

        for (Attachment attachment : attachments) {
            if (attachment.getAttachmentType() == AttachmentType.DOCUMENT) {
                return true;
            }
        }

        return false;
    }

    /**
     * rejectExhibitRequest(Long) method is responsible for rejecting an exhibit and its related exhibits (the ones with status as ‘Approved/Submitted/Under Review’ based on the name of the exhibit
     * being rejected and the indicator has_profile_ind.
     * 
     * @param exhibitRequestId
     */
    public void rejectExhibitRequest(Long exhibitRequestId) {
        ExhibitRequest rejectedRequest = exhibitRequestRepository.findOne(exhibitRequestId);
        if (rejectedRequest == null) {
            return;
        }
        List<ExhibitStatusType> exhibitStatusList = Arrays.asList(ExhibitStatusType.APPROVED, ExhibitStatusType.SUBMITTED, ExhibitStatusType.REVIEW);
        if (exhibitStatusList.contains(rejectedRequest.getStatus())) {
            rejectExhibitRequest(rejectedRequest);
            // Reject exhibits only when present rejected exhibit has 'Profile' in the exhibit name.
            if (rejectedRequest.getPackageExhibitXref().getExhibit().getName().contains(profileExhibit)) {
                List<ExhibitRequest> exhibitRequests = null;
                // Exhibit Name not like ‘%Profile%’ and (has_profile_ind='y' or has Exhibit Name like ‘%Dealer Agreement %’
                if (rejectedRequest.getPackageExhibitXref().getHasProfile()) {
                    exhibitRequests = exhibitRequestRepository.findAllByPackageRequestIdAndExhibitStatusAndExhibitNameAndHasProfile(rejectedRequest.getPackageRequestId(), exhibitStatusList,
                                                    profileExhibit, dealerAgreementExhibit, true);
                } else {
                    exhibitRequests = exhibitRequestRepository.findAllByPackageRequestIdAndExhibitStatusAndExhibitName(rejectedRequest.getPackageRequestId(), exhibitStatusList, dealerAgreementExhibit);
                }
                for (ExhibitRequest exhibitRequest : exhibitRequests) {
                    if (exhibitRequest.getId() == rejectedRequest.getId()) {
                        continue; // Already rejected
                    }
                    rejectExhibitRequest(exhibitRequest);
                }
            }
        }
    }

    /**
     * rejectExhibitRequest(ExhibitRequest) method is responsible for updating exhibit status to rejected, delete all non-attachments from web center and update submit component as not completed.
     * 
     * @param exhibitRequest
     */
    private void rejectExhibitRequest(ExhibitRequest exhibitRequest) {
        // Increase the review cycle if esign is required
        if (exhibitRequest.getPackageExhibitXref().getEsignRequired()) {
            exhibitRequest.incrementReviewCycle();
        }
        Date currentDate = new Date();
        String currentUser = userContext.getUser().getUsername();
        // Set the status to rejected
        exhibitRequest.setStatus(ExhibitStatusType.REJECTED);
        exhibitRequest.setUpdateDate(currentDate);
        if (userContext.hasRole("NATIONAL")) {
            exhibitRequest.setRegionReviewUser(null);
            exhibitRequest.setRegionReviewDate(null);
            exhibitRequest.setSoaReviewUser(currentUser);
            exhibitRequest.setSoaReviewDate(currentDate);
        } else {
            exhibitRequest.setRegionReviewUser(currentUser);
            exhibitRequest.setRegionReviewDate(currentDate);
            exhibitRequest.setSoaReviewUser(null);
            exhibitRequest.setSoaReviewDate(null);
        }

        exhibitRequestRepository.save(exhibitRequest);

        // Delete generated and signature attachments
        documentManagementService.deleteNonAttachments(exhibitRequest.getId());

        // Reset the status on the submit component
        updateSubmitCompletedInd(exhibitRequest.getId());
        
        // Update PackageRequest with recent activity(Reject) performed on the exhibit.
        packageRequestRepository.updateLastSubmitDate(exhibitRequest.getPackageRequestId(), currentDate);
    }

    /**
     * updateSubmitCompletedInd(Long) method is responsible for updating completed indicator for submit component.
     * 
     * @param exhibitRequestId
     */
    public void updateSubmitCompletedInd(Long exhibitRequestId) {
        List<ComponentRequest> componentRequests = componentRequestRepository.findByExhibitRequestId(exhibitRequestId);
        if (componentRequests != null && !componentRequests.isEmpty()) {
            for (ComponentRequest componentRequest : componentRequests) {
                if (componentRequest.getExhibitComponentXref().getComponent().getType().equals(ComponentType.SUBMIT)) {
                    componentRequestRepository.updateCompletedInd(componentRequest.getId(), false, userContext.getUser().getUsername(), new Date());
                }
            }
        }
    }

    /**
     * getComponentRequestAttribute(List<ComponentRequestAttribute>, String) method is responsible for obtaining componentRequestAttribute whose attributeName is same as provided string.
     * 
     * @param componentRequestAttributes
     * @param componentRequestAttributeName
     * @return
     */
    public ComponentRequestAttribute getComponentRequestAttribute(List<ComponentRequestAttribute> componentRequestAttributes, String componentRequestAttributeName) {
        if (componentRequestAttributes == null || componentRequestAttributes.isEmpty() || componentRequestAttributeName == null) {
            return null;
        }

        for (ComponentRequestAttribute attribute : componentRequestAttributes) {
            if (componentRequestAttributeName.equals(attribute.getComponentRequestAttributeId().getName())) {
                return attribute;
            }
        }

        return null;
    }

    /**
     * getAllExhibitsOrderByName() method is used to get a list of exhibits in the ascending order of exhibit name.
     * 
     * @return exhibitList
     */
    public List<Exhibit> getAllExhibitsOrderByName() {
        return exhibitRepository.findActiveExhibits();
    }

    /**
     * getAllComponentsOrderByName() method is used to get a list of components in the ascending order of component name.
     * 
     * @return componentList
     */
    public List<Component> getAllComponentsOrderByName() {
        return componentRepository.findActiveComponents();
    }

    /**
     * getExhibitComponentAssociationsByExhibitId(Long) method is responsible for getting unchanged(change_ind='n') component(ExhibitComponentXref) associations, in the ascending order of
     * their tab order.
     * 
     * @param exhibitId
     * @return exhibitComponentXrefList
     */
    public List<ExhibitComponentXref> getExhibitComponentAssociationsByExhibitId(Long exhibitId) {
        return exhibitComponentXrefRepository.findByExhibitIdAndChangedFalseOrderByTabOrderAsc(exhibitId);
    }

    /**
     * getStreetAddress(Long) method is used to retrieve the dealer street address, if it exists.
     * 
     * @param packageRequestId
     * @return
     */
    public DealerFacilities getStreetAddress(Long packageRequestId) {
        DealerFacilities streetAddress = null;
        List<ComponentRequestAttribute> profileAttributes = componentRequestAttributeRepository.findByPackageRequestIdAndName(packageRequestId, profileAttributeNames);
        if (profileAttributes != null && !profileAttributes.isEmpty()) {
            streetAddress = new DealerFacilities();
            for (ComponentRequestAttribute attribute : profileAttributes) {
                if ("dealerStreetAddress".equalsIgnoreCase(attribute.getComponentRequestAttributeId().getName())) {
                    streetAddress.setFacAddr(attribute.getValue());
                    continue;
                }
                if ("dealerCity".equalsIgnoreCase(attribute.getComponentRequestAttributeId().getName())) {
                    streetAddress.setFacCity(attribute.getValue());
                    continue;
                }
                if ("dealerStateCode".equalsIgnoreCase(attribute.getComponentRequestAttributeId().getName())) {
                    streetAddress.setFacST(attribute.getValue());
                    continue;
                }
                if ("dealerZipCode".equalsIgnoreCase(attribute.getComponentRequestAttributeId().getName())) {
                    streetAddress.setFacZip(attribute.getValue());
                    continue;
                }
                if ("dealerPhone".equalsIgnoreCase(attribute.getComponentRequestAttributeId().getName())) {
                    streetAddress.setFacPh(attribute.getValue());
                    continue;
                }
                if ("dealerFax".equalsIgnoreCase(attribute.getComponentRequestAttributeId().getName())) {
                    streetAddress.setFacFAX(attribute.getValue());
                    continue;
                }
            }
        }
        return streetAddress;
    }

    /**
     * populateStreetAddress(Long, Map<String, String) method is used to populate the street address from dealer facilities stored in DPT_PACKAGE_REQ_ATTR and update street address from
     * DPT_COMPONENT_REQ_ATTR if it is modified.
     * 
     * @param packageRequestId
     * @param packageAttributes
     * @return
     */
    public void populateStreetAddress(Long packageRequestId, Map<String, String> packageAttributes) {
        // Populate street address from package attributes
        populateStreetAddress(packageAttributes, packageAttributes);

        // Fetch facility address attributes from component attributes
        ArrayList<String> fields = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            List<String> addressFields = Arrays.asList("dealerFacilityList[" + i + "].facAddTP", "dealerFacilityList[" + i + "].facAddr", "dealerFacilityList[" + i + "].facCity",
                                            "dealerFacilityList[" + i + "].facST", "dealerFacilityList[" + i + "].facZip", "dealerFacilityList[" + i + "].facPh", "dealerFacilityList[" + i + "].facFAX");
            fields.addAll(addressFields);
        }
        // Populate street address from component attributes
        populateStreetAddress(packageAttributes, getComponentAttributesMap(componentRequestAttributeRepository.findByPackageRequestIdAndName(packageRequestId, fields)));
    }

    /**
     * populateStreetAddress(Map<String, String>, Map<String, String>) method is responsible to read street address attributes from one set of attributes and populate it into another set of
     * attributes.
     * 
     * @param packageAttributes
     * @param sourceAttributes
     */
    public void populateStreetAddress(Map<String, String> packageAttributes, Map<String, String> sourceAttributes) {
        if (packageAttributes == null || packageAttributes.isEmpty() || sourceAttributes == null || sourceAttributes.isEmpty()) {
            return;
        }
        for (int i = 0; i < 20; i++) {
            if ("Street".equalsIgnoreCase(sourceAttributes.get("dealerFacilityList[" + i + "].facAddTP"))) {
                if (StringUtils.isNotBlank(sourceAttributes.get("dealerFacilityList[" + i + "].facAddr"))) {
                    packageAttributes.put("dealerStreetAddress", sourceAttributes.get("dealerFacilityList[" + i + "].facAddr"));
                }
                if (StringUtils.isNotBlank(sourceAttributes.get("dealerFacilityList[" + i + "].facCity"))) {
                    packageAttributes.put("dealerCity", sourceAttributes.get("dealerFacilityList[" + i + "].facCity"));
                }
                if (StringUtils.isNotBlank(sourceAttributes.get("dealerFacilityList[" + i + "].facST"))) {
                    packageAttributes.put("dealerStateCode", sourceAttributes.get("dealerFacilityList[" + i + "].facST"));
                }
                if (StringUtils.isNotBlank(sourceAttributes.get("dealerFacilityList[" + i + "].facZip"))) {
                    packageAttributes.put("dealerZipCode", sourceAttributes.get("dealerFacilityList[" + i + "].facZip"));
                }
                if (StringUtils.isNotBlank(sourceAttributes.get("dealerFacilityList[" + i + "].facPh"))) {
                    packageAttributes.put("dealerPhone", sourceAttributes.get("dealerFacilityList[" + i + "].facPh"));
                }
                if (StringUtils.isNotBlank(sourceAttributes.get("dealerFacilityList[" + i + "].facFAX"))) {
                    packageAttributes.put("dealerFax", sourceAttributes.get("dealerFacilityList[" + i + "].facFAX"));
                }
                break;
            }
        }
    }

    /**
     * getComponentAttributesMap(List<ComponentRequestAttribute>) method is responsible to populate a map from a list of component attributes.
     * 
     * @param componentAttributes
     * @return
     */
    public Map<String, String> getComponentAttributesMap(List<ComponentRequestAttribute> componentAttributes) {
        if (componentAttributes == null || componentAttributes.isEmpty()) {
            return null;
        }
        Map<String, String> componentAttributesMap = new HashMap<String, String>();
        for (ComponentRequestAttribute componentAttribute : componentAttributes) {
            componentAttributesMap.put(componentAttribute.getComponentRequestAttributeId().getName(), componentAttribute.getValue());
        }
        return componentAttributesMap;
    }

    /**
     * getTrackingTypes() method is responsible for retrieving a list of active tracking types in ascending order of tracking name.
     * 
     * @return List<TrackingType>
     */
    public List<TrackingType> getTrackingTypes() {
        return trackingTypeRepository.findActiveTrackingTypes();
    }
}