package com.honeybudget.service;

import com.honeybudget.domain.Asset;
import com.honeybudget.dto.asset.AssetRequest;
import com.honeybudget.dto.asset.AssetResponse;
import com.honeybudget.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssetService {

    private final AssetRepository assetRepository;

    /**
     * 신규 자산(통장/계좌) 등록
     */
    @Transactional
    public AssetResponse createAsset(AssetRequest request) {
        if (assetRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("이미 존재하는 자산 이름입니다: " + request.getName());
        }

        Asset asset = Asset.create(request.getName(), request.getRole());
        Asset savedAsset = assetRepository.save(asset);

        return AssetResponse.from(savedAsset);
    }

    /**
     * 전체 자산 목록 조회
     */
    public List<AssetResponse> getAllAssets() {
        return assetRepository.findAll().stream()
                .map(AssetResponse::from)
                .toList();
    }

    /**
     * 자산 단건 조회
     */
    public AssetResponse getAssetById(Long assetId) {
        Asset asset = findAssetById(assetId);
        return AssetResponse.from(asset);
    }

    /**
     * 내부 비즈니스 로직용 자산 조회
     */
    public Asset findAssetById(Long assetId) {
        return assetRepository.findById(assetId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자산입니다. ID: " + assetId));
    }
}
