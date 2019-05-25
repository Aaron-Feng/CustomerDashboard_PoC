package com.vonage.vnet.servicesImpl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.subaru.snet.csf.auth.domain.SNETUserContext;
import com.subaru.snet.dpt.domain.AdminComponentTemplate;
import com.subaru.snet.dpt.domain.AdminComponentType;
import com.subaru.snet.dpt.domain.AdminExhibitTemplate;
import com.subaru.snet.dpt.domain.AdminPackageSetTemplate;
import com.subaru.snet.dpt.domain.AdminPackageTemplate;
import com.subaru.snet.dpt.domain.AdminTemplate;
import com.subaru.snet.dpt.entity.Component;
import com.subaru.snet.dpt.entity.Exhibit;
import com.subaru.snet.dpt.entity.ExhibitComponentXref;
import com.subaru.snet.dpt.entity.Package;
import com.subaru.snet.dpt.entity.PackageExhibitXref;
import com.subaru.snet.dpt.entity.PackagePackSetXref;
import com.subaru.snet.dpt.entity.PackageSet;
import com.subaru.snet.dpt.repository.dpt.ComponentRepository;
import com.subaru.snet.dpt.repository.dpt.ExhibitComponentXrefRepository;
import com.subaru.snet.dpt.repository.dpt.ExhibitRepository;
import com.subaru.snet.dpt.repository.dpt.PackageExhibitXrefRepository;
import com.subaru.snet.dpt.repository.dpt.PackagePackSetXrefRepository;
import com.subaru.snet.dpt.repository.dpt.PackageRepository;
import com.subaru.snet.dpt.repository.dpt.PackageSetRepository;

/**
 * AdminService class contains methods for all functionality in admin modules.
 */

@Service
public class AdminService implements BeanFactoryAware {
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    @Autowired
    private PackageService packageService;

    @Autowired
    private ExhibitService exhibitService;

    @Autowired
    private PackageSetRepository packageSetRepository;

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private PackagePackSetXrefRepository packageSetXrefRepository;

    @Autowired
    private PackageExhibitXrefRepository packageExhibitXrefRepository;

    @Autowired
    private ExhibitRepository exhibitRepository;

    @Autowired
    private ExhibitComponentXrefRepository exhibitComponentXrefRepository;

    @Autowired
    private ComponentRepository componentRepository;

    @Autowired
    private SNETUserContext userContext;

    private BeanFactory beanFactory;

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * getAdminForm(String) method is responsible for returning the respective admin form bean from beanFactory for a given reference.
     * 
     * @param reference
     * @return form
     */
    public Object getAdminForm(String reference) {
        Object form = beanFactory.getBean(reference);

        if (form == null) {
            logger.error("Invalid admin form requested {}", reference);
        }

        return form;
    }

    /**
     * getAdminTemplate() method is responsible for creating and returning basic AdminTemplate.
     * 
     * @return
     */
    public AdminTemplate getAdminTemplate() {
        AdminTemplate adminTemplate = new AdminTemplate();
        // Get all admin components
        adminTemplate.setComponents(AdminComponentType.values());
        // Set default tab to display
        adminTemplate.setSelectedPage(AdminComponentType.CREATE_COMPONENT);

        return adminTemplate;
    }

    /**
     * getAdminTemplateByPageName(String) method is responsible for creating admin template based on the component/page (tab) name.
     * 
     * @param pageName
     * @return
     */
    public AdminTemplate getAdminTemplateByPageName(String pageName) {
        AdminTemplate adminTemplate = null;
        if (StringUtils.isBlank(pageName)) {
            adminTemplate = getAdminTemplate();
        } else {
            AdminComponentType selectedComponent = AdminComponentType.getByValue(pageName);
            adminTemplate = new AdminTemplate();
            adminTemplate.setPageName(pageName);
            adminTemplate.setSelectedPage(selectedComponent);
            adminTemplate.setComponents(AdminComponentType.values());
        }

        return adminTemplate;
    }

    /**
     * getBasicAdminPackageSetTemplate() method returns basic AdminPackageSetTemplate object with a list of available exhibits.
     * 
     * @return
     */
    public AdminPackageSetTemplate getBasicAdminPackageSetTemplate() {
        AdminPackageSetTemplate adminPackageSetTemplate = (AdminPackageSetTemplate) getAdminForm("adminPackageSetTemplate");
        if (adminPackageSetTemplate != null) {
            adminPackageSetTemplate.setExhibitList(exhibitService.getAllExhibitsOrderByName());
        } else {
            logger.error("Invalid admin form requested for adminPackageSetTemplate");
        }

        return adminPackageSetTemplate;
    }

    /**
     * getBasicAdminPackageTemplate() method returns basic AdminPackageTemplate object with a list of available package sets.
     * 
     * @return
     */
    public AdminPackageTemplate getBasicAdminPackageTemplate() {
        AdminPackageTemplate adminPackageTemplate = (AdminPackageTemplate) getAdminForm("adminPackageTemplate");
        if (adminPackageTemplate != null) {
            adminPackageTemplate.setPackageSetList(packageService.getAllPackageSetsOrderByName());
        } else {
            logger.error("Invalid admin form requested for adminPackageTemplate");
        }

        return adminPackageTemplate;
    }

    /**
     * getAdminPackageSetTemplateByPackageSetId(Long) method creates AdminPackageSetTemplate object with a list of available package sets along with list of package sets associated with a package.
     * 
     * @param packageSetId
     * @return
     */
    public AdminPackageSetTemplate getAdminPackageSetTemplateByPackageSetId(Long packageSetId) {
        AdminPackageSetTemplate adminPackageSetTemplate = (AdminPackageSetTemplate) getAdminForm("adminPackageSetTemplate");
        if (adminPackageSetTemplate != null) {
            PackageSet packageSet = packageSetRepository.findOne(packageSetId);
            adminPackageSetTemplate.setExhibitList(exhibitService.getAllExhibitsOrderByName());
            adminPackageSetTemplate.setPackageExhibitXrefList(packageService.getPackageExhibitAssociationsByPackageSetId(packageSetId));
            populatePackageSetDetailsFromPackageSet(packageSet, adminPackageSetTemplate);
        } else {
            logger.error("Invalid admin form requested for adminPackageSetTemplate with packageSetId {}", packageSetId);
        }

        return adminPackageSetTemplate;
    }

    /**
     * getAdminPackageTemplateByPackageId(Long) method creates AdminPackageTemplate object with list of available exhibits along with a list of exhibits associated with a package.
     * 
     * @param packageId
     * @return
     */
    public AdminPackageTemplate getAdminPackageTemplateByPackageId(Long packageId) {
        AdminPackageTemplate adminPackageTemplate = (AdminPackageTemplate) getAdminForm("adminPackageTemplate");
        if (adminPackageTemplate != null) {
            Package pack = packageRepository.findOne(packageId);
            adminPackageTemplate.setPackageSetList(packageService.getAllPackageSetsOrderByName());
            adminPackageTemplate.setPackageSetXrefList(packageService.getPackageSetAssociationsByPackageId(packageId));
            populatePackageDetailsFromPackage(pack, adminPackageTemplate);
        } else {
            logger.error("Invalid admin form requested for adminPackageTemplate with packageId {}", packageId);
        }

        return adminPackageTemplate;
    }

    /**
     * populatePackageSetDetailsFromPackageSet(PackageSet, AdminPackageSetTemplate) method is responsible for populating package set details into AdminPackageSetTemplate from PackageSet.
     * 
     * @param packageSet
     * @param adminPackageSetTemplate
     */
    private void populatePackageSetDetailsFromPackageSet(PackageSet packageSet, AdminPackageSetTemplate adminPackageSetTemplate) {
        if (packageSet != null && adminPackageSetTemplate != null) {
            adminPackageSetTemplate.setPackageSetId(packageSet.getId());
            adminPackageSetTemplate.setPackageSetName(packageSet.getName());
            adminPackageSetTemplate.setPackageSetDescription(packageSet.getDescription());
            adminPackageSetTemplate.setPackageSetCode(packageSet.getCode());
            adminPackageSetTemplate.setPackageSetAbbr(packageSet.getAbbreviation());
            adminPackageSetTemplate.setActive(packageSet.getActive());
            adminPackageSetTemplate.setOriginalPackageSetId(packageSet.getOriginalPackageSetId());
            adminPackageSetTemplate.setAgreementType(packageSet.getAgreementType());
            adminPackageSetTemplate.setCreateDate(packageSet.getCreateDate());
            adminPackageSetTemplate.setCreateUser(packageSet.getCreateUser());
        }
    }

