package com.medeasy.domain.medicine.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.medeasy.common.error.ErrorCode;
import com.medeasy.common.error.MedicineErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.medicine.db.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MedicineDocumentService {

    private static final Logger log = LoggerFactory.getLogger(MedicineDocumentService.class);
    private final MedicineSearchRepository medicineSearchRepository;
    private final MedicineRepository medicineRepository; // 기존 JPA Repository

    private final MedicineSearchCustomRepository medicineSearchCustomRepository;

    private final ElasticsearchClient elasticsearchClient;

    // 애플리케이션 실행시 elasticsearch repository, repo 동기화 작업
//    @PostConstruct
    public void init() {
        indexAllMedicines();
    }

    public void indexAllMedicines() {
        List<MedicineEntity> medicines = medicineRepository.findAll();
        List<MedicineDocument> medicineDocuments = medicines.stream()
                .map(m -> MedicineDocument.builder()
                        .id(m.getId().toString())
                        .itemCode(m.getItemCode())
                        .entpName(m.getEntpName())
                        .itemName(m.getItemName())
                        .shape(m.getShape())
                        .color(m.getColor())
                        .efficacy(m.getEfficacy())
                        .useMethod(m.getUseMethod())
                        .attention(m.getAttention())
                        .interaction(m.getInteraction())
                        .sideEffect(m.getSideEffect())
                        .depositMethod(m.getDepositMethod())
                        .openAt(m.getOpenAt())
                        .updateAt(m.getUpdateAt())
                        .imageUrl(m.getImageUrl())
                        .bizrno(m.getBizrno())
                        .build()
                ).toList();

        medicineSearchRepository.saveAll(medicineDocuments);
    }

    // TODO 검색한 약이 존재하지 않을 경우 크롤링 고려
    public List<MedicineDocument> searchMedicineContainingName(String medicineName, int size) {
        log.info("약 검색 {}", medicineName);

        Pageable pageable = PageRequest.of(0, size);
        List<MedicineDocument> medicineDocuments=medicineSearchRepository.findByItemNameContaining(medicineName, pageable);
        medicineDocuments.stream()
                .findAny()
                .orElseThrow(()-> new ApiException(MedicineErrorCode.NOT_FOUND_MEDICINE, "해당하는 약이 존재하지 않습니다. "+medicineName))
                ;
        return medicineDocuments;
    }

    public List<MedicineDocument> searchMedicineContainingNameWithColor(String medicineName, List<String> colors, List<String> shape, int size) {
        Pageable pageable = PageRequest.of(0, size);
        List<MedicineDocument> medicineDocuments=medicineSearchCustomRepository.findMedicineBySearching(medicineName, colors, shape, pageable);
        medicineDocuments.stream()
                .findAny()
                .orElseThrow(()-> new ApiException(MedicineErrorCode.NOT_FOUND_MEDICINE, "해당하는 약이 존재하지 않습니다. "+medicineName))
        ;
        return medicineDocuments;
    }

    public void saveSearchKeyword(String userId, String keyword) {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("user_id", userId);
        jsonMap.put("keyword", keyword);
        jsonMap.put("search_time", Instant.now());

        // IndexRequest를 빌더 패턴으로 생성
        IndexRequest<Map<String, Object>> request = IndexRequest.of(i -> i
                .index("search_history") // 인덱스명
                .document(jsonMap)       // 문서 데이터
        );

        try{
            IndexResponse response = elasticsearchClient.index(request);
        }catch (Exception e){
            throw new ApiException(ErrorCode.SERVER_ERROR, "검색 내역 기록 중 오류 발생");
        }

    }
}
