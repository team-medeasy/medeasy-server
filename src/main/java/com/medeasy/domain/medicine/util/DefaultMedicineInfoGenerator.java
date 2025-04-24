package com.medeasy.domain.medicine.util;

import java.util.List;

public class DefaultMedicineInfoGenerator extends MedicineInfoGenerator{
    @Override
    String generateMedicineInfoTitleScript(String medicineName) {
        // 약 이름을 소개하는 간단한 문장
        return String.format(
                "%s에 대한 상세 정보를 알려드리겠습니다.",
                medicineName
        );
    }

    @Override
    String generateMedicineInfoIndicationsScript(List<String> indications) {
        // 적응증 리스트가 비어 있으면 기본 문구
        if (indications == null || indications.isEmpty()) {
            return "";
        }
        // 쉼표로 구분된 적응증 나열
        String joined = String.join(", ", indications);
        return String.format(
                "이 약의 효능에 대해서 설명드리겠습니다.: %s",
                joined
        );
    }

    @Override
    String generateMedicineInfoDosageScript(List<String> dosage) {
        if (dosage == null || dosage.isEmpty()) {
            return "";
        }
        String joined = String.join(", ", dosage);
        return String.format(
                "이 약의 복용법에 대해서 설명드리겠습니다.: %s",
                joined
        );
    }

    @Override
    String generateMedicineInfoPrecautionsScript(List<String> precautions) {
        if (precautions == null || precautions.isEmpty()) {
            return "";
        }
        String joined = String.join(", ", precautions);
        return String.format(
                "이 약의 주의사항에 대해서 설명드리겠습니다.: %s",
                joined
        );
    }

    @Override
    String generateMedicineInfoSideEffectsScript(List<String> sideEffects) {
        if (sideEffects == null || sideEffects.isEmpty()) {
            return "";
        }
        String joined = String.join(", ", sideEffects);
        return String.format(
                "이 약의 발생할 수 있는 부작용에 대해서 설명드리겠습니다.: %s",
                joined
        );
    }
}