    /**
     * populatePackageSetDetailsFromPackageSetTemplate(AdminPackageSetTemplate, PackageSet) method is responsible for populating packageSet details, updating indicators(active_ind, change_ind) and
     * audit columns of original packageSet based on change_ind.
     * 
     * @param adminPackageSetTemplate
     * @param packageSet
     * @return
     */
    private boolean populatePackageSetDetailsFromPackageSetTemplate(AdminPackageSetTemplate adminPackageSetTemplate, PackageSet packageSet) {
        boolean packageSetChanged = false;
        if (adminPackageSetTemplate != null && packageSet != null) {
            Date currentDate = new Date();
            String username = userContext.getUser().getUsername();
            Long packageSetId = adminPackageSetTemplate.getPackageSetId();
            if (packageSetId != null && packageSetId > 0) { // Update base packageSet active and change indicators.
                if (adminPackageSetTemplate.getChanged()) {
                    PackageSet originalPackageSet = packageSetRepository.findOne(adminPackageSetTemplate.getPackageSetId());
                    if (originalPackageSet != null) {
                        originalPackageSet.setActive(false);
                        originalPackageSet.setChanged(true);
                        originalPackageSet.setUpdateDate(currentDate);
                        originalPackageSet.setUpdateUser(username);
                        packageSetRepository.save(originalPackageSet);
                        packageSet.setOriginalPackageSetId(packageSetId); // Id from where clone has been created.
                        packageSetChanged = true;
                    }
                } else { // Update is not required for the packageSet as user has not altered the packageSet data.
                    return packageSetChanged;
                }
            }
            packageSet.setName(adminPackageSetTemplate.getPackageSetName());
            packageSet.setDescription(adminPackageSetTemplate.getPackageSetDescription());
            packageSet.setCode(adminPackageSetTemplate.getPackageSetCode().toLowerCase());
            packageSet.setAbbreviation(adminPackageSetTemplate.getPackageSetAbbr());
            packageSet.setActive(adminPackageSetTemplate.getActive());
            packageSet.setAgreementType(adminPackageSetTemplate.getAgreementType());
            packageSet.setCreateDate(currentDate);
            packageSet.setCreateUser(username);
            packageSet.setUpdateDate(currentDate);
            packageSet.setUpdateUser(username);
        }
        return packageSetChanged;
    }

    /**
     * populatePackageDetailsFromPackage(Package, AdminPackageTemplate) method is responsible for populating package details into AdminPackageTemplate from Package.
     * 
     * @param pack
     * @param adminPackageTemplate
     */
    private void populatePackageDetailsFromPackage(Package pack, AdminPackageTemplate adminPackageTemplate) {
        if (pack != null && adminPackageTemplate != null) {
            adminPackageTemplate.setPackageId(pack.getId());
            adminPackageTemplate.setPackageName(pack.getName());
            adminPackageTemplate.setPackageDescription(pack.getDescription());
            adminPackageTemplate.setPackageCode(pack.getCode());
            adminPackageTemplate.setActiveInd(pack.getActive());
            adminPackageTemplate.setNewDealerPackageInd(pack.getNewDealerPackage());
            adminPackageTemplate.setDataInitLoad(pack.getDataInitLoad());
            adminPackageTemplate.setSoaInitiateInd(pack.getSoaInitiate());
            adminPackageTemplate.setPackageType(pack.getType());
            adminPackageTemplate.setMassInitiateInd(pack.getMassInitiate());
            adminPackageTemplate.setBypassApprovalInd(pack.getBypassApproval());
            adminPackageTemplate.setDealerDashboardOnlyInd(pack.getDealerDashboardOnly());
            adminPackageTemplate.setMultiplePackageInd(pack.getMultiplePackage());
            adminPackageTemplate.setOriginalPackageId(pack.getOriginalPackageId());
            adminPackageTemplate.setCreateDate(pack.getCreateDate());
            adminPackageTemplate.setCreateUser(pack.getCreateUser());
        }
    }

    /**
     * populatePackageDetailsFromPackageTemplate(AdminPackageTemplate, Package) method is responsible for populating package details, updating indicators(active_ind, change_ind) and audit columns of
     * original package based on change_ind.
     * 
     * @param adminPackageTemplate
     * @param pack
     * @return
     */
    private boolean populatePackageDetailsFromPackageTemplate(AdminPackageTemplate adminPackageTemplate, Package pack) {
        boolean packageChanged = false;
        if (adminPackageTemplate != null && pack != null) {
            Date currentDate = new Date();
            String username = userContext.getUser().getUsername();
            Long packageId = adminPackageTemplate.getPackageId();
            if (packageId != null && packageId > 0) {
                if (adminPackageTemplate.getChanged()) {
                    // Update original package active and change indicators.
                    Package originalPackage = packageRepository.findOne(adminPackageTemplate.getPackageId());
                    if (originalPackage != null) {
                        originalPackage.setActive(false);
                        originalPackage.setChanged(true);
                        originalPackage.setUpdateDate(currentDate);
                        originalPackage.setUpdateUser(username);
                        packageRepository.save(originalPackage);
                        pack.setOriginalPackageId(packageId); // Id from where clone has been created.
                        packageChanged = true;
                    }
                } else { // Update is not required for the package as user has not altered the package data.
                    return packageChanged;
                }
            }
            pack.setName(adminPackageTemplate.getPackageName());
            pack.setDescription(adminPackageTemplate.getPackageDescription());
            pack.setCode(adminPackageTemplate.getPackageCode().toLowerCase());
            pack.setActive(adminPackageTemplate.getActiveInd());
            pack.setNewDealerPackage(adminPackageTemplate.getNewDealerPackageInd());
            pack.setDataInitLoad(adminPackageTemplate.getDataInitLoad());
            pack.setSoaInitiate(adminPackageTemplate.getSoaInitiateInd());
            pack.setType(adminPackageTemplate.getPackageType());
            pack.setMassInitiate(adminPackageTemplate.getMassInitiateInd());
            pack.setBypassApproval(adminPackageTemplate.getBypassApprovalInd());
            pack.setDealerDashboardOnly(adminPackageTemplate.getDealerDashboardOnlyInd());
            pack.setMultiplePackage(adminPackageTemplate.getMultiplePackageInd());
            pack.setCreateDate(currentDate);
            pack.setCreateUser(username);
            pack.setUpdateDate(currentDate);
            pack.setUpdateUser(username);
        }
        return packageChanged;
    }

    /**
     * getBasicAdminExhibitTemplate() method is responsible for returning basic AdminExhibitTemplate object with list of available components.
     * 
     * @return adminExhibitTemplate
     */
    public AdminExhibitTemplate getBasicAdminExhibitTemplate() {
        AdminExhibitTemplate adminExhibitTemplate = (AdminExhibitTemplate) getAdminForm("adminExhibitTemplate");
        if (adminExhibitTemplate != null) {
            adminExhibitTemplate.setComponentList(exhibitService.getAllComponentsOrderByName());
            adminExhibitTemplate.setTrackingTypeList(exhibitService.getTrackingTypes());
        } else {
            logger.error("Invalid admin form requested for adminExhibitTemplate");
        }

        return adminExhibitTemplate;
    }

