package com.blitz.springboot4.dao;

import com.blitz.springboot4.entity.PickingItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PickingItemRepository  extends JpaRepository<PickingItem, Long> {
}
