package com.blitz.springboot4.service;

import com.blitz.springboot4.entity.WarehouseLocation;
import com.blitz.springboot4.mapper.LocationMapper;
import com.blitz.springboot4.util.DeepCopyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/*
腾双库位
 */
@Service
public class LocationServicePlus {

    @Autowired
    private LocationMapper locationMapper;

    public List<Map<String, Object>> findAll() {
        List<WarehouseLocation> locations = locationMapper.selectBySomeColumn();

        List<Map<String, Object>> finalList = new ArrayList<>();

        Map<String, List<WarehouseLocation>> todoMap = new HashMap<>();


        List<WarehouseLocation> toDoLocationList = new ArrayList<>();

        for (int index = 0; index < locations.size() - 1; index++) {
            if (locations.get(index).getItemCode().equals(locations.get(index + 1).getItemCode())) {
                toDoLocationList.add(locations.get(index));
            } else {
                if (!toDoLocationList.isEmpty()) {
                    toDoLocationList.add(locations.get(index));
                    todoMap.put(locations.get(index).getItemCode(), DeepCopyUtil.deepCopyByClone(toDoLocationList));
                }
                toDoLocationList.clear();
            }
        }

        //
        List<WarehouseLocation> locationsOrderByLocationCode = locationMapper.selectBySomeColumn();

        //计算
        for (int i = 0; i < locationsOrderByLocationCode.size() - 1; i++) {
            WarehouseLocation location = locationsOrderByLocationCode.get(i);
            WarehouseLocation nextLocation = locationsOrderByLocationCode.get(i + 1);

            String part1 = location.getLocationCode().substring(2, 4); // 从索引2到4提取字符串"02"
            String part2 = nextLocation.getLocationCode().substring(2, 4);

            String part3 = location.getLocationCode().substring(5, 7);
            String part4 = nextLocation.getLocationCode().substring(5, 7);

            int num1 = Integer.parseInt(part3); // 将"02"转换为数值2
            int num2 = Integer.parseInt(part4); // 将"11"转换为数值11

            //如果是同一组，同一列
            if (part1.equals(part2) && ((num1 - 11) / 3 == (num2 - 11) / 3)) {
                //如果该两个SKU可以移动
                if (todoMap.containsKey(location.getItemCode()) && todoMap.containsKey(nextLocation.getItemCode())) {
                    //如果该两个SKU相同
                    if (location.getItemCode().equals(nextLocation.getItemCode())) {
                        //移动第一个
                        List<WarehouseLocation> todoList = todoMap.get(location.getItemCode());
                        String firstPosition = location.getLocationCode();

                        pinToTop(todoList,firstPosition, nextLocation.getLocationCode());//置顶
                        Combine(todoList, finalList);
                        //移除

                        todoMap.remove(location.getItemCode());

                    } else {
                        //如果两个SKU不同
                        //移动第一个
                        List<WarehouseLocation> todoList = todoMap.get(location.getItemCode());
                        String firstPostion = location.getLocationCode();
                        pinToTop(todoList,firstPostion,null);//置顶
                        Combine(todoList, finalList);
                        //移动第二个
                        List<WarehouseLocation> todoList2 = todoMap.get(nextLocation.getItemCode());
                        String secondPostion = nextLocation.getLocationCode();
                        pinToTop(todoList2,secondPostion,null);//置顶
                        Combine(todoList2, finalList);
                        i++;
                        //移除
                        todoMap.remove(location.getItemCode());
                        todoMap.remove(nextLocation.getItemCode());
                        //
//                        System.out.println("空：------------------------");
//                        System.out.print("     "+firstPostion+"-"+secondPostion);
//                        System.out.println("空：------------------------");
                    }

                }
            }
        }


        return finalList;
    }