    /**
     * getAdminExhibitTemplateByExhibitId(Long) method is responsible for creating AdminExhibitTemplate object with list of available components along with list of components associated with an
     * exhibit.
     * 
     * @param exhibitId
     * @return
     */
    public AdminExhibitTemplate getAdminExhibitTemplateByExhibitId(Long exhibitId) {
        AdminExhibitTemplate adminExhibitTemplate = (AdminExhibitTemplate) getAdminForm("adminExhibitTemplate");
        if (adminExhibitTemplate != null) {
            Exhibit exhibit = exhibitRepository.findOne(exhibitId);
            adminExhibitTemplate.setComponentList(exhibitService.getAllComponentsOrderByName());
            adminExhibitTemplate.setExhibitComponentXrefList(exhibitService.getExhibitComponentAssociationsByExhibitId(exhibitId));
            adminExhibitTemplate.setTrackingTypeList(exhibitService.getTrackingTypes());
            populateExhibitDetailsFromExhibit(exhibit, adminExhibitTemplate);
        } else {
            logger.error("Invalid admin form requested for adminExhibitTemplate with exhibitId {}", exhibitId);
        }

        return adminExhibitTemplate;
    }

    /**
     * populateExhibitDetailsFromExhibit(Exhibit, AdminExhibitTemplate) method is responsible for populating exhibit details into AdminExhibitTemplate from Exhibit.
     * 
     * @param exhibit
     * @param
     */
    private void populateExhibitDetailsFromExhibit(Exhibit exhibit, AdminExhibitTemplate adminExhibitTemplate) {
        if (exhibit != null && adminExhibitTemplate != null) {
            adminExhibitTemplate.setExhibitId(exhibit.getId());
            adminExhibitTemplate.setExhibitName(exhibit.getName());
            adminExhibitTemplate.setExhibitNumber(exhibit.getNumber());
            adminExhibitTemplate.setExhibitCode(exhibit.getCode());
            adminExhibitTemplate.setExhibitDescription(exhibit.getDescription());
            adminExhibitTemplate.setTemplateName(exhibit.getTemplateName());
            adminExhibitTemplate.setDocumentName(exhibit.getDocumentName());
            adminExhibitTemplate.setFinalTemplateName(exhibit.getFinalTemplateName());
            adminExhibitTemplate.setFinalDocumentName(exhibit.getFinalDocumentName());
            adminExhibitTemplate.setIncludeInFinalInd(exhibit.getIncludeInFinal());
            adminExhibitTemplate.setOriginalExhibitId(exhibit.getOriginalExhibitId());
            adminExhibitTemplate.setCreateDate(exhibit.getCreateDate());
            adminExhibitTemplate.setCreateUser(exhibit.getCreateUser());
            adminExhibitTemplate.setActive(exhibit.getActive());
            adminExhibitTemplate.setAmendmentExhibitCode(exhibit.getAmendmentExhibitCode());
            adminExhibitTemplate.setTrackingType(exhibit.getTrackingTypeId());
            adminExhibitTemplate.setIncludeInAgreementDocsInd(exhibit.getIncludeInAgreementDocsInd());
        }
    }

    /**
     * populateExhibitDetailsFromExhibitTemplate(AdminExhibitTemplate, Exhibit) method is responsible for populating exhibit details, updating indicators(active_ind, change_ind) and audit columns of
     * original exhibit based on change_ind.
     * 
     * @param adminExhibitTemplate
     * @param exhibit
     * @return
     */
    private boolean populateExhibitDetailsFromExhibitTemplate(AdminExhibitTemplate adminExhibitTemplate, Exhibit exhibit) {
        boolean exhibitChanged = false;
        if (adminExhibitTemplate != null && exhibit != null) {
            Date currentDate = new Date();
            String username = userContext.getUser().getUsername();
            Long exhibitId = adminExhibitTemplate.getExhibitId();
            if (exhibitId != null && exhibitId > 0) { // Update base Exhibit active and change indicators.
                if (adminExhibitTemplate.getChanged()) {
                    Exhibit originalExhibit = exhibitRepository.findOne(adminExhibitTemplate.getExhibitId());
                    if (originalExhibit != null) {
                        originalExhibit.setActive(false);
                        originalExhibit.setChanged(true);
                        originalExhibit.setUpdateDate(currentDate);
                        originalExhibit.setUpdateUser(username);
                        exhibitRepository.save(originalExhibit);
                        exhibit.setOriginalExhibitId(exhibitId); // Id from where clone has been created.
                        exhibitChanged = true;
                    }
                } else { // Update is not required for the exhibit as user has not altered the exhibit data.
                    return exhibitChanged;
                }
            }
            exhibit.setName(adminExhibitTemplate.getExhibitName());
            exhibit.setCode(adminExhibitTemplate.getExhibitCode());
            exhibit.setDescription(adminExhibitTemplate.getExhibitDescription());
            exhibit.setNumber(adminExhibitTemplate.getExhibitNumber());
            exhibit.setTemplateName(adminExhibitTemplate.getTemplateName());
            exhibit.setDocumentName(adminExhibitTemplate.getDocumentName());
            exhibit.setFinalDocumentName(adminExhibitTemplate.getFinalDocumentName());
            exhibit.setFinalTemplateName(adminExhibitTemplate.getFinalTemplateName());
            exhibit.setIncludeInFinal(adminExhibitTemplate.getIncludeInFinalInd());
            exhibit.setActive(adminExhibitTemplate.getActive());
            exhibit.setAmendmentExhibitCode(adminExhibitTemplate.getAmendmentExhibitCode());
            exhibit.setTrackingTypeId(adminExhibitTemplate.getTrackingType());
            exhibit.setIncludeInAgreementDocsInd(adminExhibitTemplate.getIncludeInAgreementDocsInd());
            exhibit.setCreateDate(currentDate);
            exhibit.setCreateUser(username);
            exhibit.setUpdateDate(currentDate);
            exhibit.setUpdateUser(username);
        }
        return exhibitChanged;
    }

    /**
     * getPackageSetsByName(String) method is responsible for retrieving a list of package sets based on the given package set name in the ascending order of package set name.
     * 
     * @param packageSetName
     * @return List<List<String>>
     */
    public List<List<String>> getPackageSetsByName(String packageSetName) {
        if (StringUtils.isNotBlank(packageSetName)) {
            packageSetName = packageSetName.toLowerCase().trim().replaceAll("\\*", "%");
        }
        List<PackageSet> packageSetList = packageSetRepository.findAllPackageSetsByName(packageSetName);
        List<List<String>> packageSetInfoList = new ArrayList<List<String>>();
        if (packageSetList == null || packageSetList.isEmpty()) {
            return packageSetInfoList;
        }
        for (PackageSet packageSet : packageSetList) {
            List<String> packageSetInfo = new ArrayList<String>();
            packageSetInfo.add(packageSet.getId().toString());
            packageSetInfo.add(packageSet.getName());
            packageSetInfo.add(packageSet.getDescription());
            packageSetInfoList.add(packageSetInfo);
        }

        return packageSetInfoList;
    }

    /**
     * getPackagesByName(String) method is responsible for retrieving a list of packages for a given package name, in its ascending order.
     * 
     * @param packageName
     * @return List<List<String>>
     */
    public List<List<String>> getPackagesByName(String packageName) {
        if (StringUtils.isNotBlank(packageName)) {
            packageName = packageName.toLowerCase().trim().replaceAll("\\*", "%");
        }
        List<Package> packageList = packageRepository.findAllActivePackagesByName(packageName);
        List<List<String>> packageInfoList = new ArrayList<List<String>>();
        if (packageList == null || packageList.isEmpty()) {
            return packageInfoList;
        }
        for (Package pack : packageList) {
            List<String> packageInfo = new ArrayList<String>();
            packageInfo.add(pack.getId().toString());
            packageInfo.add(pack.getName());
            packageInfo.add(pack.getDescription());
            packageInfoList.add(packageInfo);
        }

        return packageInfoList;
    }

