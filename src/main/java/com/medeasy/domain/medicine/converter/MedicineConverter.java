package com.medeasy.domain.medicine.converter;

import com.medeasy.common.annotation.Converter;
import com.medeasy.domain.medicine.db.MedicineDocument;
import com.medeasy.domain.medicine.db.MedicineEntity;
import com.medeasy.domain.medicine.dto.MedicineRequest;
import com.medeasy.domain.medicine.dto.MedicineResponse;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Converter
@Builder
public class MedicineConverter {

    public MedicineResponse toResponseWithEntity(MedicineEntity entity) {

        return MedicineResponse.builder()
                .id(entity.getId())
                .itemCode(entity.getItemCode())
                .entpName(entity.getEntpName())
                .itemName(entity.getItemName())
                .efficacy(entity.getEfficacy())
                .useMethod(entity.getUseMethod())
                .attention(entity.getAttention())
                .interaction(entity.getInteraction())
                .sideEffect(entity.getSideEffect())
                .depositMethod(entity.getDepositMethod())
                .openAt(entity.getOpenAt())
                .updateAt(entity.getUpdateAt())
                .imageUrl(entity.getImageUrl())
                .bizrno(entity.getBizrno())
                .build()
                ;
    }

    public MedicineResponse toResponseWithDocument(MedicineDocument document) {

        return MedicineResponse.builder()
                .id(Long.parseLong(document.getId()))
                .itemCode(document.getItemCode())
                .entpName(document.getEntpName())
                .itemName(document.getItemName())
                .shape(document.getShape())
                .color(document.getColor())
                .efficacy(document.getEfficacy())
                .useMethod(document.getUseMethod())
                .attention(document.getAttention())
                .interaction(document.getInteraction())
                .sideEffect(document.getSideEffect())
                .depositMethod(document.getDepositMethod())
                .openAt(document.getOpenAt())
                .updateAt(document.getUpdateAt())
                .imageUrl(document.getImageUrl())
                .bizrno(document.getBizrno())
                .build()
                ;
    }

    public MedicineEntity toEntity(MedicineDocument medicineDocument) {
        return MedicineEntity.builder()
                .itemCode(medicineDocument.getItemCode())
                .entpName(medicineDocument.getEntpName())
                .itemName(medicineDocument.getItemName())
                .efficacy(medicineDocument.getEfficacy())
                .useMethod(medicineDocument.getUseMethod())
                .attention(medicineDocument.getAttention()) // getAtpnQesitm의 값과 getAtpnWarnQesitm 값 병합
                .interaction(medicineDocument.getInteraction())
                .sideEffect(medicineDocument.getSideEffect())
                .depositMethod(medicineDocument.getDepositMethod())
                .openAt(medicineDocument.getOpenAt())
                .updateAt(medicineDocument.getUpdateAt())
                .imageUrl(medicineDocument.getImageUrl())
                .bizrno(medicineDocument.getBizrno())
                .build();
    }

    public Page<MedicineResponse> toResponse(Page<MedicineEntity> medicineEntities) {
        return medicineEntities.map(this::toResponseWithEntity);
    }

    public MedicineEntity toEntity(MedicineRequest request){
        String attention = request.getAtpnQesitm();
        if (request.getAtpnWarnQesitm() != null && !request.getAtpnWarnQesitm().isBlank()) {
            // atpnQesitm이 null인 경우에도 대비하여 null 체크
            attention = request.getAtpnWarnQesitm() + " " + (attention != null ? attention : "");
            attention = attention.trim();
        }
        return MedicineEntity.builder()
                .itemCode(request.getItemSeq())
                .entpName(request.getEntpName())
                .itemName(request.getItemName())
                .efficacy(request.getEfcyQesitm())
                .useMethod(request.getUseMethodQesitm())
                .attention(attention) // getAtpnQesitm의 값과 getAtpnWarnQesitm 값 병합
                .interaction(request.getIntrcQesitm())
                .sideEffect(request.getSeQesitm())
                .depositMethod(request.getDepositMethodQesitm())
                .openAt(parseOpenDate(request.getOpenDe()))
                .updateAt(parseUpdateDate(request.getUpdateDe()))
                .imageUrl(request.getItemImage())
                .bizrno(request.getBizrno())
                .build();
    }

    public MedicineDocument toDocument(MedicineEntity entity) {
        return MedicineDocument.builder()
                .id(entity.getId().toString())
                .itemCode(entity.getItemCode())
                .entpName(entity.getEntpName())
                .itemName(entity.getItemName())
                .efficacy(entity.getEfficacy())
                .useMethod(entity.getUseMethod())
                .attention(entity.getAttention())
                .interaction(entity.getInteraction())
                .sideEffect(entity.getSideEffect())
                .depositMethod(entity.getDepositMethod())
                .build();
    }

    private LocalDate parseOpenDate(String openDe) {
        if (openDe == null || openDe.isEmpty()) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(openDe, formatter);
        return dateTime.toLocalDate();
    }

    /**
     * updateDe: "yyyy-MM-dd" → LocalDate로 변환
     */
    private LocalDate parseUpdateDate(String updateDe) {
        if (updateDe == null || updateDe.isEmpty()) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(updateDe, formatter);
    }
}
