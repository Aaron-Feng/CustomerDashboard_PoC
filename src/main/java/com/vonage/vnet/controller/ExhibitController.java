package com.vonage.vnet.controller;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;



/**
 * Controller class for generating data for component(tab) view and controlling switching between tabs.
 */
@Controller
public class ExhibitController {
    private static final Logger logger = LoggerFactory.getLogger(ExhibitController.class);

    @Autowired
    private ExhibitService exhibitService;

    @Autowired
    private ExhibitPDFService exhibitPDFService;

    @Autowired
    private DocumentManagementService documentManagementService;

    @Autowired
    private PackageServicesService packageServicesService;

    @Autowired
    private PackageDetailsService packageDetailsService;

    @Autowired
    private GenericFormService genericFormService;

    @Autowired
    private MemoService memoService;

    @Autowired
    private ExhibitRequestRepository exhibitRequestRepository;

    @Autowired
    private OverviewService overviewService;

    @Autowired
    private BenchmarkService benchmarkService;

    @Autowired
    private PackageRequestRepository packageRequestRepository;

    @Autowired
    private PackageService packageService;

    @Autowired
    private DDMSData ddmsData;

    /**
     * showExhibit(Model, Long, Long Long, Long) method is invoked when user submits form to save the data and land on component details page.
     * 
     * @param model
     * @param packageRequestId
     * @param packageSetId
     * @param packageExhibitXrefId
     * @param componentId
     * @return String
     */
    public String showExhibit(Model model, Long packageRequestId, Long packageSetId, Long packageExhibitXrefId, Long componentId) {
        // Load the exhibit template for the given PackageExhibitXref id
        if (packageExhibitXrefId == null) {
            logger.error("An exception occurred as the PackageExhibitXrefId is null.");
            model.addAttribute("errorMsg", "Error occurred in retrieving the component data. Association between Package and Exhibit is not available.");
            return "error";
        }

        ExhibitTemplate exhibitTemplate = exhibitService.getExhibitTemplate(packageRequestId, packageSetId, packageExhibitXrefId);
        model.addAttribute("exhibit", exhibitTemplate);

        ComponentTemplate selectedComponent = exhibitService.getComponentTemplate(packageRequestId, exhibitTemplate, componentId);
        model.addAttribute("component", selectedComponent);

        PackageTemplate packageTemplate = packageDetailsService.getPackageDetails(packageRequestId);
        packageTemplate.setExhibitId(exhibitTemplate.getId());
        packageTemplate.setExhibitRequestId(exhibitTemplate.getExhibitRequestId());
        packageTemplate.setComponentId(selectedComponent.getId());
        packageTemplate.setComponentRequestId(selectedComponent.getComponentRequestId());
        packageTemplate.setPackageSetId(packageSetId);
        packageTemplate.setPackageSetRequestId(exhibitTemplate.getPackageSetRequestId());
        packageTemplate.setPackageExhibitXrefId(packageExhibitXrefId);
        model.addAttribute("packageTemplate", packageTemplate);

        if (!model.containsAttribute("form")) {
            populateComponent(model, packageRequestId, exhibitTemplate, selectedComponent);
        }
        if ((ddmsData.getPackageRequestId() != null && !ddmsData.getPackageRequestId().equals(packageTemplate.getPackageRequestId())) || !model.containsAttribute("ddmsData")) {
            // Set data of ddms for the first time or when data present in session is not of current package_req_id
            ddmsData.setPackageRequestId(packageTemplate.getPackageRequestId());
            ddmsData.setDdmsDataMap(packageService.getPackageReqAttrData(packageTemplate.getPackageRequestId()));
            model.addAttribute("ddmsData", ddmsData);
        }
        return "exhibitDashboard/exhibitDetails";
    }

