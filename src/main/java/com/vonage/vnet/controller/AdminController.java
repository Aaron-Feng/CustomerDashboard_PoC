package com.vonage.vnet.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;

import com.subaru.snet.csf.auth.domain.SNETUserContext;
import com.subaru.snet.dpt.domain.AdminComponentTemplate;
import com.subaru.snet.dpt.domain.AdminComponentType;
import com.subaru.snet.dpt.domain.AdminExhibitTemplate;
import com.subaru.snet.dpt.domain.AdminPackageSetTemplate;
import com.subaru.snet.dpt.domain.AdminPackageTemplate;
import com.subaru.snet.dpt.domain.AdminTemplate;
import com.subaru.snet.dpt.entity.Component;
import com.subaru.snet.dpt.entity.Exhibit;
import com.subaru.snet.dpt.entity.Package;
import com.subaru.snet.dpt.entity.PackageSet;
import com.subaru.snet.dpt.service.AdminService;
import com.subaru.snet.dpt.service.ExhibitService;
import com.subaru.snet.dpt.service.PackageService;

/**
 * AdminController class contains methods that are responsible for retrieval of data for admin functionality and handles the switching between tabs.
 */
@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminService adminService;

    @Autowired
    private PackageService packageService;

    @Autowired
    private ExhibitService exhibitService;
    
    @Autowired
    private SNETUserContext userContext;
    
    /**
     * initBinder(ServletRequestDataBinder) method is responsible to let the Date field be blank for 'Create' functions and to convert String form of date to Date(Format :day month date hours:minutes:seconds time-zone year) object for 'Update' functions.
     * 
     * @param binder
     * @throws Exception
     */
    @InitBinder
    protected void initBinder(ServletRequestDataBinder binder) throws Exception {
        CustomDateEditor editor = new CustomDateEditor(new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy"), true);
        binder.registerCustomEditor(Date.class, editor);
    }

    /**
     * adminMenu(Model) method is responsible for redirecting the user to Admin Intermediate page.
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "/menu")
    public String adminMenu(Model model) {
        // Display error when user who does not have SNET_SUPERUSER role accesses this URL directly.
        if (!userContext.hasRole("SNET_SUPERUSER")) {
            model.addAttribute("errorMsg", "Error occurred in displaying admin home page. User does not have SNET_SUPERUSER role.");
            return "error";
        }
        return "admin/menu";
    }
    
    /**
     * adminHome(Model) method is responsible for redirecting the user to Admin Component.
     * @param model
     * @return
     */
    @RequestMapping(value = "/home")
    public String adminHome(Model model) {
        model.addAttribute("adminTemplate", adminService.getAdminTemplate());
        model.addAttribute("adminComponentTemplate", adminService.getBasicAdminComponentTemplate());
        return "admin/main";
    }
    

    /**
     * showComponent(Model, AdminTemplate) method is responsible for displaying an appropriate admin component.
     * 
     * @param model
     * @param adminTemplate
     * @return
     */
    @RequestMapping(value = "/showComponent")
    public String showComponent(Model model, AdminTemplate adminTemplate) {
        if (adminTemplate == null) {
            model.addAttribute("errorMsg", "Error occurred in displaying admin page. Admin template is null.");
            return "error";
        }
        adminTemplate = adminService.getAdminTemplateByPageName(adminTemplate.getPageName());
        AdminComponentType adminComponentType = AdminComponentType.getByValue(adminTemplate.getPageName());
        if (AdminComponentType.CREATE_COMPONENT == adminComponentType || AdminComponentType.EDIT_COMPONENT == adminComponentType) {
            model.addAttribute("adminComponentTemplate", adminService.getBasicAdminComponentTemplate());
        } else if (AdminComponentType.CREATE_EXHIBIT == adminComponentType || AdminComponentType.EDIT_EXHIBIT == adminComponentType) {
            model.addAttribute("adminExhibitTemplate", adminService.getBasicAdminExhibitTemplate());
        } else if (AdminComponentType.CREATE_PACKAGE == adminComponentType || AdminComponentType.EDIT_PACKAGE == adminComponentType) {
            model.addAttribute("adminPackageTemplate", adminService.getBasicAdminPackageTemplate());
        } else if (AdminComponentType.CREATE_PACKAGE_SET == adminComponentType || AdminComponentType.EDIT_PACKAGE_SET == adminComponentType) {
            model.addAttribute("adminPackageSetTemplate", adminService.getBasicAdminPackageSetTemplate());
        }
        // Hide fields on edit component/exhibit/package page initially
        if (AdminComponentType.EDIT_PACKAGE == adminComponentType || AdminComponentType.EDIT_EXHIBIT == adminComponentType || AdminComponentType.EDIT_COMPONENT == adminComponentType
                                        || AdminComponentType.EDIT_PACKAGE_SET == adminComponentType) {
            model.addAttribute("hideFieldsForEdit", "true");
        }
        model.addAttribute("adminTemplate", adminTemplate);
        return "admin/main";
    }

    /**
     * createPackageSet(AdminPackageSetTemplate, BindingResult, Model, AdminTemplate) method is responsible for creating a new package set.
     * 
     * @param adminPackageSetTemplate
     * @param result
     * @param model
     * @param adminTemplate
     * @return
     */
    @RequestMapping(value = "/createPackageSet")
    public String createPackageSet(@Valid AdminPackageSetTemplate adminPackageSetTemplate, BindingResult result, Model model, AdminTemplate adminTemplate) {
        if (adminPackageSetTemplate == null || adminTemplate == null) {
            model.addAttribute("errorMsg", "Error occurred in adding new package set.");
            return "error";
        }
        try {
            if (!result.hasErrors()) {
                List<PackageSet> packageSetList = adminService.getPackageSetDetailsByPackageSetCode(adminPackageSetTemplate.getPackageSetCode());
                if (packageSetList == null || packageSetList.isEmpty()) {
                    adminService.savePackageSet(adminPackageSetTemplate);
                    model.addAttribute("confirmMessage", "Package Set '" + adminPackageSetTemplate.getPackageSetName() + "' was created successfully.");
                    model.addAttribute("adminPackageSetTemplate", adminService.getBasicAdminPackageSetTemplate());
                } else {
                    result.addError(new ObjectError("duplicatePackageSetError", "The specified Package Set Code already exists."));
                }
            }
        } catch (Exception e) {
            logger.error("Exception occurred in adding package set details", e);
            result.addError(new ObjectError("packageSetAddError", "There was an error in adding package set details."));
        } finally {
            if (result.hasErrors()) {
                adminPackageSetTemplate.setExhibitList(exhibitService.getAllExhibitsOrderByName());
                model.addAttribute("adminPackageSetTemplate", adminPackageSetTemplate);
            }
        }
        model.addAttribute("adminTemplate", adminService.getAdminTemplateByPageName(adminTemplate.getPageName()));
        return "admin/main";
    }

    /**
     * updatePackageSet(AdminPackageSetTemplate, BindingResult, Model, AdminTemplate) method is responsible for updating the existing package set details.
     * 
     * @param adminPackageSetTemplate
     * @param result
     * @param model
     * @param adminTemplate
     * @return
     */
    @RequestMapping(value = "/updatePackageSet")
    public String updatePackageSet(@Valid AdminPackageSetTemplate adminPackageSetTemplate, BindingResult result, Model model, AdminTemplate adminTemplate) {
        if (adminPackageSetTemplate == null || adminTemplate == null) {
            model.addAttribute("errorMsg", "Error occurred in updating package set details.");
            return "error";
        }
        try {
            if (!result.hasErrors()) {
                Long packageSetId = adminService.savePackageSet(adminPackageSetTemplate);
                model.addAttribute("adminPackageSetTemplate", adminService.getAdminPackageSetTemplateByPackageSetId(packageSetId));
                model.addAttribute("confirmMessage", "Package Set was updated successfully.");
            }
        } catch (Exception e) {
            logger.error("Exception occurred in updating package set details", e);
            result.addError(new ObjectError("packageSetUpdateError", "There was an error in updating package set details."));
        } finally {
            if (result.hasErrors()) {
                adminPackageSetTemplate.setExhibitList(exhibitService.getAllExhibitsOrderByName());
                adminPackageSetTemplate.setPackageExhibitXrefList(packageService.getPackageExhibitAssociationsByPackageSetId(adminPackageSetTemplate.getPackageSetId()));
                model.addAttribute("adminPackageSetTemplate", adminPackageSetTemplate);
            }
        }
        model.addAttribute("adminTemplate", adminService.getAdminTemplateByPageName(adminTemplate.getPageName()));
        return "admin/main";
    }

    /**
     * packageSetDetails(Model, AdminTemplate, Long) method is responsible for loading details of the selected package set.
     * 
     * @param model
     * @param adminTemplate
     * @param searchPackageSetId
     * @return
     */
    @RequestMapping(value = "/packageSetDetails")
    public String packageSetDetails(Model model, AdminTemplate adminTemplate, Long searchPackageSetId) {
        logger.debug("Loading package set details for package set id {}", searchPackageSetId);
        try {
            model.addAttribute("adminPackageSetTemplate", adminService.getAdminPackageSetTemplateByPackageSetId(searchPackageSetId));
        } catch (Exception e) {
            logger.error("Exception occurred in getting package set details for '" + searchPackageSetId + "'", e);
            model.addAttribute("adminPackageSetTemplate", adminService.getBasicAdminPackageSetTemplate());
            model.addAttribute("hideFieldsForEdit", true);
        }
        model.addAttribute("adminTemplate", adminService.getAdminTemplateByPageName(adminTemplate.getPageName()));
        return "admin/main";
    }

    /**
     * createPackage(AdminPackageTemplate, BindingResult, Model, AdminTemplate) method is responsible for creating a new package type.
     * 
     * @param adminPackageTemplate
     * @param result
     * @param model
     * @param adminTemplate
     * @return
     */
    @RequestMapping(value = "/createPackage")
    public String createPackage(@Valid AdminPackageTemplate adminPackageTemplate, BindingResult result, Model model, AdminTemplate adminTemplate) {
        if (adminPackageTemplate == null || adminTemplate == null) {
            model.addAttribute("errorMsg", "Error occurred in adding new package.");
            return "error";
        }
        try {
            if (!result.hasErrors()) {
                List<Package> packageList = adminService.getPackageDetailsByPackageCode(adminPackageTemplate.getPackageCode());
                if (packageList == null || packageList.isEmpty()) {
                    adminService.savePackage(adminPackageTemplate);
                    model.addAttribute("confirmMessage", "Package '" + adminPackageTemplate.getPackageName() + "' was created successfully.");
                    model.addAttribute("adminPackageTemplate", adminService.getBasicAdminPackageTemplate());
                } else {
                    result.addError(new ObjectError("duplicatePackageError", "The specified Package Code already exists."));
                }
            }
        } catch (Exception e) {
            logger.error("Exception occurred in adding package details", e);
            result.addError(new ObjectError("packageAddError", "There was an error in adding package details."));
        } finally {
            if (result.hasErrors()) {
                adminPackageTemplate.setPackageSetList(packageService.getAllPackageSetsOrderByName());
                model.addAttribute("adminPackageTemplate", adminPackageTemplate);
            }
        }
        model.addAttribute("adminTemplate", adminService.getAdminTemplateByPageName(adminTemplate.getPageName()));
        return "admin/main";
    }

    /**
     * updatePackage(AdminPackageTemplate, BindingResult, Model, AdminTemplate) method is responsible for updating the existing package details.
     * 
     * @param adminPackageTemplate
     * @param result
     * @param model
     * @param adminTemplate
     * @return
     */
    @RequestMapping(value = "/updatePackage")
    public String updatePackage(@Valid AdminPackageTemplate adminPackageTemplate, BindingResult result, Model model, AdminTemplate adminTemplate) {
        if (adminPackageTemplate == null || adminTemplate == null) {
            model.addAttribute("errorMsg", "Error occurred in updating package details.");
            return "error";
        }
        try {
            if (!result.hasErrors()) {
                Long packageId = adminService.savePackage(adminPackageTemplate);
                model.addAttribute("adminPackageTemplate", adminService.getAdminPackageTemplateByPackageId(packageId));
                model.addAttribute("confirmMessage", "Package was updated successfully.");
            }
        } catch (Exception e) {
            logger.error("Exception occurred in updating package details", e);
            result.addError(new ObjectError("packageUpdateError", "There was an error in updating package details."));
        } finally {
            if (result.hasErrors()) {
                adminPackageTemplate.setPackageSetList(packageService.getAllPackageSetsOrderByName());
                adminPackageTemplate.setPackageSetXrefList(packageService.getActivePackageSetAssociationsByPackageId(adminPackageTemplate.getPackageId()));
                model.addAttribute("adminPackageTemplate", adminPackageTemplate);
            }
        }
        model.addAttribute("adminTemplate", adminService.getAdminTemplateByPageName(adminTemplate.getPageName()));
        return "admin/main";
    }

    /**
     * packageDetails(Model, AdminTemplate, Long) method is responsible for loading details of the selected package.
     * 
     * @param model
     * @param adminTemplate
     * @param searchPackageId
     * @return
     */
    @RequestMapping(value = "/packageDetails")
    public String packageDetails(Model model, AdminTemplate adminTemplate, Long searchPackageId) {
        logger.debug("Loading package details for package id {}", searchPackageId);
        try {
            model.addAttribute("adminPackageTemplate", adminService.getAdminPackageTemplateByPackageId(searchPackageId));
        } catch (Exception e) {
            logger.error("Exception occurred in getting package details for '" + searchPackageId + "'", e);
            model.addAttribute("adminPackageTemplate", adminService.getBasicAdminPackageTemplate());
            model.addAttribute("hideFieldsForEdit", true);
        }
        model.addAttribute("adminTemplate", adminService.getAdminTemplateByPageName(adminTemplate.getPageName()));
        return "admin/main";
    }

    /**
     * createExhibit(AdminExhibitTemplate, BindingResult, Model, AdminTemplate) method is responsible for creating a new exhibit and its component associations.
     * 
     * @param adminExhibitTemplate
     * @param result
     * @param model
     * @param adminTemplate
     * @return
     */
    @RequestMapping(value = "/createExhibit")
    public String createExhibit(@Valid AdminExhibitTemplate adminExhibitTemplate, BindingResult result, Model model, AdminTemplate adminTemplate) {
        if (adminExhibitTemplate == null || adminTemplate == null) {
            model.addAttribute("errorMsg", "Error occurred in adding new exhibit.");
            return "error";
        }
        try {
            if (!result.hasErrors()) {
                List<Exhibit> exhibitList = adminService.getExhibitDetailsByExhibitCode(adminExhibitTemplate.getExhibitCode());
                if (exhibitList == null || exhibitList.isEmpty()) {
                    adminService.saveExhibit(adminExhibitTemplate);
                    model.addAttribute("adminExhibitTemplate", adminService.getBasicAdminExhibitTemplate());
                    model.addAttribute("confirmMessage", "Exhibit '" + adminExhibitTemplate.getExhibitName() + "' was created successfully.");
                } else {
                    result.addError(new ObjectError("duplicateExhibitCodeError", "The specified Exhibit Code already exists."));
                }
            }
        } catch (Exception e) {
            logger.error("Exception occurred in adding exhibit details", e);
            result.addError(new ObjectError("exhibitAddError", "There was an error in adding exhibit details."));
        } finally {
            if (result.hasErrors()) {
                adminExhibitTemplate.setComponentList(exhibitService.getAllComponentsOrderByName());
                model.addAttribute("adminExhibitTemplate", adminExhibitTemplate);
            }
        }
        model.addAttribute("adminTemplate", adminService.getAdminTemplateByPageName(adminTemplate.getPageName()));
        return "admin/main";
    }

    /**
     * exhibitDetails(Model, AdminTemplate, Long) method is responsible for loading details of the selected exhibit.
     * 
     * @param model
     * @param adminTemplate
     * @param searchExhibitId
     * @return
     */
    @RequestMapping(value = "/exhibitDetails")
    public String exhibitDetails(Model model, AdminTemplate adminTemplate, Long searchExhibitId) {
        logger.debug("Loading exhibit details for exhibit id {}", searchExhibitId);
        try {
            model.addAttribute("adminExhibitTemplate", adminService.getAdminExhibitTemplateByExhibitId(searchExhibitId));
        } catch (Exception e) {
            logger.error("Exception occurred in getting exhibit details for '" + searchExhibitId + "'", e);
            model.addAttribute("adminExhibitTemplate", adminService.getBasicAdminExhibitTemplate());
            model.addAttribute("hideFieldsForEdit", true);
        }
        model.addAttribute("adminTemplate", adminService.getAdminTemplateByPageName(adminTemplate.getPageName()));
        return "admin/main";
    }

    /**
     * createComponent(AdminComponentTemplate, BindingResult, Model, AdminTemplate) method is responsible for creating a new component.
     * 
     * @param adminComponentTemplate
     * @param result
     * @param model
     * @param adminTemplate
     * @return
     */
    @RequestMapping(value = "/createComponent")
    public String createComponent(@Valid AdminComponentTemplate adminComponentTemplate, BindingResult result, Model model, AdminTemplate adminTemplate) {
        if (adminComponentTemplate == null || adminTemplate == null) {
            model.addAttribute("errorMsg", "Error occurred in adding new component.");
            return "error";
        }
        try {
            if (!result.hasErrors()) {
                List<Component> componentList = adminService.getComponentDetailsByComponentCode(adminComponentTemplate.getComponentCode());
                if (componentList == null || componentList.isEmpty()) {
                    adminService.saveComponent(adminComponentTemplate);
                    model.addAttribute("adminComponentTemplate", adminService.getBasicAdminComponentTemplate());
                    model.addAttribute("confirmMessage", "Component '" + adminComponentTemplate.getComponentName() + "' was created successfully.");
                } else {
                    result.addError(new ObjectError("duplicateComponentCodeError", "The specified Component Code already exists."));
                }
            }
        } catch (Exception e) {
            logger.error("Exception occurred in adding component details", e);
            result.addError(new ObjectError("componentAddError", "There was an error in adding component details."));
        } finally {
            if (result.hasErrors()) {
                model.addAttribute("adminComponentTemplate", adminComponentTemplate);
            }
        }
        model.addAttribute("adminTemplate", adminService.getAdminTemplateByPageName(adminTemplate.getPageName()));
        return "admin/main";
    }

    /**
     * updateExhibit(AdminExhibitTemplate, BindingResult, Model, AdminTemplate) method is responsible for updating the existing exhibit details.
     * 
     * @param adminExhibitTemplate
     * @param result
     * @param model
     * @param adminTemplate
     * @return
     */
    @RequestMapping(value = "/updateExhibit")
    public String updateExhibit(@Valid AdminExhibitTemplate adminExhibitTemplate, BindingResult result, Model model, AdminTemplate adminTemplate) {
        if (adminExhibitTemplate == null || adminTemplate == null) {
            model.addAttribute("errorMsg", "Error occurred in updating exhibit details.");
            return "error";
        }
        try {
            if (!result.hasErrors()) {
                Long exhibitId = adminService.saveExhibit(adminExhibitTemplate);
                model.addAttribute("adminExhibitTemplate", adminService.getAdminExhibitTemplateByExhibitId(exhibitId));
                model.addAttribute("confirmMessage", "Exhibit was updated successfully.");
            }
        } catch (Exception e) {
            logger.error("Exception occurred in updating exhibit details", e);
            result.addError(new ObjectError("exhibitUpdateError", "There was an error in updating exhibit details."));
        } finally {
            if (result.hasErrors()) {
                adminExhibitTemplate.setComponentList(exhibitService.getAllComponentsOrderByName());
                adminExhibitTemplate.setExhibitComponentXrefList(exhibitService.getExhibitComponentAssociationsByExhibitId(adminExhibitTemplate.getExhibitId()));
                model.addAttribute("adminExhibitTemplate", adminExhibitTemplate);
            }
        }
        model.addAttribute("adminTemplate", adminService.getAdminTemplateByPageName(adminTemplate.getPageName()));
        return "admin/main";
    }

    /**
     * updateComponent(AdminComponentTemplate, BindingResult, Model, AdminTemplate) method is responsible for updating existing component details.
     * 
     * @param adminComponentTemplate
     * @param result
     * @param model
     * @param adminTemplate
     * @return
     */
    @RequestMapping(value = "/updateComponent")
    public String updateComponent(@Valid AdminComponentTemplate adminComponentTemplate, BindingResult result, Model model, AdminTemplate adminTemplate) {
        if (adminComponentTemplate == null || adminTemplate == null) {
            model.addAttribute("errorMsg", "Error occurred in updating component details.");
            return "error";
        }
        try {
            if (!result.hasErrors()) {
                Long componentId = adminService.saveComponent(adminComponentTemplate);
                model.addAttribute("adminComponentTemplate", adminService.getAdminComponentTemplateByComponentId(componentId));
                model.addAttribute("confirmMessage", "Component was updated successfully.");
            }
        } catch (Exception e) {
            logger.error("Exception occurred in updating component details", e);
            result.addError(new ObjectError("componentUpdateError", "There was an error in updating component details."));
        } finally {
            if (result.hasErrors()) {
                model.addAttribute("adminComponentTemplate", adminComponentTemplate);
            }
        }
        model.addAttribute("adminTemplate", adminService.getAdminTemplateByPageName(adminTemplate.getPageName()));
        return "admin/main";
    }

    /**
     * componentDetails(Model, AdminTemplate, Long) method is responsible for loading details of the selected component.
     * 
     * @param model
     * @param adminTemplate
     * @param searchComponentId
     * @return
     */
    @RequestMapping(value = "/componentDetails")
    public String componentDetails(Model model, AdminTemplate adminTemplate, Long searchComponentId) {
        logger.debug("Loading component details for component id {}", searchComponentId);
        try {
            model.addAttribute("adminComponentTemplate", adminService.getAdminComponentTemplateByComponentId(searchComponentId));
        } catch (Exception e) {
            logger.error("Exception occurred in getting component details for '" + searchComponentId + "'", e);
            model.addAttribute("adminComponentTemplate", adminService.getBasicAdminComponentTemplate());
            model.addAttribute("hideFieldsForEdit", true);
        }
        model.addAttribute("adminTemplate", adminService.getAdminTemplateByPageName(adminTemplate.getPageName()));
        return "admin/main";
    }
   
}