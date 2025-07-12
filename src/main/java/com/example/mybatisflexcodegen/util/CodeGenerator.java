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
            
            // Generate R class for response wrapper
            String rCode = generateRClass(tableInfo);
            generatedFiles.put("R.java", rCode);
        }
        
        return generatedFiles;
    }
    
    /**
     * Generates example values for Swagger documentation based on column type
     * @param column Column information
     * @return Example value as string
     */
    private static String generateExample(ColumnInfo column) {
        String javaType = column.getJavaType();
        String fieldName = column.getFieldName().toLowerCase();
        
        // Generate examples based on field name and type
        if ("String".equals(javaType)) {
            if (fieldName.contains("email")) {
                return "example@example.com";
            } else if (fieldName.contains("phone") || fieldName.contains("mobile")) {
                return "13800138000";
            } else if (fieldName.contains("name")) {
                return "Example Name";
            } else if (fieldName.contains("address")) {
                return "123 Example Street";
            } else if (fieldName.contains("url") || fieldName.contains("website")) {
                return "https://example.com";
            } else if (fieldName.contains("password")) {
                return "********";
            } else {
                return "example";
            }
        } else if ("Integer".equals(javaType) || "Long".equals(javaType)) {
            if (fieldName.contains("age")) {
                return "25";
            } else if (fieldName.contains("year")) {
                return "2023";
            } else if (fieldName.contains("count")) {
                return "10";
            } else {
                return "1";
            }
        } else if ("Double".equals(javaType) || "Float".equals(javaType) || javaType.contains("BigDecimal")) {
            if (fieldName.contains("price") || fieldName.contains("amount")) {
                return "99.99";
            } else {
                return "1.0";
            }
        } else if ("Boolean".equals(javaType)) {
            return "true";
        } else if (javaType.contains("Date")) {
            return "2023-01-01";
        } else {
            return "";
        }
    }
    
    private static String generateRClass(TableInfo tableInfo) {
        StringBuilder sb = new StringBuilder();
        
        // Package declaration
        sb.append("package ").append(tableInfo.getPackageName()).append(".common;\n\n");
        
        // Imports
        sb.append("import lombok.Data;\n");
        sb.append("import lombok.NoArgsConstructor;\n");
        sb.append("import lombok.AllArgsConstructor;\n\n");
        
        // Class comment
        sb.append("/**\n");
        sb.append(" * Generic response wrapper for API responses\n");
        sb.append(" * @param <T> Type of data returned\n");
        sb.append(" */\n");
        
        // Class declaration
        sb.append("@Data\n");
        sb.append("@NoArgsConstructor\n");
        sb.append("@AllArgsConstructor\n");
        sb.append("public class R<T> {\n\n");
        
        // Constants
        sb.append("    private static final int SUCCESS_CODE = 200;\n");
        sb.append("    private static final int ERROR_CODE = 500;\n");
        sb.append("    private static final String SUCCESS_MESSAGE = \"Success\";\n");
        sb.append("    private static final String ERROR_MESSAGE = \"Error\";\n\n");
        
        // Fields
        sb.append("    /**\n");
        sb.append("     * Response code\n");
        sb.append("     */\n");
        sb.append("    private int code;\n\n");
        
        sb.append("    /**\n");
        sb.append("     * Response message\n");
        sb.append("     */\n");
        sb.append("    private String message;\n\n");
        
        sb.append("    /**\n");
        sb.append("     * Response data\n");
        sb.append("     */\n");
        sb.append("    private T data;\n\n");
        
        // Static methods
        sb.append("    /**\n");
        sb.append("     * Create a success response with data\n");
        sb.append("     * @param data Response data\n");
        sb.append("     * @param <T> Type of data\n");
        sb.append("     * @return Success response with data\n");
        sb.append("     */\n");
        sb.append("    public static <T> R<T> success(T data) {\n");
        sb.append("        return new R<>(SUCCESS_CODE, SUCCESS_MESSAGE, data);\n");
        sb.append("    }\n\n");
        
        sb.append("    /**\n");
        sb.append("     * Create a success response with custom message and data\n");
        sb.append("     * @param message Custom message\n");
        sb.append("     * @param data Response data\n");
        sb.append("     * @param <T> Type of data\n");
        sb.append("     * @return Success response with custom message and data\n");
        sb.append("     */\n");
        sb.append("    public static <T> R<T> success(String message, T data) {\n");
        sb.append("        return new R<>(SUCCESS_CODE, message, data);\n");
        sb.append("    }\n\n");
        
        sb.append("    /**\n");
        sb.append("     * Create an error response with message\n");
        sb.append("     * @param message Error message\n");
        sb.append("     * @param <T> Type of data\n");
        sb.append("     * @return Error response with message\n");
        sb.append("     */\n");
        sb.append("    public static <T> R<T> error(String message) {\n");
        sb.append("        return new R<>(ERROR_CODE, message, null);\n");
        sb.append("    }\n\n");
        
        sb.append("    /**\n");
        sb.append("     * Create an error response with custom code and message\n");
        sb.append("     * @param code Error code\n");
        sb.append("     * @param message Error message\n");
        sb.append("     * @param <T> Type of data\n");
        sb.append("     * @return Error response with custom code and message\n");
        sb.append("     */\n");
        sb.append("    public static <T> R<T> error(int code, String message) {\n");
        sb.append("        return new R<>(code, message, null);\n");
        sb.append("    }\n");
        
        sb.append("}\n");
        
        return sb.toString();
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
            sb.append("import lombok.NoArgsConstructor;\n");
            sb.append("import lombok.AllArgsConstructor;\n");
        }
        
        // Add Validation annotations if requested
        if (tableInfo.isUseValidation()) {
            sb.append("import javax.validation.constraints.NotNull;\n");
            sb.append("import javax.validation.constraints.Size;\n");
            sb.append("import javax.validation.constraints.Email;\n");
            sb.append("import javax.validation.constraints.Min;\n");
            sb.append("import javax.validation.constraints.Max;\n");
        }
        
        // Add Swagger annotations if requested
        if (tableInfo.isUseSwagger()) {
            sb.append("import io.swagger.annotations.ApiModel;\n");
            sb.append("import io.swagger.annotations.ApiModelProperty;\n");
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
            sb.append("@NoArgsConstructor\n");
            sb.append("@AllArgsConstructor\n");
        }
        
        // Add Swagger ApiModel annotation if requested
        if (tableInfo.isUseSwagger()) {
            sb.append("@ApiModel(value = \"").append(tableInfo.getClassName()).append("\", description = \"")
              .append(StringUtils.isNotBlank(tableInfo.getComment()) ? tableInfo.getComment() : tableInfo.getClassName())
              .append("\")\n");
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
            
            // Add Swagger ApiModelProperty annotation if requested
            if (tableInfo.isUseSwagger()) {
                sb.append("    @ApiModelProperty(value = \"")
                  .append(StringUtils.isNotBlank(column.getComment()) ? column.getComment() : column.getFieldName())
                  .append("\"");
                
                if (!column.isNullable()) {
                    sb.append(", required = true");
                }
                
                if (column.getLength() != null) {
                    sb.append(", example = \"").append(generateExample(column)).append("\"");
                }
                
                sb.append(")\n");
            }
            
            // Add Validation annotations if requested
            if (tableInfo.isUseValidation()) {
                // Add NotNull annotation for non-nullable fields
                if (!column.isNullable() && !column.isPrimaryKey()) {
                    sb.append("    @NotNull\n");
                }
                
                // Add Size annotation for String fields with length
                if ("String".equals(column.getJavaType()) && column.getLength() != null) {
                    sb.append("    @Size(max = ").append(column.getLength()).append(")\n");
                }
                
                // Add Email annotation for email fields
                if ("String".equals(column.getJavaType()) && 
                    (column.getFieldName().toLowerCase().contains("email") || 
                     (column.getComment() != null && column.getComment().toLowerCase().contains("email")))) {
                    sb.append("    @Email\n");
                }
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
        sb.append("import ").append(tableInfo.getPackageName()).append(".common.R;\n");
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
        sb.append("    public R<List<").append(tableInfo.getClassName()).append(">> list() {\n");
        sb.append("        return R.success(").append(SqlParser.toCamelCase(tableInfo.getClassName(), false)).append("Service.list());\n");
        sb.append("    }\n\n");
        
        // Get by id
        sb.append("    @GetMapping(\"/{id}\")\n");
        sb.append("    public R<").append(tableInfo.getClassName()).append("> getById(@PathVariable Long id) {\n");
        sb.append("        var entity = ").append(SqlParser.toCamelCase(tableInfo.getClassName(), false)).append("Service.getById(id);\n");
        sb.append("        return entity != null ? R.success(entity) : R.error(\"Record not found\");\n");
        sb.append("    }\n\n");
        
        // Create
        sb.append("    @PostMapping\n");
        sb.append("    public R<Boolean> save(@RequestBody ").append(tableInfo.getClassName()).append(" ").append(SqlParser.toCamelCase(tableInfo.getClassName(), false)).append(") {\n");
        sb.append("        try {\n");
        sb.append("            var result = ").append(SqlParser.toCamelCase(tableInfo.getClassName(), false)).append("Service.save(").append(SqlParser.toCamelCase(tableInfo.getClassName(), false)).append(");\n");
        sb.append("            return R.success(result);\n");
        sb.append("        } catch (Exception e) {\n");
        sb.append("            return R.error(\"Failed to save: \" + e.getMessage());\n");
        sb.append("        }\n");
        sb.append("    }\n\n");
        
        // Update
        sb.append("    @PutMapping\n");
        sb.append("    public R<Boolean> update(@RequestBody ").append(tableInfo.getClassName()).append(" ").append(SqlParser.toCamelCase(tableInfo.getClassName(), false)).append(") {\n");
        sb.append("        try {\n");
        sb.append("            var result = ").append(SqlParser.toCamelCase(tableInfo.getClassName(), false)).append("Service.updateById(").append(SqlParser.toCamelCase(tableInfo.getClassName(), false)).append(");\n");
        sb.append("            return R.success(result);\n");
        sb.append("        } catch (Exception e) {\n");
        sb.append("            return R.error(\"Failed to update: \" + e.getMessage());\n");
        sb.append("        }\n");
        sb.append("    }\n\n");
        
        // Delete
        sb.append("    @DeleteMapping(\"/{id}\")\n");
        sb.append("    public R<Boolean> delete(@PathVariable Long id) {\n");
        sb.append("        try {\n");
        sb.append("            var result = ").append(SqlParser.toCamelCase(tableInfo.getClassName(), false)).append("Service.removeById(id);\n");
        sb.append("            return R.success(result);\n");
        sb.append("        } catch (Exception e) {\n");
        sb.append("            return R.error(\"Failed to delete: \" + e.getMessage());\n");
        sb.append("        }\n");
        sb.append("    }\n");
        
        sb.append("}");
        
        return sb.toString();
    }
}