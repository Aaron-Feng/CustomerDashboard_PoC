package com.vonage.vnet.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;

import com.subaru.snet.csf.auth.domain.SNETUserContext;
import com.subaru.snet.dpt.domain.LookupType;
import com.subaru.snet.dpt.domain.PackageStatusType;
import com.subaru.snet.dpt.domain.PackageTemplate;
import com.subaru.snet.dpt.entity.PackageRequest;
import com.subaru.snet.dpt.service.DashboardService;
import com.subaru.snet.dpt.service.LookupService;

/**
 * DashboardController class contains methods for handling operations related to Package Dashboard screen.
 */
@Controller
public class DashboardController {
    
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    
    /**
     * initBinder(ServletRequestDataBinder) method is invoked to let date field be blank, because initially if date field is blank an exception is being thrown.
     * 
     * @param binder
     * @throws Exception
     */
    @InitBinder
    protected void initBinder(ServletRequestDataBinder binder) throws Exception {
        CustomDateEditor editor = new CustomDateEditor(new SimpleDateFormat("MM/dd/yyyy"), true);
        binder.registerCustomEditor(Date.class, editor);
    }
    
    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private LookupService lookupService;

    @Autowired
    private SNETUserContext userContext;

    /**
     * packageInquiry(PackageTemplate, Model) method is responsible for retrieving the data to show on Package dashboard screen.
     * 
     * @param packageTemplate
     * @param model
     * @return
     */
    @RequestMapping(value = "/packageInquiry")
    public String packageInquiry(PackageTemplate packageTemplate, Model model) {
        try {
            if (!dashboardService.isAuthorizedUser()) {
                logger.error("Unexpected System Error. Please call the Subarunet Helpdesk for assistance. [DPTUSR: " + userContext.getUser().getUsername() + "]");
                model.addAttribute("errorMsg", "Unexpected System Error. Please call the Subarunet Helpdesk for assistance. [DPTUSR: " + userContext.getUser().getUsername() + "]");
                return "error";
            }
            if (userContext.hasRole("NATIONAL")) {
                model.addAttribute("disableRegion", false);
                model.addAttribute("disableZone", false);
                model.addAttribute("disableDealerNumber", false);
                packageTemplate.setDealerNumber(null);
            } else if (userContext.hasRole("REGION_OFFICE")) {
                model.addAttribute("disableRegion", true);
                model.addAttribute("disableZone", false);
                model.addAttribute("disableDealerNumber", false);
                List<String> zoneCodeList = dashboardService.getZoneCodeListByRegionCode(userContext.getProfile().getRegionCode());
                if (zoneCodeList != null && !zoneCodeList.isEmpty()) {
                    model.addAttribute("zoneCodeList", zoneCodeList);
                }
                packageTemplate.setDealerNumber(null);
            } else if (userContext.hasRole("ZONE_OFFICE")) {
                model.addAttribute("disableRegion", true);
                model.addAttribute("disableZone", true);
                model.addAttribute("disableDealerNumber", false);
                packageTemplate.setDealerNumber(null);
            } else if (userContext.hasRole("DEALER_EXECUTIVE")) {
                model.addAttribute("disableRegion", true);
                model.addAttribute("disableZone", true);
                model.addAttribute("disableDealerNumber", true);
                packageTemplate.setDealerNumber(userContext.getProfile().getDealer().getDealerNumber());
            }
            List<String> packageStatusList = lookupService.getLookupValues(LookupType.PACKAGE_STATUS);
            packageStatusList.remove(packageStatusList.indexOf(PackageStatusType.DELETED.getLabel()));
            model.addAttribute("packageStatusList", packageStatusList);
            model.addAttribute("packageNameMap", dashboardService.getPackageMap());
            model.addAttribute("regionCodeMap", dashboardService.getRegionCodeMap(userContext.getProfile().getRegionCode()));
            model.addAttribute("packageTemplate", packageTemplate);
        } catch (Exception e) {
            logger.error("Exception occurred in retrieving package dashboard data", e);
        }
        return "package/dashboard/dealerPackageSearch";
    }

    /**
     * searchPackage(PackageTemplate, Model) method is responsible for retrieving the package search results with the given parameters.
     * 
     * @param packageTemplate
     * @param model
     * @return String
     */
    @RequestMapping("/searchPackage")
    public String searchPackage(PackageTemplate packageTemplate, Model model) {
        try {
            List<PackageRequest> packageSearchList = dashboardService.getPackageSearchList(packageTemplate);
            // Populate dealership name
            dashboardService.populateDealerName(packageSearchList);
            model.addAttribute("packageSearchResultsList", packageSearchList);
        } catch (Exception e) {
            logger.error("Exception occurred while performing package search", e);
        }
        return "package/dashboard/dealerPackageSearchList";
    }

}