    /**
     * showExhibit(Model, PackageTemplate) method is invoked to retrieve components associated with particular exhibit request, when user clicks on a particular exhibit in package details page or switches
     * tabs in component details page.
     * 
     * @param model
     * @param packageTemplate
     * @return String
     */
    @RequestMapping(value = "/showExhibit")
    public String showExhibit(Model model, PackageTemplate packageTemplate) {
        // Load the exhibit template for the given ExhibitRequestId and optionally for a ComponentRequestId
        if (packageTemplate.getExhibitId() == null) {
            logger.error("An exception occured as the ExhibitRequestId is null");
            model.addAttribute("errorMsg", "Error occured in retrieving the component data. ExhibitRequestId is null.");
            return "error";
        }

        ExhibitTemplate exhibitTemplate = exhibitService.getExhibitTemplate(packageTemplate.getPackageRequestId(), packageTemplate.getPackageSetId(), packageTemplate.getPackageExhibitXrefId());
        model.addAttribute("exhibit", exhibitTemplate);

        ComponentTemplate selectedComponent = exhibitService.getComponentTemplate(packageTemplate.getPackageRequestId(), exhibitTemplate, packageTemplate.getComponentId());
        model.addAttribute("component", selectedComponent);

        model.addAttribute("packageTemplate", packageTemplate);

        if (!model.containsAttribute("form")) {
            populateComponent(model, packageTemplate.getPackageRequestId(), exhibitTemplate, selectedComponent);
        }
        if ((ddmsData.getPackageRequestId() != null && !ddmsData.getPackageRequestId().equals(packageTemplate.getPackageRequestId())) || !model.containsAttribute("ddmsData")) {
            //Set data of ddms for the first time or when data present in session is not of current package_req_id
            ddmsData.setPackageRequestId(packageTemplate.getPackageRequestId());
            ddmsData.setDdmsDataMap(packageService.getPackageReqAttrData(packageTemplate.getPackageRequestId()));
            model.addAttribute("ddmsData", ddmsData);
        }
        return "exhibitDashboard/exhibitDetails";
    }

    /**
     * submitEsignExhibit(SubmitForm, BindingResult, Model) method is invoked when exhibit with requires_esign_ind = y has been submitted.
     * 
     * @param submitForm
     * @param bindingResult
     * @param model
     * @return
     */
    @RequestMapping(value = "/submitExhibit", params = "eSign")
    public String submitEsignExhibit(@Validated(value = Esign.class) SubmitForm submitForm, BindingResult bindingResult, Model model) {
        if (!bindingResult.hasErrors()) {
            try {
                exhibitService.submitExhibit(submitForm.getPackageRequestId(), submitForm.getExhibitRequestId(), submitForm.getComponentRequestId(), submitForm.getSignatureText(),
                                                submitForm.getPassword());
            } catch (CSFException e) {
                if (e.getErrorCode() == CSFErrorCode.CSF_NOT_FOUND) {
                    bindingResult.addError(new ObjectError("password", "Document is not able to be signed.  Please resubmit."));
                } else {
                    // Need to assume an exception means the password entered was incorrect
                    bindingResult.addError(new ObjectError("password", "Password is incorrect."));
                }
            }
        } else {
            logger.debug("Esign required fields validation is failing.");
        }

        return showExhibit(model, submitForm.getPackageRequestId(), submitForm.getPackageSetId(), submitForm.getPackageExhibitXrefId(), submitForm.getComponentId());
    }

    /**
     * submitNonEsignExhibit(Model, SubmitForm) method is invoked when exhibit with requires_esign_ind = n has been submitted.
     * 
     * @param model
     * @param submitForm
     * @return
     */
    @RequestMapping(value = "/submitExhibit", params = "submit")
    public String submitNonEsignExhibit(Model model, SubmitForm submitForm) {
        try {
            exhibitService.submitExhibit(submitForm.getPackageRequestId(), submitForm.getExhibitRequestId(), submitForm.getComponentRequestId());
        } catch (CSFException e) {
            logger.error("Exception in submitting submit exhibit", e);
        }
        return showExhibit(model, submitForm.getPackageRequestId(), submitForm.getPackageSetId(), submitForm.getPackageExhibitXrefId(), submitForm.getComponentId());
    }

    /**
     * viewPDF(Model, SubmitForm) method is invoked when 'View PDF' button on eSign page is clicked.
     * 
     * @param model
     * @param submitForm
     * @return
     */
    @RequestMapping(value = "/submitExhibit", params = "viewpdf")
    public String viewPDF(Model model, SubmitForm submitForm) {
        model.addAttribute("form", submitForm);
        model.addAttribute("service", exhibitPDFService);
        model.addAttribute("repository", exhibitRequestRepository);
        // TODO: ADD DRAFT WATERMARK
        return "pdfView";
    }

