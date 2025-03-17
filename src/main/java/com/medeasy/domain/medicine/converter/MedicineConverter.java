package com.medeasy.domain.medicine.converter;

import com.medeasy.common.annotation.Converter;
import com.medeasy.domain.medicine.db.MedicineDocument;
import com.medeasy.domain.medicine.dto.MedicineResponse;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Converter
@Builder
public class MedicineConverter {

    public MedicineResponse toResponseWithDocument(MedicineDocument document) {

        return MedicineResponse.builder()
                .id(document.getId())
                .itemSeq(document.getItemSeq())
                .itemName(document.getItemName())
                .entpName(document.getEntpName())
                .entpSeq(document.getEntpSeq())
                .classNo(document.getClassNo())
                .className(document.getClassName())
                .ediCode(document.getEdiCode())
                .drugShape(document.getDrugShape())
                .colorClasses(document.getColorClasses())
                .lineFront(document.getLineFront())
                .lineBack(document.getLineBack())
                .printFront(document.getPrintFront())
                .printBack(document.getPrintBack())
                .markCodeFrontAnal(document.getMarkCodeFrontAnal()) // 수정: 중복된 `markCodeBackAnal` 제거
                .markCodeBackAnal(document.getMarkCodeBackAnal())
                .indications(document.getIndications())
                .dosage(document.getDosage())
                .precautions(document.getPrecautions())
                .sideEffects(document.getSideEffects())
                .storageMethod(document.getStorageMethod())
                .validTerm(document.getValidTerm())
                .etcOtcName(document.getEtcOtcName())
                .cancelName(document.getCancelName())
                .itemImage(document.getItemImage())
                .lengLong(document.getLengLong())
                .lengShort(document.getLengShort())
                .thick(document.getThick())
                .isPill(document.getIsPill())
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
