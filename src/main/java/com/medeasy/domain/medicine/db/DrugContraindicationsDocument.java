package com.medeasy.domain.medicine.db;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "drug_contraindications")
public class DrugContraindicationsDocument {

    @Id
    private String id;

    @Field(name = "item_seq", type = FieldType.Keyword)
    private String itemSeq;

    @Field(name = "pregnancy_contraindication", type = FieldType.Text)
    private String pregnancyContraindication;

    @Field(name = "elderly_precaution", type = FieldType.Text)
    private String elderlyPrecaution;

    @Field(name = "combination_contraindications", type = FieldType.Nested)
    private List<CombinationContraindication> combinationContraindications;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CombinationContraindication {

        @Field(name = "mixture_item_seq", type = FieldType.Keyword)
        private String mixtureItemSeq;

        @Field(name = "prohbt_content", type = FieldType.Text)
        private String prohbtContent;
    }
}
