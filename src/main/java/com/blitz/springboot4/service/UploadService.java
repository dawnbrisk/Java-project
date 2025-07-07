package com.blitz.springboot4.service;

import com.blitz.springboot4.entity.PickingItem;
import com.blitz.springboot4.mapper.PickingItemMapper;

import com.blitz.springboot4.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.sql.SQLTransactionRollbackException;

@Service
public class UploadService {


    @Autowired
    private PickingItemMapper pickingItemMapper;

    @Transactional
    public void handleUpload(List<PickingItem> pickingItems) throws SQLTransactionRollbackException, SQLException {



        // 生成 ID

        pickingItems.forEach(item -> {
            if (item.getId() == null) {
                String uuid = IdGenerator.generateId();
                item.setId(uuid);
            }
        });


        List<PickingItem> sortedList = pickingItems.stream()
                .sorted(Comparator.comparing(PickingItem::getId)
                        .thenComparing(PickingItem::getPickingOrderNumber)
                        .thenComparing(PickingItem::getItemNo)
                        .thenComparing(PickingItem::getItemCode))
                .toList();


        // 插入
//        sortedList.forEach(item -> {
//            pickingItemMapper.insertIgnore(item);
//        });

        pickingItemMapper.batchInsertIgnore(sortedList);

    }
}