    /**
     * savePackageSet(AdminPackageSetTemplate) method is responsible for saving/updating package set details into DPT_PACK_SET table and to save/update it's exhibit associations into
     * DPT_PACKAGE_EXHIBIT_XREF table.
     * 
     * @param adminPackageSetTemplate
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    @Transactional(value = "dptTransactionManager", rollbackFor = Exception.class)
    public Long savePackageSet(AdminPackageSetTemplate adminPackageSetTemplate) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        Long packageSetId = null;
        if (adminPackageSetTemplate != null) {
            PackageSet packageSet = new PackageSet();
            boolean packageSetChanged = populatePackageSetDetailsFromPackageSetTemplate(adminPackageSetTemplate, packageSet);
            // Save when PackageSet is altered from EditPackageSet or new PackageSet is created from CreatePackageSet.
            packageSetId = (packageSetChanged || adminPackageSetTemplate.getPackageSetId() == null) ? packageSetRepository.save(packageSet).getId() : adminPackageSetTemplate.getPackageSetId();
            // If component is altered(cloned) corresponding Xref(ExhibitComponentXref) record has to be cloned with the cloned ComponentId, same exhibitId, appropriate change and active indicators.
            if (packageSetChanged) {
                clonePackagePackSetXrefInfo(adminPackageSetTemplate.getPackageSetId(), packageSetId);
            }
            // Save package set exhibit associations.
            addPackageExhibitXrefInfo(adminPackageSetTemplate.getPackageExhibitXrefList(), packageSetId, packageSetChanged);
        }
        return packageSetId;
    }

    /**
     * clonePackagePackSetXrefInfo(Long, Long) method is responsible for cloning PackagePackSetXrefs and updating existing/original PackagePackSetXrefs with active_ind='n', change_ind='y' for the
     * provided packageSetId.
     * 
     * @param originalPackageSetId
     * @param packageSetId
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    private void clonePackagePackSetXrefInfo(Long originalPackageSetId, Long packageSetId) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        // Get all PackagePackSetXref(change_ind='n') associations with the given packageSetId, create clone of that Xref with the new packageSetId(PackageSet which is created as part of edit)
        List<PackagePackSetXref> packagePackSetXrefs = packageSetXrefRepository.findByPackageSetIdAndChangedFalse(originalPackageSetId);
        if (packagePackSetXrefs != null && !packagePackSetXrefs.isEmpty()) {
            List<PackagePackSetXref> packagePackSetXrefList = null;
            Date currentDate = new Date();
            String username = userContext.getUser().getUsername();
            for (PackagePackSetXref packagePackSetXref : packagePackSetXrefs) {
                if (packagePackSetXrefList == null) {
                    packagePackSetXrefList = new ArrayList<PackagePackSetXref>();
                }
                // Update existing ExhibitComponentXref active_ind='n', change_ind='y' and audit columns.
                packagePackSetXref.setUpdateDate(currentDate);
                packagePackSetXref.setUpdateUser(username);
                // Create cloned object and update with cloned packageSet details, change_ind='n' and audit columns.
                PackagePackSetXref clonedPackagePackSetXref = (PackagePackSetXref) BeanUtils.cloneBean(packagePackSetXref);
                if (clonedPackagePackSetXref != null) {
                    clonedPackagePackSetXref.setId(null);
                    clonedPackagePackSetXref.setPackageSetId(packageSetId);
                    clonedPackagePackSetXref.setPackageSet(packageSetRepository.findOne(packageSetId));
                    clonedPackagePackSetXref.setPack(packageRepository.findOne(packagePackSetXref.getPackageId()));
                    clonedPackagePackSetXref.setChanged(false);
                    clonedPackagePackSetXref.setCreateDate(currentDate);
                    clonedPackagePackSetXref.setCreateUser(username);
                    clonedPackagePackSetXref.setOriginalPackagePackSetXrefId(packagePackSetXref.getId());
                    packagePackSetXrefList.add(clonedPackagePackSetXref);
                }
                packagePackSetXref.setActive(false);
                packagePackSetXref.setChanged(true);
            }
            packagePackSetXrefList.addAll(packagePackSetXrefs); // Original PackagePackSetXref record with active_ind='n', change_ind='y'.
            if (packagePackSetXrefList != null && !packagePackSetXrefList.isEmpty()) {
                packageSetXrefRepository.save(packagePackSetXrefList);
            }
        }
    }

    /**
     * savePackage(AdminPackageTemplate) method is responsible for saving/updating package details into DPT_PACKAGE table and to save/update it's exhibit associations into DPT_PACKAGE_EXHIBIT_XREF
     * table.
     * 
     * @param adminPackageTemplate
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    @Transactional(value = "dptTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public Long savePackage(AdminPackageTemplate adminPackageTemplate) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        Long packageId = null;
        if (adminPackageTemplate != null) {
            Package pack = new Package();
            boolean packageChanged = populatePackageDetailsFromPackageTemplate(adminPackageTemplate, pack);
            // Save when Package is altered from EditPackage or new Package is created from CreatePackage.
            packageId = (packageChanged || adminPackageTemplate.getPackageId() == null) ? packageRepository.save(pack).getId() : adminPackageTemplate.getPackageId();
            // Save package and packageSet associations.
            addPackageSetXrefInfo(adminPackageTemplate.getPackageSetXrefList(), packageId, packageChanged);
        }
        return packageId;
    }

    /**
     * saveExhibit(AdminExhibitTemplate) method is responsible for saving/updating exhibit details into DPT_EXHIBIT table and to save/update it's component associations into DPT_EXHIBIT_COMPONENT_XREF
     * table.
     * 
     * @param adminExhibitTemplate
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    @Transactional(value = "dptTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public Long saveExhibit(AdminExhibitTemplate adminExhibitTemplate) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        Long exhibitId = null;
        if (adminExhibitTemplate != null) {
            Exhibit exhibit = new Exhibit();
            boolean exhibitChanged = populateExhibitDetailsFromExhibitTemplate(adminExhibitTemplate, exhibit);
            // Save when Exhibit is altered from EditExhibit or new Exhibit is created from CreateExhibit'
            exhibitId = (exhibitChanged || adminExhibitTemplate.getExhibitId() == null) ? exhibitRepository.save(exhibit).getId() : adminExhibitTemplate.getExhibitId();
            // If exhibit is altered(cloned) corresponding Xref(PackageExhibitXref) record has to be cloned with the cloned exhibitId, same packageSet, appropriate change and active indicators.
            if (exhibitChanged) {
                clonePackageExhibitXrefInfo(adminExhibitTemplate.getExhibitId(), exhibitId);
            }
            // Save exhibit and component associations.
            addExhibitComponentXrefInfo(adminExhibitTemplate.getExhibitComponentXrefList(), exhibitId, exhibitChanged);
        }
        return exhibitId;
    }
   
    /**
     * clonePackageExhibitXrefInfo(Long, Long) method is responsible for cloning PackageExhibitXrefs and updating existing/original PackageExhibitXrefs with active_ind='n', change_ind='y' for the provided exhibitId.
     * 
     * @param originalExhibitId
     * @param exhibitId
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    private void clonePackageExhibitXrefInfo(Long originalExhibitId, Long exhibitId) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        // Get all the PackageExhibitXref change_ind='n' associations with the given exhibitId, create clone of that Xref ID with the new exhibitId(Exhibit which is created as part of edit)
        List<PackageExhibitXref> packageExhibitXrefs = packageExhibitXrefRepository.findByExhibitIdAndChangedFalse(originalExhibitId);
        if (packageExhibitXrefs != null && !packageExhibitXrefs.isEmpty()) {
            List<PackageExhibitXref> packageExhibitXrefList = null;
            Date currentDate = new Date();
            String username = userContext.getUser().getUsername();
            for (PackageExhibitXref packageExhibitXref : packageExhibitXrefs) {
                if (packageExhibitXrefList == null) {
                    packageExhibitXrefList = new ArrayList<PackageExhibitXref>();
                }
                // Update existing PackageExhibitXref active_ind='n', change_ind='y', audit columns and originalId
                packageExhibitXref.setUpdateDate(currentDate);
                packageExhibitXref.setUpdateUser(username);
                // Create cloned object and update with cloned exhibitId.
                PackageExhibitXref clonedPackageExhibitXref = (PackageExhibitXref) BeanUtils.cloneBean(packageExhibitXref);
                if (clonedPackageExhibitXref != null) {
                    clonedPackageExhibitXref.setId(null);
                    clonedPackageExhibitXref.setExhibitId(exhibitId);
                    clonedPackageExhibitXref.setExhibit(exhibitRepository.findOne(exhibitId));
                    clonedPackageExhibitXref.setPackageSet(packageSetRepository.findOne(packageExhibitXref.getPackageSetId()));
                    clonedPackageExhibitXref.setChanged(false);
                    clonedPackageExhibitXref.setCreateDate(currentDate);
                    clonedPackageExhibitXref.setCreateUser(username);
                    clonedPackageExhibitXref.setOriginalPackageExhibitXrefId(packageExhibitXref.getId());
                    packageExhibitXrefList.add(clonedPackageExhibitXref);
                }
                packageExhibitXref.setActive(false);
                packageExhibitXref.setChanged(true);
            }
            packageExhibitXrefList.addAll(packageExhibitXrefs); // Original PackageExhibitXref record with active_ind='n', change_ind='y'
            if (packageExhibitXrefList != null && !packageExhibitXrefList.isEmpty()) {
                packageExhibitXrefRepository.save(packageExhibitXrefList);
            }
        }
    }

    /**
     * getExhibitsByName(String) method is responsible for retrieving a list of exhibits for a given exhibit name, in its ascending order.
     * 
     * @param exhibitName
     * @return List<List<String>>
     */
    public List<List<String>> getExhibitsByName(String exhibitName) {
        if (StringUtils.isNotBlank(exhibitName)) {
            exhibitName = exhibitName.toLowerCase().trim().replaceAll("\\*", "%");
        }
        List<Exhibit> exhibitList = exhibitRepository.findExhibitsByName(exhibitName);
        List<List<String>> exhibitInfoList = new ArrayList<List<String>>();
        if (exhibitList == null || exhibitList.isEmpty()) {
            return exhibitInfoList;
        }
        for (Exhibit exhibit : exhibitList) {
            List<String> exhibitInfo = new ArrayList<String>();
            exhibitInfo.add(exhibit.getId().toString());
            exhibitInfo.add(String.valueOf(exhibit.getNumber()));
            exhibitInfo.add(exhibit.getName());
            exhibitInfo.add(exhibit.getDescription());
            exhibitInfoList.add(exhibitInfo);
        }

        return exhibitInfoList;
    }

