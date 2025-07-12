package com.example.mybatisflexcodegen.model;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Data
public class CodeGenerationResult {
    private List<TableResult> tables = new ArrayList<>();
    
    @Data
    public static class TableResult {
        private String tableName;
        private String className;
        private Map<String, String> generatedFiles = new HashMap<>();
    }
}