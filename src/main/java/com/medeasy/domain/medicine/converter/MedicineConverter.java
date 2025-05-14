package com.medeasy.domain.medicine.converter;

import com.medeasy.common.annotation.Converter;
import com.medeasy.domain.medicine.db.DrugContraindicationsDocument;
import com.medeasy.domain.medicine.db.MedicineDocument;
import com.medeasy.domain.medicine.dto.MedicineResponse;
import com.medeasy.domain.medicine.dto.MedicineResponseWithContraindications;
import com.medeasy.domain.medicine.dto.MedicineSimpleDto;
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
                .chart(document.getChart())
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
                .indications(String.join("\n", document.getIndications()))
                .dosage(String.join("\n", document.getDosage()))
                .precautions(String.join("\n", document.getPrecautions()))
                .sideEffects(String.join("\n", document.getSideEffects()))
                .storageMethod(document.getStorageMethod())
                .validTerm(document.getValidTerm())
                .etcOtcName(document.getEtcOtcName())
                .cancelName(document.getCancelName())
                .itemImage(document.getItemImage())
                .lengLong(document.getLengLong())
                .lengShort(document.getLengShort())
                .thick(document.getThick())
                .isPill(document.getIsPill())
                .audioUrl(document.getAudioUrl())
                .build();
    }

    public MedicineResponseWithContraindications toResponseWithContraindications(MedicineDocument medicineDocument, DrugContraindicationsDocument contraindicationsDocument) {

//        return MedicineResponse.builder()
//                .id(medicineDocument.getId())
//                .itemSeq(medicineDocument.getItemSeq())
//                .itemName(medicineDocument.getItemName())
//                .entpName(medicineDocument.getEntpName())
//                .entpSeq(medicineDocument.getEntpSeq())
//                .chart(medicineDocument.getChart())
//                .classNo(medicineDocument.getClassNo())
//                .className(medicineDocument.getClassName())
//                .ediCode(medicineDocument.getEdiCode())
//                .drugShape(medicineDocument.getDrugShape())
//                .colorClasses(medicineDocument.getColorClasses())
//                .lineFront(medicineDocument.getLineFront())
//                .lineBack(medicineDocument.getLineBack())
//                .printFront(medicineDocument.getPrintFront())
//                .printBack(medicineDocument.getPrintBack())
//                .markCodeFrontAnal(medicineDocument.getMarkCodeFrontAnal()) // 수정: 중복된 `markCodeBackAnal` 제거
//                .markCodeBackAnal(medicineDocument.getMarkCodeBackAnal())
//                .indications(String.join("\n", medicineDocument.getIndications()))
//                .dosage(String.join("\n", medicineDocument.getDosage()))
//                .precautions(String.join("\n", medicineDocument.getPrecautions()))
//                .sideEffects(String.join("\n", medicineDocument.getSideEffects()))
//                .storageMethod(medicineDocument.getStorageMethod())
//                .validTerm(medicineDocument.getValidTerm())
//                .etcOtcName(medicineDocument.getEtcOtcName())
//                .cancelName(medicineDocument.getCancelName())
//                .itemImage(medicineDocument.getItemImage())
//                .lengLong(medicineDocument.getLengLong())
//                .lengShort(medicineDocument.getLengShort())
//                .thick(medicineDocument.getThick())
//                .isPill(medicineDocument.getIsPill())
//                .audioUrl(medicineDocument.getAudioUrl())
//                .build();
        return null;
    }


    public MedicineSimpleDto toSimpleResponseWithDocument(MedicineDocument document) {
        return MedicineSimpleDto.builder()
                .medicineId(document.getId())
                .medicineName(document.getItemName())
                .entpName(document.getEntpName())
                .className(document.getClassName())
                .itemImage(document.getItemImage())
                .build()
                ;
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
