package com.example.mybatisflexcodegen.util;

import com.example.mybatisflexcodegen.model.ColumnInfo;
import com.example.mybatisflexcodegen.model.TableInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlParser {

    private static final Pattern CREATE_TABLE_PATTERN = Pattern.compile(
            "CREATE\\s+TABLE\\s+(?:IF\\s+NOT\\s+EXISTS\\s+)?[`'\"]?([\\w_]+)[`'\"]?\\s*\\((.+?)\\)(?:\\s*COMMENT\\s*=?\\s*['\"](.+?)['\"])?\\s*(?:;|$)",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private static final Pattern COLUMN_PATTERN = Pattern.compile(
            "\\s*[`'\"]?([\\w_]+)[`'\"]?\\s+([\\w\\(\\),']+)(?:\\s+(?:CHARACTER SET|CHARSET)\\s+\\w+)?(?:\\s+COLLATE\\s+\\w+)?(?:\\s+UNSIGNED)?(?:\\s+ZEROFILL)?(?:\\s+(?:NOT\\s+NULL|NULL))?(?:\\s+(?:DEFAULT\\s+([^,]+?)))?(?:\\s+(?:ON\\s+UPDATE\\s+[^,]+))?(?:\\s+AUTO_INCREMENT)?(?:\\s+COMMENT\\s+['\"](.+?)['\"])?(?:\\s+PRIMARY\\s+KEY)?(?:\\s*,|$)",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern PRIMARY_KEY_PATTERN = Pattern.compile(
            "\\s*PRIMARY\\s+KEY\\s*\\(\\s*[`'\"]?([\\w_]+)[`'\"]?\\s*\\)",
            Pattern.CASE_INSENSITIVE);

    private static final Map<String, String> SQL_TO_JAVA_TYPE_MAP = new HashMap<>();

    static {
        // Initialize SQL to Java type mapping
        SQL_TO_JAVA_TYPE_MAP.put("INT", "Integer");
        SQL_TO_JAVA_TYPE_MAP.put("INTEGER", "Integer");
        SQL_TO_JAVA_TYPE_MAP.put("TINYINT", "Integer");
        SQL_TO_JAVA_TYPE_MAP.put("SMALLINT", "Integer");
        SQL_TO_JAVA_TYPE_MAP.put("MEDIUMINT", "Integer");
        SQL_TO_JAVA_TYPE_MAP.put("BIGINT", "Long");
        SQL_TO_JAVA_TYPE_MAP.put("FLOAT", "Float");
        SQL_TO_JAVA_TYPE_MAP.put("DOUBLE", "Double");
        SQL_TO_JAVA_TYPE_MAP.put("DECIMAL", "java.math.BigDecimal");
        SQL_TO_JAVA_TYPE_MAP.put("DATE", "java.util.Date");
        SQL_TO_JAVA_TYPE_MAP.put("DATETIME", "java.util.Date");
        SQL_TO_JAVA_TYPE_MAP.put("TIMESTAMP", "java.util.Date");
        SQL_TO_JAVA_TYPE_MAP.put("TIME", "java.sql.Time");
        SQL_TO_JAVA_TYPE_MAP.put("YEAR", "Integer");
        SQL_TO_JAVA_TYPE_MAP.put("CHAR", "String");
        SQL_TO_JAVA_TYPE_MAP.put("VARCHAR", "String");
        SQL_TO_JAVA_TYPE_MAP.put("TINYTEXT", "String");
        SQL_TO_JAVA_TYPE_MAP.put("TEXT", "String");
        SQL_TO_JAVA_TYPE_MAP.put("MEDIUMTEXT", "String");
        SQL_TO_JAVA_TYPE_MAP.put("LONGTEXT", "String");
        SQL_TO_JAVA_TYPE_MAP.put("BINARY", "byte[]");
        SQL_TO_JAVA_TYPE_MAP.put("VARBINARY", "byte[]");
        SQL_TO_JAVA_TYPE_MAP.put("TINYBLOB", "byte[]");
        SQL_TO_JAVA_TYPE_MAP.put("BLOB", "byte[]");
        SQL_TO_JAVA_TYPE_MAP.put("MEDIUMBLOB", "byte[]");
        SQL_TO_JAVA_TYPE_MAP.put("LONGBLOB", "byte[]");
        SQL_TO_JAVA_TYPE_MAP.put("BIT", "Boolean");
        SQL_TO_JAVA_TYPE_MAP.put("BOOLEAN", "Boolean");
        SQL_TO_JAVA_TYPE_MAP.put("ENUM", "String");
        SQL_TO_JAVA_TYPE_MAP.put("SET", "String");
        SQL_TO_JAVA_TYPE_MAP.put("TINYINT(1)", "Boolean");
        SQL_TO_JAVA_TYPE_MAP.put("JSON", "String");
    }

    public static List<TableInfo> parseSql(String sqlScript) {
        List<TableInfo> tables = new ArrayList<>();
        
        // Remove SQL comments to avoid interference with parsing
        String cleanedSql = removeComments(sqlScript);
        
        Matcher createTableMatcher = CREATE_TABLE_PATTERN.matcher(cleanedSql);
        while (createTableMatcher.find()) {
            String tableName = createTableMatcher.group(1);
            String columnsDefinition = createTableMatcher.group(2);
            String tableComment = createTableMatcher.group(3);
            
            TableInfo tableInfo = new TableInfo();
            tableInfo.setTableName(tableName);
            tableInfo.setClassName(toCamelCase(tableName, true));
            tableInfo.setComment(tableComment != null ? tableComment : "");
            
            // Find primary key
            Matcher primaryKeyMatcher = PRIMARY_KEY_PATTERN.matcher(columnsDefinition);
            String primaryKeyColumn = null;
            if (primaryKeyMatcher.find()) {
                primaryKeyColumn = primaryKeyMatcher.group(1);
            }
            
            // If no explicit PRIMARY KEY clause, look for PRIMARY KEY in column definition
            if (primaryKeyColumn == null && columnsDefinition.toLowerCase().contains("primary key")) {
                Pattern inlineKeyPattern = Pattern.compile("\\s*[`'\"]?([\\w_]+)[`'\"]?\\s+[\\w\\(\\)]+.*?\\bPRIMARY\\s+KEY\\b", 
                    Pattern.CASE_INSENSITIVE);
                Matcher inlineKeyMatcher = inlineKeyPattern.matcher(columnsDefinition);
                if (inlineKeyMatcher.find()) {
                    primaryKeyColumn = inlineKeyMatcher.group(1);
                }
            }
            
            // Parse columns
            Matcher columnMatcher = COLUMN_PATTERN.matcher(columnsDefinition);
            while (columnMatcher.find()) {
                String columnName = columnMatcher.group(1);
                String dataType = columnMatcher.group(2).toUpperCase();
                String defaultValue = columnMatcher.group(3);
                String comment = columnMatcher.group(4);
                
                ColumnInfo columnInfo = new ColumnInfo();
                columnInfo.setColumnName(columnName);
                columnInfo.setFieldName(toCamelCase(columnName, false));
                columnInfo.setDataType(dataType);
                
                // Extract length, precision, scale from data type
                if (dataType.contains("(")) {
                    String baseType = dataType.substring(0, dataType.indexOf("("));
                    String params = dataType.substring(dataType.indexOf("(") + 1, dataType.indexOf(")"));
                    
                    columnInfo.setDataType(baseType);
                    
                    if (params.contains(",")) {
                        String[] parts = params.split(",");
                        columnInfo.setPrecision(Integer.parseInt(parts[0].trim()));
                        columnInfo.setScale(Integer.parseInt(parts[1].trim()));
                    } else {
                        columnInfo.setLength(Integer.parseInt(params.trim()));
                    }
                }
                
                // Map SQL type to Java type
                String baseType = columnInfo.getDataType().toUpperCase();
                String javaType = SQL_TO_JAVA_TYPE_MAP.getOrDefault(baseType, "String");
                columnInfo.setJavaType(javaType);
                
                // Set primary key
                if (columnName.equals(primaryKeyColumn)) {
                    columnInfo.setPrimaryKey(true);
                }
                
                columnInfo.setComment(comment != null ? comment : "");
                columnInfo.setDefaultValue(defaultValue != null ? defaultValue.trim() : null);
                
                tableInfo.getColumns().add(columnInfo);
            }
            
            tables.add(tableInfo);
        }
        
        return tables;
    }
    
    /**
     * Removes SQL comments from the input SQL script
     * @param sql SQL script with comments
     * @return SQL script without comments
     */
    private static String removeComments(String sql) {
        // Remove single-line comments (-- comment)
        String noSingleLineComments = sql.replaceAll("--.*?\\n", " ");
        
        // Remove multi-line comments (/* comment */)
        String noComments = noSingleLineComments.replaceAll("/\\*[\\s\\S]*?\\*/", " ");
        
        return noComments;
    }
    
    /**
     * Converts a snake_case string to camelCase or PascalCase
     * @param input Input string in snake_case
     * @param capitalizeFirstLetter If true, converts to PascalCase, otherwise to camelCase
     * @return Converted string
     */
    public static String toCamelCase(String input, boolean capitalizeFirstLetter) {
        if (StringUtils.isBlank(input)) {
            return input;
        }
        
        StringBuilder result = new StringBuilder();
        boolean nextUpperCase = capitalizeFirstLetter;
        
        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            
            if (currentChar == '_' || currentChar == ' ' || currentChar == '-') {
                nextUpperCase = true;
            } else if (nextUpperCase) {
                result.append(Character.toUpperCase(currentChar));
                nextUpperCase = false;
            } else {
                result.append(Character.toLowerCase(currentChar));
            }
        }
        
        return result.toString();
    }
}