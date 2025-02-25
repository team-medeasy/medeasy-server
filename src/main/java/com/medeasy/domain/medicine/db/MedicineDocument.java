package com.medeasy.domain.medicine.db;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "medicine_index")
public class MedicineDocument {
    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String itemCode;

    @Field(type = FieldType.Text)
    private String entpName;

    @Field(type = FieldType.Text)
    private String itemName;

    @Field(type = FieldType.Text)
    private String shape;

    @Field(type = FieldType.Text)
    private String color;

    @Field(type = FieldType.Text)
    private String efficacy;

    @Field(type = FieldType.Text)
    private String useMethod;

    @Field(type = FieldType.Text)
    private String attention;

    @Field(type = FieldType.Text)
    private String interaction; // 상호작용

    @Field(type = FieldType.Text)
    private String sideEffect; // 부작용

    @Field(type = FieldType.Text)
    private String depositMethod; // 보관법

    @Field(type = FieldType.Date)
    private LocalDate openAt; // 공개일자

    @Field(type = FieldType.Date)
    private LocalDate updateAt; // 수정일자

    @Field(type = FieldType.Text)
    private String imageUrl; // 이미지 URL

    @Field(type = FieldType.Text)
    private String bizrno;
}