    /**
     * populateComponentDetailsFromComponentTemplate(AdminComponentTemplate, Component) method is responsible for populating Component details, updating indicators(active_ind, change_ind) and audit columns of
     * original component based on change_ind.
     * 
     * @param adminComponentTemplate
     * @param component
     * @return
     */
    private boolean populateComponentDetailsFromComponentTemplate(AdminComponentTemplate adminComponentTemplate, Component component) {
        boolean componentChanged = false;
        if (adminComponentTemplate != null && component != null) {
            Date currentDate = new Date();
            String username = userContext.getUser().getUsername();
            Long componentId = adminComponentTemplate.getComponentId();
            if (componentId != null && componentId > 0) { // Update base component active and change indicators.
                if (adminComponentTemplate.getChanged()) {
                    Component originalComponent = componentRepository.findOne(componentId);
                    if (originalComponent != null) {
                        originalComponent.setActive(false);
                        originalComponent.setChanged(true);
                        originalComponent.setUpdateUser(username);
                        originalComponent.setUpdateDate(currentDate);
                        componentRepository.save(originalComponent);
                        component.setOriginalComponentId(componentId); // Id from where clone has been created.
                        componentChanged = true;
                    }
                } else { // Update is not required for the component as user has not altered the component data.
                    return componentChanged;
                }
            }
            component.setName(adminComponentTemplate.getComponentName());
            component.setDescription(adminComponentTemplate.getComponentDescription());
            component.setTypeString(adminComponentTemplate.getComponentType().toLowerCase());
            component.setInitialFilename(adminComponentTemplate.getInitialFileName());
            component.setText(adminComponentTemplate.getComponentText());
            component.setCode(adminComponentTemplate.getComponentCode().toLowerCase());
            component.setCreateDate(currentDate);
            component.setCreateUser(username);
            component.setUpdateDate(currentDate);
            component.setUpdateUser(username);
            component.setActive(adminComponentTemplate.getActive());
        }
        return componentChanged;
    }

    /**
     * saveComponent(AdminComponentTemplate) method is responsible for saving/updating component details into DPT_COMPONENT table.
     * 
     * @param adminComponentTemplate
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    @Transactional(value = "dptTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public Long saveComponent(AdminComponentTemplate adminComponentTemplate) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        Long componentId = null;
        if (adminComponentTemplate != null) {
            Component component = new Component();
            boolean componentChanged = populateComponentDetailsFromComponentTemplate(adminComponentTemplate, component);
            // Save when Component is altered from EditComponent or new Component is created from CreateComponent.
            componentId = (componentChanged || adminComponentTemplate.getComponentId() == null) ? componentRepository.save(component).getId() : adminComponentTemplate.getComponentId();
            // If component is altered(cloned) corresponding Xref(ExhibitComponentXref) record has to be cloned with the cloned ComponentId, same exhibitId, appropriate change and active indicators.
            if (componentChanged) {
                cloneExhibitComponentXrefInfo(adminComponentTemplate.getComponentId(), componentId);
            }
        }
        return componentId;
    }

    /**
     * cloneExhibitComponentXrefInfo(Long, Long) method is responsible for cloning ExhibitComponentXrefs and updating existing/original ExhibitComponentXrefs with active_ind='n', change_ind='y' for the provided componentId.
     * 
     * @param originalComponentId
     * @param componentId
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    private void cloneExhibitComponentXrefInfo(Long originalComponentId, Long componentId) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        // Get all the ExhibitComponentXref change_ind='n' associations with the given componentId, create clone of that Xref ID with the new componetID(component which is created as part of edit
        List<ExhibitComponentXref> exhibitComponentXrefs = exhibitComponentXrefRepository.findByComponentIdAndChangedFalse(originalComponentId);
        if (exhibitComponentXrefs != null && !exhibitComponentXrefs.isEmpty()) {
            List<ExhibitComponentXref> exhibitComponentXrefList = null;
            Date currentDate = new Date();
            String username = userContext.getUser().getUsername();
            for (ExhibitComponentXref exhibitComponentXref : exhibitComponentXrefs) {
                if (exhibitComponentXrefList == null) {
                    exhibitComponentXrefList = new ArrayList<ExhibitComponentXref>();
                }
                // Update existing ExhibitComponentXref active_ind='n', change_ind='y', audit columns and originalId
                exhibitComponentXref.setUpdateDate(currentDate);
                exhibitComponentXref.setUpdateUser(username);
                // Create cloned object and update with cloned componentId.
                ExhibitComponentXref clonedExhibitComponentXref = (ExhibitComponentXref) BeanUtils.cloneBean(exhibitComponentXref);
                if (clonedExhibitComponentXref != null) {
                    clonedExhibitComponentXref.setId(null);
                    clonedExhibitComponentXref.setComponentId(componentId);
                    clonedExhibitComponentXref.setComponent(componentRepository.findOne(componentId));
                    clonedExhibitComponentXref.setExhibit(exhibitRepository.findOne(exhibitComponentXref.getExhibitId()));
                    clonedExhibitComponentXref.setChanged(false);
                    clonedExhibitComponentXref.setCreateDate(currentDate);
                    clonedExhibitComponentXref.setCreateUser(username);
                    clonedExhibitComponentXref.setOriginalExhibitComponentXrefId(exhibitComponentXref.getId());
                    exhibitComponentXrefList.add(clonedExhibitComponentXref);
                }
                exhibitComponentXref.setActive(false);
                exhibitComponentXref.setChanged(true);
            }
            exhibitComponentXrefList.addAll(exhibitComponentXrefs); // Original ExhibitComponentXref record with active_ind='n', change_ind='y'
            if (exhibitComponentXrefList != null && !exhibitComponentXrefList.isEmpty()) {
                exhibitComponentXrefRepository.save(exhibitComponentXrefList);
            }
        }
    }

    /**
     * getBasicAdminComponentTemplate() method is responsible for returning basic AdminComponentTemplate.
     * 
     * @return
     */
    public AdminComponentTemplate getBasicAdminComponentTemplate() {
        AdminComponentTemplate adminComponentTemplate = (AdminComponentTemplate) getAdminForm("adminComponentTemplate");
        if (adminComponentTemplate == null) {
            logger.error("Invalid admin form requested for adminComponentTemplate");
        }

        return adminComponentTemplate;
    }

