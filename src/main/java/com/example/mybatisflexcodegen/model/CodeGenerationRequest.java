package com.example.mybatisflexcodegen.model;

import lombok.Data;

@Data
public class CodeGenerationRequest {
    private String sqlScript;
    private String packageName = "com.example";
    private String moduleName = "system";
    private boolean useLombok = true;
    private boolean generateMapper = true;
    private boolean generateService = true;
    private boolean generateController = true;
}