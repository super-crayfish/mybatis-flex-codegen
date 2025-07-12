package com.example.mybatisflexcodegen.service;

import com.example.mybatisflexcodegen.model.CodeGenerationRequest;
import com.example.mybatisflexcodegen.model.CodeGenerationResult;
import com.example.mybatisflexcodegen.model.TableInfo;
import com.example.mybatisflexcodegen.util.CodeGenerator;
import com.example.mybatisflexcodegen.util.SqlParser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CodeGenerationService {

    public CodeGenerationResult generateCode(CodeGenerationRequest request) {
        // Parse SQL script to extract table information
        List<TableInfo> tables = SqlParser.parseSql(request.getSqlScript());
        
        // Set code generation options for each table
        for (TableInfo table : tables) {
            table.setPackageName(request.getPackageName());
            table.setModuleName(request.getModuleName());
            table.setUseLombok(request.isUseLombok());
            table.setUseValidation(request.isUseValidation());
            table.setUseSwagger(request.isUseSwagger());
            table.setGenerateMapper(request.isGenerateMapper());
            table.setGenerateService(request.isGenerateService());
            table.setGenerateController(request.isGenerateController());
        }
        
        // Generate code for each table
        CodeGenerationResult result = new CodeGenerationResult();
        for (TableInfo table : tables) {
            Map<String, String> generatedFiles = CodeGenerator.generateCode(table);
            
            CodeGenerationResult.TableResult tableResult = new CodeGenerationResult.TableResult();
            tableResult.setTableName(table.getTableName());
            tableResult.setClassName(table.getClassName());
            tableResult.setGeneratedFiles(generatedFiles);
            
            result.getTables().add(tableResult);
        }
        
        return result;
    }
}