    /**
     * getAdminComponentTemplateByComponentId(Long) method is responsible for creating AdminComponentTemplate object with list of available components.
     * 
     * @param componentId
     * @return
     */
    public AdminComponentTemplate getAdminComponentTemplateByComponentId(Long componentId) {
        Component component = componentRepository.findOne(componentId);
        AdminComponentTemplate adminComponentTemplate = (AdminComponentTemplate) getAdminForm("adminComponentTemplate");
        if (component != null && adminComponentTemplate != null) {
            adminComponentTemplate.setComponentId(component.getId());
            adminComponentTemplate.setComponentName(component.getName());
            adminComponentTemplate.setComponentDescription(component.getDescription());
            adminComponentTemplate.setComponentType(component.getTypeString().toUpperCase());
            adminComponentTemplate.setComponentCode(component.getCode());
            adminComponentTemplate.setInitialFileName(component.getInitialFilename());
            adminComponentTemplate.setComponentText(component.getText());
            adminComponentTemplate.setOriginalComponentId(component.getOriginalComponentId());
            adminComponentTemplate.setCreateDate(component.getCreateDate());
            adminComponentTemplate.setCreateUser(component.getCreateUser());
            adminComponentTemplate.setActive(component.getActive());
        } else {
            logger.error("Invalid admin form requested for adminComponentTemplate with componentId {}", componentId);
        }

        return adminComponentTemplate;
    }

    /**
     * getComponentsByName(String) method is responsible for retrieving a list of components for a given component name, in its ascending order.
     * 
     * @param componentName
     * @return List<List<String>>
     */
    public List<List<String>> getComponentsByName(String componentName) {
        if (StringUtils.isNotBlank(componentName)) {
            componentName = componentName.toLowerCase().trim().replaceAll("\\*", "%");
        }
        List<Component> componentList = componentRepository.findComponentsByName(componentName);
        List<List<String>> componentInfoList = new ArrayList<List<String>>();
        if (componentList == null || componentList.isEmpty()) {
            return componentInfoList;
        }
        for (Component component : componentList) {
            List<String> componentInfo = new ArrayList<String>();
            componentInfo.add(component.getId().toString());
            componentInfo.add(component.getName());
            componentInfo.add(component.getTypeString());
            componentInfo.add(component.getDescription());
            componentInfoList.add(componentInfo);
        }

        return componentInfoList;
    }

    /**
     * getExhibitDetailsByExhibitCode(String) method is responsible for retrieving exhibit details based on the given exhibit code, if exists.
     * 
     * @param exhibitCode
     * @return List<Exhibit>
     */
    public List<Exhibit> getExhibitDetailsByExhibitCode(String exhibitCode) {
        List<Exhibit> exhibitList = null;
        try {
            exhibitList = exhibitRepository.findByCode(exhibitCode);
        } catch (Exception e) {
            logger.error("Exception occurred in retrieving exhibit details for exhibit code '" + exhibitCode + "'", e);
        }
        return exhibitList;
    }

    /**
     * getComponentDetailsByComponentCode(String) method is responsible for retrieving component details based on the given component code, if exists.
     * 
     * @param componentCode
     * @return List<Component>
     */
    public List<Component> getComponentDetailsByComponentCode(String componentCode) {
        List<Component> componentList = null;
        try {
            if (StringUtils.isNotBlank(componentCode)) {
                componentList = componentRepository.findByCode(componentCode);
            }
        } catch (Exception e) {
            logger.error("Exception occurred in retrieving component details for component code '" + componentCode + "'", e);
        }
        return componentList;
    }

    /**
     * getPackageSetDetailsByPackageSetCode(String) method is responsible for retrieving package set details based on the given package set code, if exists.
     * 
     * @param packageSetCode
     * @return List<PackageSet>
     */
    public List<PackageSet> getPackageSetDetailsByPackageSetCode(String packageSetCode) {
        List<PackageSet> packageSetList = null;
        try {
            if (StringUtils.isNotBlank(packageSetCode)) {
                packageSetList = packageSetRepository.findByCode(packageSetCode);
            }
        } catch (Exception e) {
            logger.error("Exception occurred in retrieving package set details for package set code '" + packageSetCode + "'", e);
        }
        return packageSetList;
    }

    /**
     * getPackageDetailsByPackageCode(String) method is responsible for retrieving package details based on the given package code, if exists.
     * 
     * @param packageCode
     * @return List<Package>
     */
    public List<Package> getPackageDetailsByPackageCode(String packageCode) {
        List<Package> packageList = null;
        try {
            if (StringUtils.isNotBlank(packageCode)) {
                packageList = packageRepository.findByCode(packageCode);
            }
        } catch (Exception e) {
            logger.error("Exception occurred in retrieving package details for package code '" + packageCode + "'", e);
        }
        return packageList;
    }

