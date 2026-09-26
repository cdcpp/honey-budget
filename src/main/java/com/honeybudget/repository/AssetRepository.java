package com.honeybudget.repository;

import com.honeybudget.domain.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    Optional<Asset> findByName(String name);

    boolean existsByName(String name);
}
