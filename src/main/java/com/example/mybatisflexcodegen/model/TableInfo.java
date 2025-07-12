package com.example.mybatisflexcodegen.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TableInfo {
    private String tableName;
    private String className;
    private String comment;
    private List<ColumnInfo> columns = new ArrayList<>();
    
    // For code generation
    private String packageName;
    private String moduleName;
    private boolean useLombok = true;
    private boolean generateMapper = true;
    private boolean generateService = true;
    private boolean generateController = true;
}