package com.medeasy.domain.medicine.business;

import com.medeasy.common.annotation.Business;
import com.medeasy.domain.medicine.converter.MedicineConverter;
import com.medeasy.domain.medicine.db.MedicineEntity;
import com.medeasy.domain.medicine.dto.MedicineRequest;
import com.medeasy.domain.medicine.dto.MedicineResponse;
import com.medeasy.domain.medicine.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

@Business
@RequiredArgsConstructor
public class MedicineBusiness {

    private final MedicineService medicineService;
    private final MedicineConverter medicineConverter;

    // 파일명(루트 디렉터리에 생성됨)
    private static final String RECORD_FILE_NAME = "medicine_itemseq_record.txt";

    public Page<MedicineResponse> searchMedicineByPaging(Pageable pageable) {
        Page<MedicineEntity> medicineEntities=medicineService.searchMedicineByPaging(pageable);

        Page<MedicineResponse> response=medicineConverter.toResponse(medicineEntities);

        return response;
    }

    public void saveMedicines(List<MedicineRequest> requests) {
        List<MedicineEntity> entities = requests.stream()
                        .map(medicineConverter::toEntity)
                        .toList();

        medicineService.saveAll(entities);

        // 저장된 약품의 기록을 txt 파일에 남김
        saveItemSeqToFile(requests);
    }

    /**
     * 요청에서 전달된 각 MedicineRequest의 itemSeq 값을 txt 파일에 기록합니다.
     * 파일이 존재하지 않으면 새로 생성하고, 이미 존재하면 내용을 이어서 기록(append)합니다.
     */
    private void saveItemSeqToFile(List<MedicineRequest> requests) {
        // 루트 디렉터리(애플리케이션 실행 디렉터리)에 파일 생성
        Path filePath = Paths.get(RECORD_FILE_NAME);

        // 각 request의 itemSeq 값을 한 줄씩 저장하기 위한 문자열 리스트 생성
        List<String> lines = requests.stream()
                .map(MedicineRequest::getItemSeq)
                .collect(Collectors.toList());

        try {
            // 파일이 존재하지 않으면 생성 후 기록, 존재하면 이어쓰기
            if (Files.notExists(filePath)) {
                Files.write(filePath, lines, StandardOpenOption.CREATE);
            } else {
                // 각 기록 앞에 개행 문자(\n)를 추가하여 구분할 수 있도록 함
                // 파일에 기존 내용과 구분되는 새 라인을 추가합니다.
                Files.write(filePath, ("\n" + String.join("\n", lines)).getBytes(), StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            // 예외 발생 시 로깅 혹은 적절한 예외 처리를 합니다.
            e.printStackTrace();
        }
    }
}
