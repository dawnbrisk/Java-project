package com.blitz.springboot4.service;

import com.blitz.springboot4.entity.PickingItem;
import com.blitz.springboot4.mapper.PickingItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UploadService {


    @Autowired
    private PickingItemMapper pickingItemMapper;

    @Transactional
    public void handleUpload( List<PickingItem> pickingItems) {
        List<PickingItem> sortedList = pickingItems.stream()
                .sorted(Comparator.comparing(PickingItem::getPickingOrderNumber)
                        .thenComparing(PickingItem::getItemNo)
                        .thenComparing(PickingItem::getItemCode))
                .toList();

        // 生成 id
        sortedList.forEach(item -> {
            if (item.getId() == null) {
                String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                String uuid = UUID.randomUUID().toString().replace("-", "");
                item.setId(dateTime + "_" + uuid);
            }
        });


        sortedList.forEach(item -> {
            pickingItemMapper.insertIgnore(item);
        });



    }
}