    /**
     * addPackageSetXrefInfo(List<PackagePackSetXref>, Long, boolean) method is responsible for populating/cloning packagePackSetXref details with appropriate indicators(based on change indicator) and
     * saving packagePackSetXref data.
     * 
     * @param packagePackSetXrefList
     * @param packageId
     * @param packageChanged
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    public void addPackageSetXrefInfo(List<PackagePackSetXref> packagePackSetXrefList, Long packageId, boolean packageChanged) throws IllegalAccessException, InstantiationException,
                                    InvocationTargetException, NoSuchMethodException {
        List<PackagePackSetXref> packagePackSetXrefs = null;
        if (packagePackSetXrefList != null && !packagePackSetXrefList.isEmpty() && packageId != null && packageId > 0) {
            if (packagePackSetXrefs == null) {
                packagePackSetXrefs = new ArrayList<PackagePackSetXref>();
            }
            List<PackagePackSetXref> unreferencedPackSetXrefs = new ArrayList<PackagePackSetXref>();
            Date currentDate = new Date();
            String username = userContext.getUser().getUsername();
            for (PackagePackSetXref packagePackSetXref : packagePackSetXrefList) {
                Long packagePackSetXrefId = packagePackSetXref.getId();
                if (packagePackSetXrefId != null && packagePackSetXrefId > 0) { // From Edit screen.
                    if (packageChanged) { // Clone of PackagePackSetXref(child) has to be created as Package(parent) has changed(package changeInd checked).
                        // Latest changes from admin module have to be taken when creating clone, as PackagePackSetXref changeInd is checked.
                        if (packagePackSetXref.getChanged() != null && packagePackSetXref.getChanged()) {
                            populatePackagePackSetXrefDetails(packagePackSetXref, packageId, currentDate, username);
                        } else { // Latest changes from admin module have to be discarded and clone has to be created from existing packageSetXref with appropriate indicators.
                            PackagePackSetXref clonedPackagePackSetXref = (PackagePackSetXref) BeanUtils.cloneBean(packageSetXrefRepository.findOne(packagePackSetXrefId));
                            if (clonedPackagePackSetXref != null) {
                                populatePackagePackSetXrefDetails(clonedPackagePackSetXref, packageId, currentDate, username);
                                packagePackSetXrefs.add(clonedPackagePackSetXref);
                                unreferencedPackSetXrefs.add(packagePackSetXref);
                            }
                        }
                    } else { // Clone of only changed PackagePackSetXref has to be created as Package(parent) is not changed.
                        // Latest changes from admin module have to be taken when creating clone, as PackagePackSetXref changeInd is checked.
                        if (packagePackSetXref.getChanged() != null && packagePackSetXref.getChanged()) {
                            populatePackagePackSetXrefDetails(packagePackSetXref, packageId, currentDate, username);

                            // Update original record with active and change indicators.
                            packagePackSetXrefs.add(populateOriginalPackagePackSetXrefDetails(packagePackSetXrefId, currentDate, username));
                        } else { // Skip this record from save as parent is not changed and PackagePackSetXref changeInd is not checked.
                            unreferencedPackSetXrefs.add(packagePackSetXref);
                        }
                    }
                } else { // Create PackageSet or new PackagePackSetXref has been associated to packageSet from Edit Screen.
                    populatePackagePackSetXrefDetails(packagePackSetXref, packageId, currentDate, username);
                }
            }
            if (!unreferencedPackSetXrefs.isEmpty()) {
                packagePackSetXrefList.removeAll(unreferencedPackSetXrefs); // Remove all unchanged PackSetXrefs.
            }
            packagePackSetXrefs.addAll(packagePackSetXrefList); // Existing PackagePackSetXref record with updated audit columns and indicators.
            if (packagePackSetXrefs != null && !packagePackSetXrefs.isEmpty()) {
                packageSetXrefRepository.save(packagePackSetXrefs);
            }
        }
    }

    /**
     * populateOriginalPackagePackSetXrefDetails(Long, Date, String) method is responsible for updating original(Cloned record is created from this original record) packagePackSetXref details.
     * 
     * @param packagePackSetXrefId
     * @param currentDate
     * @param username
     * @return
     */
    private PackagePackSetXref populateOriginalPackagePackSetXrefDetails(Long packagePackSetXrefId, Date currentDate, String username) {
        // Update original packagePackSetXrefId active_ind='n' and change_ind='y'
        PackagePackSetXref originalPackagePackSetXref = packageSetXrefRepository.findOne(packagePackSetXrefId);
        if (originalPackagePackSetXref != null) {
            originalPackagePackSetXref.setActive(false);
            originalPackagePackSetXref.setChanged(true);
            originalPackagePackSetXref.setUpdateDate(currentDate);
            originalPackagePackSetXref.setUpdateUser(username);
        }
        return originalPackagePackSetXref;
    }

    /**
     * populatePackagePackSetXrefDetails(PackagePackSetXref, Long, Date, String) method is responsible for updating PackagePackSetXref details.
     * 
     * @param packagePackSetXref
     * @param packageId
     * @param currentDate
     * @param username
     */
    private void populatePackagePackSetXrefDetails(PackagePackSetXref packagePackSetXref, Long packageId, Date currentDate, String username) {
        packagePackSetXref.setOriginalPackagePackSetXrefId(packagePackSetXref.getId());
        packagePackSetXref.setId(null);
        packagePackSetXref.setChanged(false);
        packagePackSetXref.setPackageId(packageId);
        packagePackSetXref.setPack(packageRepository.findOne(packagePackSetXref.getPackageId()));
        packagePackSetXref.setPackageSet(packageSetRepository.findOne(packagePackSetXref.getPackageSetId()));
        packagePackSetXref.setCreateDate(currentDate);
        packagePackSetXref.setCreateUser(username);
        packagePackSetXref.setUpdateDate(currentDate);
        packagePackSetXref.setUpdateUser(username);
    }

    /**
     * addPackageExhibitXrefInfo(List<PackageExhibitXref>, Long, boolean) method is responsible for populating/cloning packageExhibitXref details with appropriate indicators(based on change indicator)
     * and saving packageExhibitXref data.
     * 
     * @param packageExhibitXrefList
     * @param packageSetId
     * @param packageSetChanged
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    public void addPackageExhibitXrefInfo(List<PackageExhibitXref> packageExhibitXrefList, Long packageSetId, boolean packageSetChanged) throws IllegalAccessException, InstantiationException,
                                    InvocationTargetException, NoSuchMethodException {
        List<PackageExhibitXref> packageExhibitXrefs = null;
        if (packageExhibitXrefList != null && !packageExhibitXrefList.isEmpty() && packageSetId != null && packageSetId > 0) {
            if (packageExhibitXrefs == null) {
                packageExhibitXrefs = new ArrayList<PackageExhibitXref>();
            }
            List<PackageExhibitXref> unreferencedPackageExhibitXrefs = new ArrayList<PackageExhibitXref>();
            Date currentDate = new Date();
            String username = userContext.getUser().getUsername();
            for (PackageExhibitXref packageExhibitXref : packageExhibitXrefList) {
                Long packageExhibitXrefId = packageExhibitXref.getId();
                if (packageExhibitXrefId != null && packageExhibitXrefId > 0) { // From Edit screen.
                    if (packageSetChanged) { // Clone of PackageExhibitXref(child) has to be created as PackageSet(parent) has changed(packageSet changeInd checked).
                        // Latest changes from admin module have to be taken when creating clone, as PackageExhibitXref changeInd is checked.
                        if (packageExhibitXref.getChanged() != null && packageExhibitXref.getChanged()) {
                            populatePackageExhibitXrefDetails(packageExhibitXref, packageSetId, currentDate, username);
                        } else { // Latest changes from admin module have to be discarded and clone has to be created from existing PackageExhibitXref with appropriate indicators.
                            PackageExhibitXref clonedPackageExhibitXref = (PackageExhibitXref) BeanUtils.cloneBean(packageExhibitXrefRepository.findOne(packageExhibitXrefId));
                            if (clonedPackageExhibitXref != null) {
                                populatePackageExhibitXrefDetails(clonedPackageExhibitXref, packageSetId, currentDate, username);
                                packageExhibitXrefs.add(clonedPackageExhibitXref); // Adding the cloned record to be saved.
                                unreferencedPackageExhibitXrefs.add(packageExhibitXref); // Current packageExhibitXref has to be removed from list of xrefs which will be saved.
                            }
                        }
                    } else { // Clone of only changed packageExhibitXref has to be created as PackageSet(parent) is not changed.
                        // Latest changes from admin module have to be taken when creating clone, as packageExhibitXref changeInd is checked.
                        if (packageExhibitXref.getChanged() != null && packageExhibitXref.getChanged()) {
                            populatePackageExhibitXrefDetails(packageExhibitXref, packageSetId, currentDate, username);

                            // Update original record with active and change indicators.
                            packageExhibitXrefs.add(populateOriginalPackageExhibitXrefDetails(packageExhibitXrefId, currentDate, username));
                        } else { // Skip this record from save as parent is not changed and PackageExhibitXref changeInd is not checked.
                            unreferencedPackageExhibitXrefs.add(packageExhibitXref);
                        }
                    }
                } else { // Create PackageSet or new PackageExhibitXref has been associated to packageSet from Edit Screen.
                    populatePackageExhibitXrefDetails(packageExhibitXref, packageSetId, currentDate, username);
                }
            }
            if (!unreferencedPackageExhibitXrefs.isEmpty()) {
                packageExhibitXrefList.removeAll(unreferencedPackageExhibitXrefs); // Remove all unchanged PackageExhibitXref.
            }
            packageExhibitXrefs.addAll(packageExhibitXrefList); // Existing PackageExhibitXrefs record with updated audit columns and indicators.
            if (packageExhibitXrefs != null && !packageExhibitXrefs.isEmpty()) {
                packageExhibitXrefRepository.save(packageExhibitXrefs);
            }
        }
    }

    /**
     * populatePackageExhibitXrefDetails(PackageExhibitXref, Long, Date, String) method is responsible for updating packageExhibitXref details.
     * 
     * @param packageExhibitXref
     * @param packageSetId
     * @param currentDate
     * @param username
     */
    private void populatePackageExhibitXrefDetails(PackageExhibitXref packageExhibitXref, Long packageSetId, Date currentDate, String username) {
        packageExhibitXref.setOriginalPackageExhibitXrefId(packageExhibitXref.getId());
        packageExhibitXref.setId(null);
        packageExhibitXref.setChanged(false);
        packageExhibitXref.setPackageSetId(packageSetId);
        packageExhibitXref.setPackageSet(packageSetRepository.findOne(packageSetId));
        packageExhibitXref.setExhibit(exhibitRepository.findOne(packageExhibitXref.getExhibitId()));
        packageExhibitXref.setCreateDate(currentDate);
        packageExhibitXref.setCreateUser(username);
        packageExhibitXref.setUpdateDate(currentDate);
        packageExhibitXref.setUpdateUser(username);
    }

