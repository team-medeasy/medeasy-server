package com.medeasy.domain.medicine.business;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.medeasy.common.annotation.Business;
import com.medeasy.common.error.ErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.file.FileBucketService;
import com.medeasy.domain.medicine.converter.MedicineConverter;
import com.medeasy.domain.medicine.db.*;
import com.medeasy.domain.medicine.dto.MedicineResponse;
import com.medeasy.domain.medicine.dto.MedicineSimpleDto;
import com.medeasy.domain.medicine.service.MedicineDocumentService;
import com.medeasy.domain.medicine.util.MedicineInfoGenerator;
import com.medeasy.domain.mp3.Mp3Service;
import com.medeasy.domain.tts.TtsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;


@Slf4j
@Business
@RequiredArgsConstructor
public class MedicineBusiness {

    private final MedicineDocumentService medicineDocumentService;
    private final MedicineConverter medicineConverter;

    private final MedicineInfoGenerator medicineInfoGenerator;
    private final TtsService ttsService;
    private final Mp3Service mp3Service;
    private final FileBucketService fileBucketService;

    public String combineColors(String color1, String color2) {
        // 컬러 값 결합 로직
        String color = null;

        if (color1 != null && !color1.isEmpty() && color2 != null && !color2.isEmpty()) {
            color = color1 + ", " + color2;
        } else if (color1 != null && !color1.isEmpty()) {
            color = color1;
        } else if (color2 != null && !color2.isEmpty()) {
            color = color2;
        }

        return color;
    }

    /**
     * 메인 검색 로직
     * */
    public List<MedicineResponse> searchMedicinesWithColor(Long userId, String medicineName, List<MedicineColor> enumColors, List<MedicineShape> enumShapes, int page, int size) {
        List<String> colors= (enumColors != null && !enumColors.isEmpty()) ? enumColors.stream().map(MedicineColor::getColor).toList() : null;
        List<String> shapes= (enumShapes != null && !enumShapes.isEmpty()) ? enumShapes.stream().map(MedicineShape::getShape).toList() : null;

        List<MedicineDocument> medicineDocuments=medicineDocumentService.searchMedicineContainingNameWithColor(medicineName, colors, shapes, page, size);

        return medicineDocuments.stream().map(medicineConverter::toResponseWithDocument).toList();
    }

    public List<MedicineSimpleDto> getSimilarMedicineList(String medicineId, int page, int size) {
        MedicineDocument medicineDocument=medicineDocumentService.findMedicineDocumentById(medicineId);

        List<MedicineDocument> medicineDocuments=medicineDocumentService.findSimilarMedicineList(medicineDocument, page, size);
        // 유사한 약 중 검색 대상 약 제외
        medicineDocuments.removeFirst();

        return medicineDocuments.stream().map(medicineConverter::toSimpleResponseWithDocument).toList();
    }

    public MedicineResponse getMedicineById(String medicineId) {
        MedicineDocument medicineDocument=medicineDocumentService.findMedicineDocumentById(medicineId);

        return medicineConverter.toResponseWithDocument(medicineDocument);
    }

    public MedicineResponse getMedicineByItemSeq(String itemSeq) {
        MedicineDocument medicineDocument=medicineDocumentService.getMedicineByItemSeq(itemSeq);

        return medicineConverter.toResponseWithDocument(medicineDocument);
    }

    public List<MedicineResponse> getMedicineListByIds(List<String> medicineIds) {
        List<MedicineDocument> medicineDocuments=medicineDocumentService.getMedicinesByIds(medicineIds);

        return medicineDocuments.stream().map(medicineConverter::toResponseWithDocument).toList();
    }

    /**
     * 약 음성 파일 생성 및 GCP Bucket에 저장
     * */
    public String getMedicineInfoMp3FileUri(String medicineId) {
        // 약 정보 조회
        MedicineDocument medicineDocument=medicineDocumentService.findMedicineDocumentById(medicineId);
        String fileName = medicineDocument.getItemName()+"_음성정보"+".mp3";
        String bucketName = "medeasy-mp3";

        if(fileBucketService.getAudioFileUrl(bucketName, fileName) != null){
            return fileBucketService.getAudioFileUrl(bucketName, fileName);
        }

        // 약 정보 텍스트 스크립트
        String medicineInfoScript=medicineInfoGenerator.generateScriptMedicineInfo(medicineDocument);

        // 바이트 데이터
        byte[] audioBytes= ttsService.convertTextToSpeech(medicineInfoScript);
        log.info("약 정보 음성 데이터 변환 완료");

        // 2) GCS에 업로드
        String audioUrl=fileBucketService.saveAudioFile(audioBytes, bucketName, fileName);
        log.info("gcs 버킷 저장 완료");

        medicineDocumentService.updateMedicineAudioUrl(medicineId, audioUrl);

        return audioUrl;
    }
}
