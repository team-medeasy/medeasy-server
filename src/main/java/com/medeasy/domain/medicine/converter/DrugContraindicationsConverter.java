package com.medeasy.domain.medicine.converter;

import com.medeasy.common.annotation.Converter;
import com.medeasy.domain.medicine.db.DrugContraindicationsDocument;
import com.medeasy.domain.medicine.dto.DrugContraindicationsResponse;

import java.util.List;

@Converter
public class DrugContraindicationsConverter {

    public DrugContraindicationsResponse toResponse(DrugContraindicationsDocument document) {
        List<DrugContraindicationsResponse.CombinationContraindication> combinationContraindicationList=document.getCombinationContraindications().stream().map(combinationContraindication -> {
            return DrugContraindicationsResponse.CombinationContraindication.builder()
                    .mixtureItemSeq(combinationContraindication.getMixtureItemSeq())
                    .prohbtContent(combinationContraindication.getProhbtContent())
                    .build();
        }).toList();

        return DrugContraindicationsResponse.builder()
                .itemSeq(document.getItemSeq())
                .pregnancyContraindication(document.getPregnancyContraindication())
                .elderlyPrecaution(document.getElderlyPrecaution())
                .combinationContraindications(combinationContraindicationList)
                .build()
                ;
    }
}