    //该方法负责合并库位
    //locations 主键是库位号 +托数
    public void Combine(List<WarehouseLocation> locations, List<Map<String, Object>> finalList) {

        //通过深拷贝，复制原库位
        List<WarehouseLocation> originalList = new ArrayList<>();
        locations.forEach(location -> {
            originalList.add(new WarehouseLocation(location));
        });

        Collections.sort(originalList);




        List<String> toDoList = new ArrayList<>();
        int j = locations.size() - 1;
        int i = 0;
        while (i < j) {
            while (j > i) {
                int x = locations.get(i).getPalletNumber() + locations.get(j).getPalletNumber() - 5;
                if (x == 0) {
                    toDoList.add("move " + locations.get(i).getPalletNumber() + " pallets from  " + locations.get(i).getLocationCode() + "  to " + locations.get(j).getLocationCode() + "         ");
                    locations.get(j).setPalletNumber(5);
                    locations.get(i).setPalletNumber(0);
                    i++;
                    j--;
                    continue;
                }
                if (x > 0) {

                    int k = 5 - locations.get(j).getPalletNumber();
                    if (k > 0) {
                        toDoList.add("move " + k + " pallets from  " + locations.get(i).getLocationCode() + " to  " + locations.get(j).getLocationCode() + "         ");
                    }
                    locations.get(i).setPalletNumber(locations.get(i).getPalletNumber() - k);
                    locations.get(j).setPalletNumber(5);
                    j--;
                }

                if (x < 0) {
                    toDoList.add("move " + locations.get(i).getPalletNumber() + " pallets from   " + locations.get(i).getLocationCode() + "  to  " + locations.get(j).getLocationCode() + "         ");

                    locations.get(j).setPalletNumber(locations.get(j).getPalletNumber() + locations.get(i).getPalletNumber());
                    locations.get(i).setPalletNumber(0);

                    i++;
                }
            }

        }

        if (i > 0) {
            Map<String, Object> map1 = new HashMap<>();
            //sku
            String sku = locations.get(0).getItemCode();
            printMap(locations,toDoList,originalList,sku,finalList);

        }

    }

    //置顶
    public void pinToTop(List<WarehouseLocation> locations,String firstPosition,String secondPosition) {

        WarehouseLocation firstElement = new WarehouseLocation();
        WarehouseLocation secondElement = new WarehouseLocation();

        for (WarehouseLocation warehouseLocation : locations) {
            if (firstPosition.equals(warehouseLocation.getLocationCode())) {
                firstElement = warehouseLocation;
            }
            if(secondPosition!=null && secondPosition.equals(warehouseLocation.getLocationCode())) {
                secondElement = warehouseLocation;
            }
        }



        locations.removeIf(obj -> obj != null && firstPosition.equals(obj.getLocationCode()));

        locations.removeIf(obj -> {
            if (secondPosition != null) {
                return obj != null && secondPosition.equals(obj.getLocationCode());
            }
            return false;
        });
        Collections.sort(locations);
        locations.add(0, firstElement);
        if(secondPosition!=null) {
            locations.add(1, secondElement);
        }

    }


    //输出结果
    public void printMap(List<WarehouseLocation> locations,List<String> toDoList,List<WarehouseLocation> originalList,String sku,List<Map<String, Object>> finalList) {
        Map<String, Object> map1 = new HashMap<>();

        //current location
        StringBuilder currentLocation = new StringBuilder();

        for (WarehouseLocation location : originalList) {
            currentLocation.append(location.getLocationCode()).append(" : ").append(location.getPalletNumber()).append(" pallets  \r\n");
        }

        //operation method
        StringBuilder operationMethod = new StringBuilder();

        for (String str : toDoList) {
            operationMethod.append(str).append(" checkbox .");
        }

        //after location
        StringBuilder afterLocation = new StringBuilder();

        for (WarehouseLocation location : locations) {
            afterLocation.append(location.getLocationCode()).append(" : ").append(location.getPalletNumber()).append(" pallets  ");
            if (location.getPalletNumber() == 0) {
                afterLocation.append("zero \r\n");
            } else {
                afterLocation.append(" \r\n");
            }
        }

        map1.put("sku", sku);
        map1.put("currentLocation", currentLocation.toString());
        map1.put("operation", operationMethod.toString());
        map1.put("afterLocation", afterLocation.toString());

        finalList.add(map1);

    }

}