    /**
     * populateComponent(Model, Long, ExhibitTemplate, ComponentTemplate) method is responsible for getting the associated component data and populate in the form and add form to model.
     * 
     * @param model
     * @param packageRequestId
     * @param exhibit
     * @param component
     */
    private void populateComponent(Model model, Long packageRequestId, ExhibitTemplate exhibit, ComponentTemplate component) {
        GenericForm form = null;

        switch (component.getType()) {
        case FORM:
            form = genericFormService.getGenericForm(component.getReference());
            genericFormService.populate(form, packageRequestId, exhibit.getExhibitRequestId(), component.getComponentRequestId());
            break;
        case MEMO:
            form = genericFormService.getGenericForm("memo");
            Collection<Memo> memos = memoService.getMemos(exhibit.getExhibitRequestId());
            model.addAttribute("memos", memos);
            break;
        case OVERVIEW:
            form = genericFormService.getGenericForm("overview");
            List<Attachment> exhibitAttachments = packageServicesService.getAttachmentsByExhibitRequest(exhibit.getExhibitRequestId());
            overviewService.populateForm((OverviewForm) form, exhibit.getExhibitRequestId(), exhibitAttachments);
            model.addAttribute("attachments", exhibitAttachments);
            if (exhibit.isEsignRequired()
                                            && (exhibit.getStatus() == ExhibitStatusType.SUBMITTED || exhibit.getStatus() == ExhibitStatusType.REVIEW || exhibit.getStatus() == ExhibitStatusType.APPROVED)) {
                model.addAttribute("signatures", documentManagementService.getSignatures(exhibit.getExhibitRequestId()));
            }
            break;
        case SUBMIT:
            form = genericFormService.getGenericForm("submit");
            if (exhibit.isEsignRequired()
                                            && (exhibit.getStatus() == ExhibitStatusType.SUBMITTED || exhibit.getStatus() == ExhibitStatusType.REVIEW || exhibit.getStatus() == ExhibitStatusType.APPROVED)) {
                model.addAttribute("signatures", documentManagementService.getSignatures(exhibit.getExhibitRequestId()));
            }
            ExhibitStatusType exhibitStatus = exhibit.getStatus();
            boolean isSubmitEligible = exhibitService.checkSubmitEligible(exhibit.getExhibitRequestId());
            model.addAttribute("showViewPDF", (exhibit.isTemplateAvailable() && isSubmitEligible && (exhibitStatus == ExhibitStatusType.OPEN || exhibitStatus == ExhibitStatusType.REJECTED)) ? true
                                            : false);
            model.addAttribute("submitEligible", isSubmitEligible);
            List<Attachment> exhibitAttachmentList = packageServicesService.getAttachmentsByExhibitRequest(exhibit.getExhibitRequestId());
            model.addAttribute("attachments", exhibitAttachmentList);
            break;
        case UPLOAD:
            form = genericFormService.getGenericForm("upload");
            List<Attachment> componentAttachments = packageServicesService.getAttachmentsByComponentRequest(component.getComponentRequestId());
            model.addAttribute("attachments", componentAttachments);
            break;
        case BENCHMARK:
            if ("benchmark".equals(component.getReference())) {
                form = genericFormService.getGenericForm("benchmark");
                GenericForm totalCureForm = genericFormService.getGenericForm("benchmark");
                benchmarkService.populateBenchmarkForm((BenchmarkForm) totalCureForm, packageRequestId, exhibit, component);
                model.addAttribute("totalCureForm", totalCureForm);
            } else {
                form = genericFormService.getGenericForm("benchmarkamend");
                Map<Long, List<BenchmarkInfo>> benchmarkInfoMap = benchmarkService.getBenchmarkDetail(packageRequestId, exhibit.getExhibitRequestId());
                BenchmarkAmendForm benchmarkAmendForm = (BenchmarkAmendForm) form;
                benchmarkAmendForm.setAddTotalCureAvailable((benchmarkInfoMap == null || benchmarkInfoMap.isEmpty()) ? true : false);
                benchmarkAmendForm.setHasBenchmarksToAdd(CollectionUtils.isNotEmpty(benchmarkService.getAmendmentBenchmarkOptions(exhibit.getId(), exhibit.getExhibitRequestId(), false)) ? true : false);
                model.addAttribute("benchmarkInfoMap", benchmarkInfoMap);
            }
            break;
        }

        form.setPackageRequestId(packageRequestId);
        form.setExhibitId(exhibit.getId());
        form.setPackageExhibitXrefId(exhibit.getPackageExhibitXrefId());
        form.setExhibitRequestId(exhibit.getExhibitRequestId());
        form.setComponentId(component.getId());
        form.setComponentRequestId(component.getComponentRequestId());
        form.setComponentReference(component.getReference());
        form.setExhibitComponentXrefId(component.getExhibitComponentXrefId());
        form.setPackageSetId(exhibit.getPackageSetId());
        form.setPackageSetRequestId(exhibit.getPackageSetRequestId());

        // Determine readonly mode
        if (component.getType() != ComponentType.SUBMIT) {
            if (component.getType() == ComponentType.MEMO) {
                form.setReadOnly(packageRequestRepository.findOne(packageRequestId).getStatus() == PackageStatusType.ACTIVATED);
            } else {
                form.setReadOnly((exhibit.getStatus() != ExhibitStatusType.OPEN && exhibit.getStatus() != ExhibitStatusType.REJECTED) || exhibit.getStatus() == ExhibitStatusType.WAIVED);
            }
        } else {
            exhibitService.setReadOnlyForSubmit(form);
        }
        model.addAttribute("form", form);
    }

}
