package com.blitz.springboot4.dao;

import com.blitz.springboot4.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemsRepository extends JpaRepository<Item, Long> {
}
