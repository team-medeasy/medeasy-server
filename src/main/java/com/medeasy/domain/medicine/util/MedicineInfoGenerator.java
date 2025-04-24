package com.medeasy.domain.medicine.util;

import com.medeasy.domain.medicine.db.MedicineDocument;

import java.util.List;

public abstract class MedicineInfoGenerator {

    public String generateScriptMedicineInfo(MedicineDocument medicineDocument) {
        String medicineName = medicineDocument.getItemName();
        List<String> indications = medicineDocument.getIndications();
        List<String> dosage = medicineDocument.getDosage();
        List<String> precautions = medicineDocument.getPrecautions();
        List<String> sideEffects = medicineDocument.getSideEffects();

        String titleScript=generateMedicineInfoTitleScript(medicineName);
        String indicationsScript=generateMedicineInfoIndicationsScript(indications);
        String dosageScript=generateMedicineInfoDosageScript(dosage);
        String precautionsScript=generateMedicineInfoPrecautionsScript(precautions);
        String sideEffectsScript=generateMedicineInfoSideEffectsScript(sideEffects);

        // 파트들을 순서대로 합쳐서 반환
        return String.join("\n\n",
                titleScript,
                indicationsScript,
                dosageScript,
                precautionsScript,
                sideEffectsScript
        );
    }

    abstract String generateMedicineInfoTitleScript(String medicineName);
    abstract String generateMedicineInfoIndicationsScript(List<String> indications);
    abstract String generateMedicineInfoDosageScript(List<String> dosage);
    abstract String generateMedicineInfoPrecautionsScript(List<String> precautions);
    abstract String generateMedicineInfoSideEffectsScript(List<String> sideEffects);
}
