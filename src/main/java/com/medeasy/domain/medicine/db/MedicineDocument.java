package com.medeasy.domain.medicine.db;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "medicine_data")
public class MedicineDocument {

    @Id
    private String id;

    @Field(name = "item_seq", type = FieldType.Keyword)
    private String itemSeq; // 의약품 코드

    @Field(name = "item_name", type = FieldType.Text, analyzer = "korean_nori")
    private String itemName; // 의약품 이름

    @Field(name = "entp_name", type = FieldType.Text, analyzer = "korean_nori")
    private String entpName; // 제조사 이름

    @Field(name = "entp_seq", type = FieldType.Keyword)
    private String entpSeq; // 제조사 코드

    @Field(name = "chart", type = FieldType.Text)
    private String chart; // 성상

    @Field(name = "class_no", type = FieldType.Keyword)
    private String classNo; // 분류 코드

    @Field(name = "class_name", type = FieldType.Keyword)
    private String className; // 분류명

    @Field(name = "edi_code", type = FieldType.Keyword)
    private String ediCode; // 보험코드

    @Field(name = "drug_shape", type = FieldType.Keyword)
    private String drugShape; // 의약품 모양

    @Field(name = "color_classes", type = FieldType.Keyword)
    private String colorClasses; // 색상

    @Field(name = "form_code_name", type = FieldType.Keyword)
    private String formCodeName; // 제형 코드

    @Field(name = "line_front", type = FieldType.Keyword)
    private String lineFront; // 앞면 분할선

    @Field(name = "line_back", type = FieldType.Keyword)
    private String lineBack; // 뒷면 분할선

    @Field(name = "print_front", type = FieldType.Keyword)
    private String printFront; // 앞면 표기

    @Field(name = "print_back", type = FieldType.Keyword)
    private String printBack; // 뒷면 표기

    @Field(name = "mark_code_front_anal", type = FieldType.Keyword)
    private String markCodeFrontAnal; // 앞면 마크코드 분석값

    @Field(name = "mark_code_back_anal", type = FieldType.Keyword)
    private String markCodeBackAnal; // 뒷면 마크코드 분석값

    @Field(name = "indications", type = FieldType.Text, analyzer = "korean_nori")
    private String indications; // 효능 및 효과

    @Field(name = "dosage", type = FieldType.Text, analyzer = "korean_nori")
    private String dosage; // 복용 방법

    @Field(name = "precautions", type = FieldType.Text, analyzer = "korean_nori")
    private String precautions; // 주의사항

    @Field(name = "side_effects", type = FieldType.Text, analyzer = "korean_nori")
    private String sideEffects; // 부작용

    @Field(name = "storage_method", type = FieldType.Text, analyzer = "korean_nori")
    private String storageMethod; // 보관 방법

    @Field(name = "valid_term", type = FieldType.Text, analyzer = "korean_nori")
    private String validTerm; // 유효 기간

    @Field(name = "etc_otc_name", type = FieldType.Keyword)
    private String etcOtcName; // 전문의약품 여부

    @Field(name = "cancel_name", type = FieldType.Keyword)
    private String cancelName; // 판매 상태

    @Field(name = "item_image", type = FieldType.Keyword)
    private String itemImage; // 이미지 URL

    @Field(name = "leng_long", type = FieldType.Float)
    private Float lengLong; // 길이 (긴쪽)

    @Field(name = "leng_short", type = FieldType.Float)
    private Float lengShort; // 길이 (짧은쪽)

    @Field(name = "thick", type = FieldType.Float)
    private Float thick; // 두께

    @Field(name = "is_pill", type = FieldType.Long)
    private Long isPill; // 정제 여부 (0,1)
}