    /**
     * populateOriginalPackageExhibitXrefDetails(Long, Date, String) method is responsible for updating original(Cloned record is created from this base record) packageExhibitXref details.
     * 
     * @param packageExhibitXrefId
     * @param currentDate
     * @param username
     * @return
     */
    private PackageExhibitXref populateOriginalPackageExhibitXrefDetails(Long packageExhibitXrefId, Date currentDate, String username) {
        // Update existing PackageExhibitXref active_ind='n' and change_ind='y'
        PackageExhibitXref originalPackageExhibitXref = packageExhibitXrefRepository.findOne(packageExhibitXrefId);
        if (originalPackageExhibitXref != null) {
            originalPackageExhibitXref.setActive(false);
            originalPackageExhibitXref.setChanged(true);
            originalPackageExhibitXref.setUpdateDate(currentDate);
            originalPackageExhibitXref.setUpdateUser(username);
        }
        return originalPackageExhibitXref;
    }


    /**
     * addExhibitComponentXrefInfo(List<ExhibitComponentXref>, Long, boolean) method is responsible for populating exhibitComponentXref details with appropriate indicators(based on change indicator)
     * and saving exhibitComponentXref data.
     * 
     * @param exhibitComponentXrefList
     * @param exhibitId
     * @param exhibitChanged
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    public void addExhibitComponentXrefInfo(List<ExhibitComponentXref> exhibitComponentXrefList, Long exhibitId, boolean exhibitChanged) throws IllegalAccessException, InstantiationException,
                                    InvocationTargetException, NoSuchMethodException {
        List<ExhibitComponentXref> exhibitComponentXrefs = null;
        if (exhibitComponentXrefList != null && !exhibitComponentXrefList.isEmpty() && exhibitId != null && exhibitId > 0) {
            if (exhibitComponentXrefs == null) {
                exhibitComponentXrefs = new ArrayList<ExhibitComponentXref>();
            }
            List<ExhibitComponentXref> unreferencedExhibitComponentXrefs = new ArrayList<ExhibitComponentXref>();
            Date currentDate = new Date();
            String username = userContext.getUser().getUsername();
            for (ExhibitComponentXref exhibitComponentXref : exhibitComponentXrefList) {
                Long exhibitComponentXrefId = exhibitComponentXref.getId();
                if (exhibitComponentXrefId != null && exhibitComponentXrefId > 0) { // From Edit screen.
                    if (exhibitChanged) { // Clone of ExhibitComponentXref(child) has to be created as Exhibit(parent) has changed(Exhibit changeInd checked).
                        // Latest changes from admin module have to be taken when creating clone, as ExhibitComponentXref changeInd is checked.
                        if (exhibitComponentXref.getChanged() != null && exhibitComponentXref.getChanged()) {
                            populateExhibitComponentXrefDetails(exhibitComponentXref, exhibitId, currentDate, username);
                        } else { // Latest changes from admin module have to be discarded and clone has to be created from existing ExhibitComponentXref with appropriate indicators.
                            ExhibitComponentXref clonedExhibitComponentXref = (ExhibitComponentXref) BeanUtils.cloneBean(exhibitComponentXrefRepository.findOne(exhibitComponentXrefId));
                            if (clonedExhibitComponentXref != null) {
                                populateExhibitComponentXrefDetails(clonedExhibitComponentXref, exhibitId, currentDate, username);
                                exhibitComponentXrefs.add(clonedExhibitComponentXref); // Adding the cloned record to be saved.
                                unreferencedExhibitComponentXrefs.add(exhibitComponentXref); // Current exhibitComponentXref has to be removed from list of xrefs which will be saved.
                            }
                        }
                    } else { // Clone of only changed ExhibitComponentXref has to be created as Exhibit(parent) is not changed.
                        // Latest changes from admin module have to be taken when creating clone, as ExhibitComponentXref changeInd is checked.
                        if (exhibitComponentXref.getChanged() != null && exhibitComponentXref.getChanged()) {
                            populateExhibitComponentXrefDetails(exhibitComponentXref, exhibitId, currentDate, username);
                            
                            // Update original records active and change indicators.
                            exhibitComponentXrefs.add(populateOriginalExhibitComponentXrefDetails(exhibitComponentXrefId, currentDate, username));
                        } else { // Skip this record from save as parent is not changed and ExhibitComponentXref changeInd is not checked.
                            unreferencedExhibitComponentXrefs.add(exhibitComponentXref);
                        }
                    }
                } else { // Create Exhibit or new ExhibitComponentXref has been associated to Exhibit from Edit Screen.
                    populateExhibitComponentXrefDetails(exhibitComponentXref, exhibitId, currentDate, username);
                }
            }
            if (!unreferencedExhibitComponentXrefs.isEmpty()) {
                exhibitComponentXrefList.removeAll(unreferencedExhibitComponentXrefs); // Remove all unchanged ExhibitComponentXref.
            }
            exhibitComponentXrefs.addAll(exhibitComponentXrefList); // Existing ExhibitComponentXref record with updated audit columns and indicators.
            if (exhibitComponentXrefs != null && !exhibitComponentXrefs.isEmpty()) {
                exhibitComponentXrefRepository.save(exhibitComponentXrefs);
            }
        }
    }

    /**
     * populateOriginalExhibitComponentXrefDetails(Long, Date, String) method is responsible for updating original(Cloned record is created from this base record) exhibitComponentXref details.
     * 
     * @param exhibitComponentXrefId
     * @param currentDate
     * @param username
     * @return
     */
    private ExhibitComponentXref populateOriginalExhibitComponentXrefDetails(Long exhibitComponentXrefId, Date currentDate, String username) {
        ExhibitComponentXref originalExhibitComponentXref = exhibitComponentXrefRepository.findOne(exhibitComponentXrefId);
        if (originalExhibitComponentXref != null) {
            originalExhibitComponentXref.setActive(false);
            originalExhibitComponentXref.setChanged(true);
            originalExhibitComponentXref.setUpdateDate(currentDate);
            originalExhibitComponentXref.setUpdateUser(username);
        }
        return originalExhibitComponentXref;
    }

    /**
     * populateExhibitComponentXrefDetails(ExhibitComponentXref, Long, Date, String) method is responsible for updating exhibitComponentXref details.
     * 
     * @param exhibitComponentXref
     * @param exhibitId
     * @param currentDate
     * @param username
     */
    private void populateExhibitComponentXrefDetails(ExhibitComponentXref exhibitComponentXref, Long exhibitId, Date currentDate, String username) {
        exhibitComponentXref.setOriginalExhibitComponentXrefId(exhibitComponentXref.getId());
        exhibitComponentXref.setId(null);
        exhibitComponentXref.setChanged(false);
        exhibitComponentXref.setExhibitId(exhibitId);
        exhibitComponentXref.setExhibit(exhibitRepository.findOne(exhibitId));
        exhibitComponentXref.setComponent(componentRepository.findOne(exhibitComponentXref.getComponentId()));
        exhibitComponentXref.setCreateDate(currentDate);
        exhibitComponentXref.setCreateUser(username);
        exhibitComponentXref.setUpdateDate(currentDate);
        exhibitComponentXref.setUpdateUser(username);
    }

}