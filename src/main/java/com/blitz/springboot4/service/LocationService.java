package com.blitz.springboot4.service;


import com.blitz.springboot4.entity.PrefixRange;
import com.blitz.springboot4.entity.WarehouseLocation;
import com.blitz.springboot4.mapper.LocationMapper;
import com.blitz.springboot4.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LocationService {

    @Autowired
    private LocationMapper locationMapper;


    public void clear() {
        //把两个表逻辑清空
        locationMapper.deleteLocations();
        locationMapper.deleteSteps();
    }


    /*
    该方法返回腾库位的两大部分（两个库位的合并，以及挪到过道上）
     */
    @Transactional
    public void mergeLocation() {

        List<WarehouseLocation> locations = locationMapper.selectBySomeColumn();

        List<WarehouseLocation> toDoLocationList = new ArrayList<>();


        for (int index = 0; index < locations.size() - 1; index++) {
            if (locations.get(index).getItemCode().equals(locations.get(index + 1).getItemCode())) {
                toDoLocationList.add(locations.get(index));
            } else {
                if (!toDoLocationList.isEmpty()) {
                    toDoLocationList.add(locations.get(index));

                    // 如果库位相邻，且长度占据两个库位，则从中排除这样的库位。
                    removeAdjacentLocations(toDoLocationList);
                    //

                    Combine(toDoLocationList);
                }
                toDoLocationList.clear();

            }

        }

    }


    //该方法负责合并库位
    public void Combine(List<WarehouseLocation> locations) {
        if (locations.isEmpty()) {
            return;
        }

        //sku
        String sku = locations.get(0).getItemCode();


        //current location insert
        locations.forEach(location -> {
            locationMapper.insertLocations(CommonUtils.generateUUID(), sku, location.getLocationCode(), location.getPalletNumber(), "0", "0");
        });


        Collections.sort(locations);

        List<Map<String, Object>> steps = new ArrayList<>();

        int j = locations.size() - 1;
        int i = 0;
        while (i < j) {
            while (j > i) {
                WarehouseLocation front = locations.get(i);
                WarehouseLocation end = locations.get(j);

                int MaxNumber = 5;

                if (PrefixRange.isInRange(end.getLocationCode())) {
                    MaxNumber = 6;
                }

                int x = front.getPalletNumber() + end.getPalletNumber() - MaxNumber;

                Map<String, Object> map = new HashMap<>();
                map.put("fromLocation", front.getLocationCode());
                map.put("itemCode", front.getItemCode());
                map.put("toLocation", end.getLocationCode());
                map.put("type", "1");

                if (x == 0) {

                    map.put("palletNumber", front.getPalletNumber());

                    steps.add(map);

                    locations.get(j).setPalletNumber(MaxNumber);
                    locations.get(i).setPalletNumber(0);

                    i++;
                    j--;
                    continue;
                }
                if (x > 0) {

                    int k = MaxNumber - end.getPalletNumber();
                    if (k > 0) {
                        map.put("palletNumber", k);
                    }

                    locations.get(i).setPalletNumber(locations.get(i).getPalletNumber() - k);
                    locations.get(j).setPalletNumber(MaxNumber);
                    j--;
                }

                if (x < 0) {
                    map.put("palletNumber", front.getPalletNumber());

                    locations.get(j).setPalletNumber(locations.get(j).getPalletNumber() + locations.get(i).getPalletNumber());
                    locations.get(i).setPalletNumber(0);

                    i++;
                }
                steps.add(map);
            }
        }

        if (i > 0) {

            //steps
            steps.forEach(step -> {
                locationMapper.insertSteps(CommonUtils.generateUUID(), sku, Integer.parseInt(step.get("palletNumber").toString()), step.get("fromLocation").toString(), step.get("toLocation").toString(), step.get("type").toString());
            });


            for (WarehouseLocation location : locations) {

                if (location.getPalletNumber() == 0) {
                    locationMapper.insertLocations(CommonUtils.generateUUID(), sku, location.getLocationCode(), 0, "1", "1");
                } else {
                    locationMapper.insertLocations(CommonUtils.generateUUID(), sku, location.getLocationCode(), location.getPalletNumber(), "1", "0");
                }
            }
        }

    }


    public List<Map<String, Object>> findMixingLocation() {
        return locationMapper.findMixingLocation();
    }


    public Map<String, Object> getLocationListBySku(String sku) {

        List<Map<String, Object>> locations = locationMapper.getLocationListBySku(sku);
        List<Map<String, Object>> steps = locationMapper.getStepsBySku(sku);

        // 按 location_type 0 和 1 分组
        Map<Boolean, List<Map<String, Object>>> partitioned = locations.stream()
                .collect(Collectors.partitioningBy(map -> Integer.parseInt(map.get("location_type").toString()) == 1));


        Map<String, Object> result = new HashMap<>();
        result.put("currentLocation", partitioned.get(false));
        result.put("expectedLocation", partitioned.get(true));
        result.put("steps", steps);

        return result;
    }

    @Transactional
    public void updateFinish(Map<String, Map<String, String>> params) {
        Map<String, String> user = params.get("user");
        //steps
        Map<String, String> steps = params.get("steps");
        steps.forEach((k, v) -> {
            locationMapper.updateSteps(k, v, user.get("username"));
        });


        //locations
        Map<String, String> locations = params.get("expectedLocation");
        locations.forEach((k, v) -> {
            locationMapper.updateLocation(k, v, user.get("username"));
        });

    }


    public List<Map<String, Object>> getAllLocations() {
        return locationMapper.getAllLocations();
    }

    public List<Map<String, Object>> getHistory(String name) {
        return locationMapper.getHistory(name);
    }


    public List<Map<String, Object>> getAllSteps(Map<String, Object> params) {


        if (!params.get("name").toString().isEmpty()) {
            params.put("name", " AND user = '" + params.get("name").toString() + "'");
        } else {
            params.put("name", "");
        }
        if (params.get("dateRange") != null && !params.get("dateRange").toString().isEmpty()) {
            List<String> dateRange = (List<String>) params.getOrDefault("dateRange", Collections.emptyList());

            if (!dateRange.isEmpty()) {
                params.put("dateRange", "AND update_time BETWEEN '" + dateRange.get(0) + "'" + " AND '" + dateRange.get(1) + "'");
            } else {
                params.put("dateRange", "");
            }

        } else {
            params.put("dateRange", "");
        }


        return locationMapper.getAllSteps(params);
    }


    public List<String> getEmptyLocation() {
        //work out and insert empty location : b-22-        -01
        List<Map<String, Object>> emptyLocation = locationMapper.EmptyLocation();
        List<String> locations = new ArrayList<>();
        // get locations next to it .
        for (Map<String, Object> map : emptyLocation) {

            String location = map.get("location").toString();
            BigInteger bigInt = (BigInteger) map.get("group_number");
            int group = bigInt.intValue();


            String[] parts = location.split("-");
            int middle = Integer.parseInt(parts[2]);

            if (group == 0) { //找两边
                //大于它的一边
                parts[2] = String.format("%02d", (middle + 1)); // Format to two digits
                String biggerLocation = String.join("-", parts);

                String resultForBiggerSku = locationMapper.getSku(biggerLocation);
                //小于它的一边
                parts[2] = String.format("%02d", (middle - 1)); // Format to two digits
                String smallerLocation = String.join("-", parts);
                String resultForSmallerSku = locationMapper.getSku(smallerLocation);

                if ("no".equals(resultForBiggerSku) && "no".equals(resultForSmallerSku)) {
                    locations.add(location);
                }


            } else if (group == 1) {//找小于它的一边

                //小于它的一边

                parts[2] = String.format("%02d", (middle - 1)); // Format to two digits
                String smallerLocation = String.join("-", parts);

                String resultForSmallerSku = locationMapper.getSku(smallerLocation);
                if ("no".equals(resultForSmallerSku)) {
                    locations.add(location);
                }

            } else if (group == 2) {//找大于它的一边

                //大于它的一边

                parts[2] = String.format("%02d", (middle + 1)); // Format to two digits
                String biggerLocation = String.join("-", parts);

                String resultForBiggerSku = locationMapper.getSku(biggerLocation);
                if ("no".equals(resultForBiggerSku)) {
                    locations.add(location);
                }

            }

        }
        ;
        //query empty location
        return locations;
    }


    public void skip(String reasonType, String sku) {
        // add it to hidden_list table
        locationMapper.insetSkipSku(sku, reasonType);
        // update the sku
    }

    public void removeAdjacentLocations(List<WarehouseLocation> warehouseLocations) {

        sortByLocationCode(warehouseLocations);

        String locationCode = warehouseLocations.get(0).getLocationCode();
        String result = locationMapper.getSku(locationCode);
        if ("no".equals(result)) {
            return;
        }
        // To avoid ConcurrentModificationException, we must iterate manually and handle removals safely
        List<WarehouseLocation> toRemove = new ArrayList<>();

        for (int i = 0; i < warehouseLocations.size() - 1; i++) {
            WarehouseLocation loc1 = warehouseLocations.get(i);
            WarehouseLocation loc2 = warehouseLocations.get(i + 1);

            if (areAdjacent(loc1.getLocationCode(), loc2.getLocationCode())) {
                // Mark both locations for removal
                i++;
                toRemove.add(loc1);
                toRemove.add(loc2);
                locationMapper.insertBigPallet(loc1.getLocationCode(), loc2.getLocationCode());
            }
        }

        warehouseLocations.removeAll(toRemove);

    }

    private boolean areAdjacent(String loc1Code, String loc2Code) {
        String[] parts1 = loc1Code.split("-");
        String[] parts2 = loc2Code.split("-");

        // Ensure first and last parts are the same
        if (!parts1[0].equals(parts2[0]) || !parts1[3].equals(parts2[3])) {
            return false;
        }

        // Parse the middle numbers
        int firstMiddle1 = Integer.parseInt(parts1[1]);
        int secondMiddle1 = Integer.parseInt(parts1[2]);

        int firstMiddle2 = Integer.parseInt(parts2[1]);
        int secondMiddle2 = Integer.parseInt(parts2[2]);

        // Check adjacency condition: same first middle, second middle differs by 1
        return firstMiddle1 == firstMiddle2 && Math.abs(secondMiddle1 - secondMiddle2) == 1;
    }


    public void sortByLocationCode(List<WarehouseLocation> warehouseLocations) {
        warehouseLocations.sort(new Comparator<WarehouseLocation>() {
            @Override
            public int compare(WarehouseLocation loc1, WarehouseLocation loc2) {
                return loc1.getLocationCode().compareTo(loc2.getLocationCode());
            }
        });
    }


    public  List<Map<String,Object>> getMovingHistory() {
        return locationMapper.getMovingHistory();
    }
}


