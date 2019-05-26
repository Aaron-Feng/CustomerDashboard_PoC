package com.vonage.vnet.servicesImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * This is the service class consisting methods for fetching data from database related to Lookup data.
 */

@Service
public class LookupService {

    @Autowired
    private LookupRepository lookupRepository;

    @Autowired
    private SnetDMSTypeRepository snetDMSTypeRepository;

    /**
     * getLookupValues(LookupType) returns a list of lookup values for the given LookupType.
     * 
     * @param lookupType
     * @return List<String>
     */
    public List<String> getLookupValues(LookupType lookupType) {
        return getLookupValueList(lookupType.getType(), lookupType.getFieldName());
    }

    /**
     * getLookupValueList(String, String) returns a list of lookup values for the given lookup type and the field name.
     * 
     * @param lookupType
     * @param lookupFieldName
     * @return List<String>
     */
    public List<String> getLookupValueList(String lookupType, String lookupFieldName) {
        List<Lookup> lookupList = lookupRepository.findAllActiveByTypeAndFieldName(lookupType, lookupFieldName);

        if (lookupList == null || lookupList.isEmpty()) {
            return null;
        }
        List<String> lookupDataList = new ArrayList<String>();

        if (lookupList.get(0).getSortOrder() == null) {
            Collections.sort(lookupList, new Lookup());
        }

        for (Lookup lookup : lookupList) {
            lookupDataList.add(lookup.getValue());
        }

        return lookupDataList;
    }

    /**
     * getDMSProviderList() returns a list of DMS Providers.
     * 
     * @param
     * @return
     */
    public Map<Long, String> getDMSProviderList() {
        List<SnetDMSTypeView> dmsProvidersList = snetDMSTypeRepository.findDMSProviders();
        Map<Long, String> dmsProvidersMap = new LinkedHashMap<Long, String>();
        for (SnetDMSTypeView approvedDMSProvider : dmsProvidersList) {
            dmsProvidersMap.put(approvedDMSProvider.getId(), approvedDMSProvider.getDmsProvSystem() + " - " + approvedDMSProvider.getDmsApprove());
        }
        return dmsProvidersMap;
    }

    /**
     * getLookupValueMap(LookupType) method is responsible to return a map of the specified lookup type, with ddmsId as key and look up value as value in the map.
     * 
     * @param lookupType
     * @return
     */
    public Map<Long, String> getLookupValueMap(LookupType lookupType) {
        List<Lookup> lookupList = lookupRepository.findAllActiveByTypeAndFieldName(lookupType.getType(), lookupType.getFieldName());

        if (lookupList == null || lookupList.isEmpty()) {
            return null;
        }
        Map<Long, String> lookupDataMap = new HashMap<Long, String>();
        if (lookupList.get(0).getSortOrder() == null) {
            Collections.sort(lookupList, new Lookup());
        }
        for (Lookup lookup : lookupList) {
            lookupDataMap.put(lookup.getDdmsId(), lookup.getValue());
        }

        return lookupDataMap;
    }

    /**
     * getLookupValueMapBySortOrder(LookupType) method is responsible to return a map of the specified lookup type, with sortOrder as key and look up value as value in the map.
     * 
     * @param lookupType
     * @return Map<Integer, String>
     */
    public Map<Integer, String> getLookupValueMapBySortOrder(LookupType lookupType) {
        List<Lookup> lookupList = lookupRepository.findAllActiveByTypeAndFieldName(lookupType.getType(), lookupType.getFieldName());

        if (lookupList == null || lookupList.isEmpty()) {
            return null;
        }
        Map<Integer, String> lookupDataMap = new HashMap<Integer, String>();
        if (lookupList.get(0).getSortOrder() == null) {
            Collections.sort(lookupList, new Lookup());
        }
        for (Lookup lookup : lookupList) {
            lookupDataMap.put(lookup.getSortOrder(), lookup.getValue());
        }

        return lookupDataMap;
    }
    
    /**
     * getFacilityAddressMap(String) method is responsible for returning address types as a map for the given component from DPT_LOOKUP table.
     * 
     * @param addressType
     * @return
     */
    public SortedMap<Long, String> getFacilityAddressMap(String addressType) {
        List<String> addressTypeList = new ArrayList<String>(Arrays.asList(AddressType.ADDITIONAL_STORAGE_VEHICLES.getName(), AddressType.ADDITIONAL_STORAGE_VEHICLES_SECONDARY.getName(),
                                        AddressType.ADDITIONAL_STORAGE_VEHICLES_TERTIARY.getName(), AddressType.ADDITIONAL_STORAGE_PARTS.getName(),
                                        AddressType.ADDITIONAL_STORAGE_PARTS_SECONDARY.getName(), AddressType.ADDITIONAL_STORAGE_PARTS_TERTIARY.getName(),
                                        AddressType.ADDITIONAL_STORAGE_REPAIR.getName(), AddressType.ADDITIONAL_STORAGE_REPAIR_SECONDARY.getName(),
                                        AddressType.ADDITIONAL_STORAGE_REPAIR_TERTIARY.getName()));

        if ("reloPartsService".equalsIgnoreCase(addressType)) {
            addressTypeList.add(AddressType.PARTS_FACILITY.getName());
            addressTypeList.add(AddressType.SERVICE_FACILITY.getName());
            addressTypeList.add(AddressType.PARTS_SHIP_TO.getName());
        }

        List<Lookup> facAddressTypeList = lookupRepository.findAddlFacilityAddressTypes("AddressType", addressTypeList);
        if (facAddressTypeList == null || facAddressTypeList.isEmpty()) {
            return null;
        }
        TreeMap<Long, String> facAddressTypeMap = new TreeMap<Long, String>();
        for (Lookup facAddressType : facAddressTypeList) {
            facAddressTypeMap.put(facAddressType.getDdmsId(), facAddressType.getValue());
        }

        return facAddressTypeMap;
    }
}