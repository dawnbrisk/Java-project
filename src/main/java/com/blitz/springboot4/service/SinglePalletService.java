package com.blitz.springboot4.service;

import com.blitz.springboot4.mapper.LocationMapper;
import com.blitz.springboot4.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SinglePalletService {

    @Autowired
    private LocationMapper locationMapper;


     /*
    把下面的往过道上移动 插入
     */
     public void insertAllEmptyLocationOnWay() {
         //得到过道上的所有的空位置
         //location_code : empty_num : current_num
         List<Map<String,Object>> emptyLocation = locationMapper.getEmptyLocation();

         //得到非过道区的只有1托的库位
         //location_code : num : item_code
         List<Map<String, Object>> singlePallet = locationMapper.getSinglePallet();


         singlePallet.removeIf(map -> {
             String locationCode = map.get("location_code").toString();
             int num = locationMapper.isBigPallet(locationCode);
             return num > 0;

         });



         singlePallet.forEach(current -> {
             //if it is moved,skip
             //sku,num 1,from location, isDelete
             int num = locationMapper.isExist(current);
             if (num == 0) {
                 //col
                 String col = current.get("location_code").toString().substring(0,3);
                 // 使用 Stream 过滤并找到第一个匹配的 Map
                 Optional<Map<String, Object>> empty = emptyLocation.stream()
                         .filter(map -> {
                             Object locationCode = map.get("location_code");
                             return locationCode instanceof String && ((String) locationCode).startsWith(col);
                         })
                         .findFirst();
                 empty.ifPresent(map -> {

                     //current location
                     //from
                     locationMapper.insertLocations(CommonUtils.generateUUID(),current.get("item_code").toString(),current.get("location_code").toString(),1,"0","0");
                     //to
                     int currentNum = Integer.parseInt(map.get("current_num").toString());
                     locationMapper.insertLocations(CommonUtils.generateUUID(),current.get("item_code").toString(),map.get("location_code").toString(),currentNum,"0","0");

                     //expected location
                     //from
                     locationMapper.insertLocations(CommonUtils.generateUUID(),current.get("item_code").toString(),current.get("location_code").toString(),0,"1","1");
                     locationMapper.insertLocations(CommonUtils.generateUUID(),current.get("item_code").toString(),map.get("location_code").toString(),currentNum+1,"1","0");
                     //steps
                     locationMapper.insertSteps(CommonUtils.generateUUID(),current.get("item_code").toString(),1,current.get("location_code").toString(),map.get("location_code").toString(),"2");


                 });
             }


         });



     }

    /*
    把过道上的往同类移动 插入
     */
    @Transactional
    public void InsertPalletInWay() {

        //普通库位
        List<Map<String,Object>> generalLocation = locationMapper.getGeneralLocation();
        //过道库位
        List<Map<String,Object>> wayLocation = locationMapper.getPalletInWay();

        wayLocation.forEach(entry -> {
            String sku = entry.get("item_code").toString();
            Optional<Map<String, Object>> result = generalLocation.stream()
                    .filter(map -> sku.equals(map.get("item_code")))
                    .findFirst();
            result.ifPresent(map -> {


                    //current location
                    //过道
                    locationMapper.insertLocations(CommonUtils.generateUUID(),entry.get("item_code").toString(),entry.get("fromLocation").toString(),1,"0","0");
                    //普通
                    locationMapper.insertLocations(CommonUtils.generateUUID(),entry.get("item_code").toString(),map.get("location_code").toString(),Integer.parseInt(map.get("current_num").toString()),"0","0");
                    //expected location
                    //过道
                    locationMapper.insertLocations(CommonUtils.generateUUID(),entry.get("item_code").toString(),entry.get("fromLocation").toString(),0,"1","1");
                    //普通
                    locationMapper.insertLocations(CommonUtils.generateUUID(),entry.get("item_code").toString(),map.get("location_code").toString(),Integer.parseInt(map.get("current_num").toString())+1,"1","0");
                    //steps
                    locationMapper.insertSteps(CommonUtils.generateUUID(),entry.get("item_code").toString(),1,entry.get("fromLocation").toString(),map.get("location_code").toString(),"3");
                    int empty = Integer.parseInt(map.get("num").toString());
                    empty -= 1;
                    map.put("num", empty);
                    if(empty == 0) {
                        // delete
                        generalLocation.removeIf(m->m.get("location_code").equals(map.get("location_code").toString()));
                    }



            });

        });
    }

}
