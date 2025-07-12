package com.example.mybatisflexcodegen.model;

import lombok.Data;

@Data
public class ColumnInfo {
    private String columnName;
    private String fieldName;
    private String dataType;
    private String javaType;
    private boolean isPrimaryKey;
    private boolean isNullable;
    private String comment;
    private Integer length;
    private Integer precision;
    private Integer scale;
    private String defaultValue;
}