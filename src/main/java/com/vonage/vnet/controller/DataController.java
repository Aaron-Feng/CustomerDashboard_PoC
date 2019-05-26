package com.vonage.vnet.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;



/**
 * DataController class contains methods for retrieval of data from asynchronous operations across the application.
 * 
 */
@Controller
public class DataController {

    private static final Logger logger = LoggerFactory.getLogger(DataController.class);

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private DDMSService ddmsService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private LookupService lookupService;
    
    /**
     * loadZoneCodesByRegionCode(String) method returns a list of zone codes for the given region code.
     * 
     * @param regionCode
     * @return
     */
    @RequestMapping("zones")
    @ResponseBody
    public List<List<String>> loadZoneCodesByRegionCode(String regionCode) {
        logger.debug("Retrieving zone codes for the selected region code.");
        return dashboardService.getZoneCodeList(regionCode);
    }
    
    /**
     * loadDistrictCodesByRegionZoneCode(String, String) method returns a list of district codes for a given combination of region and zone code..
     * 
     * @param regionCode
     * @param zoneCode
     * @return
     */
    @RequestMapping("districts")
    @ResponseBody
    public List<List<String>> loadDistrictCodesByRegionZoneCode(String regionCode, String zoneCode) {
        logger.debug("Retrieving district codes for the selected region and zone code.");
        return dashboardService.getDistrictCodeList(regionCode, zoneCode);
    }

    /**
     * loadActiveDealersByRegionZoneCode(String, String) method returns a list of active dealers for the given region/zone code combination.
     * 
     * @param regionCode
     * @param zoneCode
     * @return
     */
    @RequestMapping("activeDealers")
    @ResponseBody
    public List<List<String>> loadActiveDealersByRegionZoneCode(String regionCode, String zoneCode) {
        return dashboardService.getDealersByLocation(regionCode, zoneCode, null, true);
    }
    
    /**
     * loadActiveDealersByRegionZoneDistrictCode(String, String, String) method returns a list of active dealers for the given region/zone/district code combination.
     * 
     * @param regionCode
     * @param zoneCode
     * @param districtCode
     * @return
     */
    @RequestMapping("activeDealersByRegionZoneDistrictCode")
    @ResponseBody
    public List<List<String>> loadActiveDealersByRegionZoneDistrictCode(String regionCode, String zoneCode, String districtCode) {
        return dashboardService.getDealersByLocation(regionCode, zoneCode, districtCode, true);
    }
    
    /**
     * loadAORDataByCensusTract(String) method returns a list of AOR data for the given Census Tract.
     * 
     * @param censusTract
     * @return
     */
    @RequestMapping("aorDetails")
    @ResponseBody
    public List<List<String>> loadAORDataByCensusTract(String censusTract) {
        logger.debug("Retrieving AOR Data for the given census tract number.");
        return ddmsService.getAORData(censusTract);
    }

    /**
     * getPackageSetListByName(String) method returns a list of package sets for the given package set name.
     * 
     * @param packageSetName
     * @return List<List<String>>
     */
    @RequestMapping("packageSets")
    @ResponseBody
    public List<List<String>> getPackageSetListByName(String packageSetName) {
        logger.debug("Retrieving the package set list for package set name: {}", packageSetName);
        return adminService.getPackageSetsByName(packageSetName);
    }

    /**
     * getPackageListByName(String) method returns a list of packages for the given package name.
     * 
     * @param packageName
     * @return List<List<String>>
     */
    @RequestMapping("packages")
    @ResponseBody
    public List<List<String>> getPackageListByName(String packageName) {
        logger.debug("Retrieving the package list for package name: {}", packageName);
        return adminService.getPackagesByName(packageName);
    }

    /**
     * getExhibitListByName(String) method returns a list of exhibits for the given exhibit name.
     * 
     * @param exhibitName
     * @return List<List<String>>
     */
    @RequestMapping("exhibits")
    @ResponseBody
    public List<List<String>> getExhibitListByName(String exhibitName) {
        logger.debug("Retrieving the exhibit list for exhibit name: {}", exhibitName);
        return adminService.getExhibitsByName(exhibitName);
    }

    /**
     * getComponentListByName(String) method returns a list of components for the given component name.
     * 
     * @param componentName
     * @return List<List<String>>
     */
    @RequestMapping("components")
    @ResponseBody
    public List<List<String>> getComponentListByName(String componentName) {
        logger.debug("Retrieving the component list for component name: {}", componentName);
        return adminService.getComponentsByName(componentName);
    }

    /**
     * hasPackageSetByCode(String) method is responsible to check if a PackageSet exists for a given packageSetCode.
     * 
     * @param packageSetCode
     * @return
     */
    @RequestMapping("packageSet")
    @ResponseBody
    public boolean hasPackageSetByCode(String packageSetCode) {
        List<PackageSet> packageSetList = adminService.getPackageSetDetailsByPackageSetCode(packageSetCode);
        return (packageSetList != null && !packageSetList.isEmpty()) ? true : false;
    }

    /**
     * hasPackageByCode(String) method is responsible to check if a Package exists for a given packageCode.
     * 
     * @param packageCode
     * @return
     */
    @RequestMapping("package")
    @ResponseBody
    public boolean hasPackageByCode(String packageCode) {
        List<Package> packageList = adminService.getPackageDetailsByPackageCode(packageCode);
        return (packageList != null && !packageList.isEmpty()) ? true : false;
    }

    /**
     * hasExhibitByCode(String) method is responsible to check if an Exhibit exists for a given exhibitCode.
     * 
     * @param exhibitCode
     * @return
     */
    @RequestMapping("exhibit")
    @ResponseBody
    public boolean hasExhibitByCode(String exhibitCode) {
        List<Exhibit> exhibitList = adminService.getExhibitDetailsByExhibitCode(exhibitCode);
        return (exhibitList != null && !exhibitList.isEmpty()) ? true : false;
    }

    /**
     * hasComponentByCode(String) method is responsible to check if a Component exists for a given componentCode.
     * 
     * @param componentCode
     * @return
     */
    @RequestMapping("component")
    @ResponseBody
    public boolean hasComponentByCode(String componentCode) {
        List<Component> componentList = adminService.getComponentDetailsByComponentCode(componentCode);
        return (componentList != null && !componentList.isEmpty()) ? true : false;
    }
    
    /**
     * getBenchmarkOptions() method returns a list of benchmark options.
     * 
     * @return  List<String>
     */
    @RequestMapping("getBenchmarkOptions")
    @ResponseBody
    public List<String> getBenchmarkOptions() {
        return lookupService.getLookupValues(LookupType.BENCHMARK_NAME);
    }

}
