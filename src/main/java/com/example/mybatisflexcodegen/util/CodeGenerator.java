package com.example.mybatisflexcodegen.util;

import com.example.mybatisflexcodegen.model.ColumnInfo;
import com.example.mybatisflexcodegen.model.TableInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class CodeGenerator {

    public static Map<String, String> generateCode(TableInfo tableInfo) {
        Map<String, String> generatedFiles = new HashMap<>();
        
        // Generate entity class
        String entityCode = generateEntityClass(tableInfo);
        generatedFiles.put("Entity.java", entityCode);
        
        // Generate mapper interface if requested
        if (tableInfo.isGenerateMapper()) {
            String mapperCode = generateMapperInterface(tableInfo);
            generatedFiles.put("Mapper.java", mapperCode);
        }
        
        // Generate service class if requested
        if (tableInfo.isGenerateService()) {
            String serviceCode = generateServiceClass(tableInfo);
            String serviceImplCode = generateServiceImplClass(tableInfo);
            generatedFiles.put("Service.java", serviceCode);
            generatedFiles.put("ServiceImpl.java", serviceImplCode);
        }
        
        // Generate controller class if requested
        if (tableInfo.isGenerateController()) {
            String controllerCode = generateControllerClass(tableInfo);
            generatedFiles.put("Controller.java", controllerCode);
        }
        
        return generatedFiles;
    }
    
    private static String generateEntityClass(TableInfo tableInfo) {
        StringBuilder sb = new StringBuilder();
        
        // Package declaration
        sb.append("package ").append(tableInfo.getPackageName()).append(".").append(tableInfo.getModuleName()).append(".entity;\n\n");
        
        // Imports
        sb.append("import com.mybatisflex.annotation.Id;\n");
        sb.append("import com.mybatisflex.annotation.KeyType;\n");
        sb.append("import com.mybatisflex.annotation.Table;\n");
        
        // Check if we need to import java.util.Date
        boolean hasDateField = tableInfo.getColumns().stream()
                .anyMatch(col -> "java.util.Date".equals(col.getJavaType()));
        if (hasDateField) {
            sb.append("import java.util.Date;\n");
        }
        
        // Check if we need to import java.math.BigDecimal
        boolean hasBigDecimalField = tableInfo.getColumns().stream()
                .anyMatch(col -> "java.math.BigDecimal".equals(col.getJavaType()));
        if (hasBigDecimalField) {
            sb.append("import java.math.BigDecimal;\n");
        }
        
        // Add Lombok annotations if requested
        if (tableInfo.isUseLombok()) {
            sb.append("import lombok.Data;\n");
        }
        
        sb.append("\n");
        
        // Class comment
        if (StringUtils.isNotBlank(tableInfo.getComment())) {
            sb.append("/**\n");
            sb.append(" * ").append(tableInfo.getComment()).append("\n");
            sb.append(" */\n");
        }
        
        // Class declaration with annotations
        if (tableInfo.isUseLombok()) {
            sb.append("@Data\n");
        }
        sb.append("@Table(\"").append(tableInfo.getTableName()).append("\")\n");
        sb.append("public class ").append(tableInfo.getClassName()).append(" {\n\n");
        
        // Fields
        for (ColumnInfo column : tableInfo.getColumns()) {
            // Field comment
            if (StringUtils.isNotBlank(column.getComment())) {
                sb.append("    /**\n");
                sb.append("     * ").append(column.getComment()).append("\n");
                sb.append("     */\n");
            }
            
            // Field annotations
            if (column.isPrimaryKey()) {
                sb.append("    @Id(keyType = KeyType.AUTO)\n");
            }
            
            // Field declaration
            sb.append("    private ").append(column.getJavaType()).append(" ").append(column.getFieldName()).append(";\n\n");
        }
        
        // Getters and setters if not using Lombok
        if (!tableInfo.isUseLombok()) {
            for (ColumnInfo column : tableInfo.getColumns()) {
                String fieldName = column.getFieldName();
                String capitalizedFieldName = StringUtils.capitalize(fieldName);
                String javaType = column.getJavaType();
                
                // Getter
                sb.append("    public ").append(javaType).append(" get").append(capitalizedFieldName).append("() {\n");
                sb.append("        return ").append(fieldName).append(";\n");
                sb.append("    }\n\n");
                
                // Setter
                sb.append("    public void set").append(capitalizedFieldName).append("(").append(javaType).append(" ").append(fieldName).append(") {\n");
                sb.append("        this.").append(fieldName).append(" = ").append(fieldName).append(";\n");
                sb.append("    }\n\n");
            }
        }
        
        sb.append("}");
        
        return sb.toString();
    }
    
    private static String generateMapperInterface(TableInfo tableInfo) {
        StringBuilder sb = new StringBuilder();
        
        // Package declaration
        sb.append("package ").append(tableInfo.getPackageName()).append(".").append(tableInfo.getModuleName()).append(".mapper;\n\n");
        
        // Imports
        sb.append("import com.mybatisflex.core.BaseMapper;\n");
        sb.append("import ").append(tableInfo.getPackageName()).append(".").append(tableInfo.getModuleName()).append(".entity.").append(tableInfo.getClassName()).append(";\n");
        sb.append("import org.apache.ibatis.annotations.Mapper;\n\n");
        
        // Interface comment
        sb.append("/**\n");
        sb.append(" * ").append(tableInfo.getClassName()).append(" Mapper Interface\n");
        sb.append(" */\n");
        
        // Interface declaration
        sb.append("@Mapper\n");
        sb.append("public interface ").append(tableInfo.getClassName()).append("Mapper extends BaseMapper<").append(tableInfo.getClassName()).append("> {\n");
        sb.append("}");
        
        return sb.toString();
    }
    
    private static String generateServiceClass(TableInfo tableInfo) {
        StringBuilder sb = new StringBuilder();
        
        // Package declaration
        sb.append("package ").append(tableInfo.getPackageName()).append(".").append(tableInfo.getModuleName()).append(".service;\n\n");
        
        // Imports
        sb.append("import com.mybatisflex.core.service.IService;\n");
        sb.append("import ").append(tableInfo.getPackageName()).append(".").append(tableInfo.getModuleName()).append(".entity.").append(tableInfo.getClassName()).append(";\n\n");
        
        // Interface comment
        sb.append("/**\n");
        sb.append(" * ").append(tableInfo.getClassName()).append(" Service Interface\n");
        sb.append(" */\n");
        
        // Interface declaration
        sb.append("public interface ").append(tableInfo.getClassName()).append("Service extends IService<").append(tableInfo.getClassName()).append("> {\n");
        sb.append("}");
        
        return sb.toString();
    }
    
    private static String generateServiceImplClass(TableInfo tableInfo) {
        StringBuilder sb = new StringBuilder();
        
        // Package declaration
        sb.append("package ").append(tableInfo.getPackageName()).append(".").append(tableInfo.getModuleName()).append(".service.impl;\n\n");
        
        // Imports
        sb.append("import com.mybatisflex.spring.service.impl.ServiceImpl;\n");
        sb.append("import ").append(tableInfo.getPackageName()).append(".").append(tableInfo.getModuleName()).append(".entity.").append(tableInfo.getClassName()).append(";\n");
        sb.append("import ").append(tableInfo.getPackageName()).append(".").append(tableInfo.getModuleName()).append(".mapper.").append(tableInfo.getClassName()).append("Mapper;\n");
        sb.append("import ").append(tableInfo.getPackageName()).append(".").append(tableInfo.getModuleName()).append(".service.").append(tableInfo.getClassName()).append("Service;\n");
        sb.append("import org.springframework.stereotype.Service;\n\n");
        
        // Class comment
        sb.append("/**\n");
        sb.append(" * ").append(tableInfo.getClassName()).append(" Service Implementation\n");
        sb.append(" */\n");
        
        // Class declaration
        sb.append("@Service\n");
        sb.append("public class ").append(tableInfo.getClassName()).append("ServiceImpl extends ServiceImpl<").append(tableInfo.getClassName()).append("Mapper, ").append(tableInfo.getClassName()).append("> implements ").append(tableInfo.getClassName()).append("Service {\n");
        sb.append("}");
        
        return sb.toString();
    }
    
    private static String generateControllerClass(TableInfo tableInfo) {
        StringBuilder sb = new StringBuilder();
        
        // Package declaration
        sb.append("package ").append(tableInfo.getPackageName()).append(".").append(tableInfo.getModuleName()).append(".controller;\n\n");
        
        // Imports
        sb.append("import ").append(tableInfo.getPackageName()).append(".").append(tableInfo.getModuleName()).append(".entity.").append(tableInfo.getClassName()).append(";\n");
        sb.append("import ").append(tableInfo.getPackageName()).append(".").append(tableInfo.getModuleName()).append(".service.").append(tableInfo.getClassName()).append("Service;\n");
        sb.append("import org.springframework.beans.factory.annotation.Autowired;\n");
        sb.append("import org.springframework.web.bind.annotation.*;\n\n");
        sb.append("import java.util.List;\n\n");
        
        // Class comment
        sb.append("/**\n");
        sb.append(" * ").append(tableInfo.getClassName()).append(" Controller\n");
        sb.append(" */\n");
        
        // Class declaration
        sb.append("@RestController\n");
        sb.append("@RequestMapping(\"/").append(tableInfo.getModuleName()).append("/").append(tableInfo.getTableName()).append("\")\n");
        sb.append("public class ").append(tableInfo.getClassName()).append("Controller {\n\n");
        
        // Service field
        sb.append("    @Autowired\n");
        sb.append("    private ").append(tableInfo.getClassName()).append("Service ").append(SqlParser.toCamelCase(tableInfo.getClassName(), false)).append("Service;\n\n");
        
        // CRUD methods
        
        // List all
        sb.append("    @GetMapping(\"/list\")\n");
        sb.append("    public List<").append(tableInfo.getClassName()).append("> list() {\n");
        sb.append("        return ").append(SqlParser.toCamelCase(tableInfo.getClassName(), false)).append("Service.list();\n");
        sb.append("    }\n\n");
        
        // Get by id
        sb.append("    @GetMapping(\"/{id}\")\n");
        sb.append("    public ").append(tableInfo.getClassName()).append(" getById(@PathVariable Long id) {\n");
        sb.append("        return ").append(SqlParser.toCamelCase(tableInfo.getClassName(), false)).append("Service.getById(id);\n");
        sb.append("    }\n\n");
        
        // Create
        sb.append("    @PostMapping\n");
        sb.append("    public boolean save(@RequestBody ").append(tableInfo.getClassName()).append(" ").append(SqlParser.toCamelCase(tableInfo.getClassName(), false)).append(") {\n");
        sb.append("        return ").append(SqlParser.toCamelCase(tableInfo.getClassName(), false)).append("Service.save(").append(SqlParser.toCamelCase(tableInfo.getClassName(), false)).append(");\n");
        sb.append("    }\n\n");
        
        // Update
        sb.append("    @PutMapping\n");
        sb.append("    public boolean update(@RequestBody ").append(tableInfo.getClassName()).append(" ").append(SqlParser.toCamelCase(tableInfo.getClassName(), false)).append(") {\n");
        sb.append("        return ").append(SqlParser.toCamelCase(tableInfo.getClassName(), false)).append("Service.updateById(").append(SqlParser.toCamelCase(tableInfo.getClassName(), false)).append(");\n");
        sb.append("    }\n\n");
        
        // Delete
        sb.append("    @DeleteMapping(\"/{id}\")\n");
        sb.append("    public boolean delete(@PathVariable Long id) {\n");
        sb.append("        return ").append(SqlParser.toCamelCase(tableInfo.getClassName(), false)).append("Service.removeById(id);\n");
        sb.append("    }\n");
        
        sb.append("}");
        
        return sb.toString();
    }
}