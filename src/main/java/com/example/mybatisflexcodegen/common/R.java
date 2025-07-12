package com.example.mybatisflexcodegen.common;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Generic response wrapper for API responses
 * @param <T> Type of data returned
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class R<T> {
    
    private static final int SUCCESS_CODE = 200;
    private static final int ERROR_CODE = 500;
    private static final String SUCCESS_MESSAGE = "Success";
    private static final String ERROR_MESSAGE = "Error";
    
    /**
     * Response code
     */
    private int code;
    
    /**
     * Response message
     */
    private String message;
    
    /**
     * Response data
     */
    private T data;
    
    /**
     * Create a success response with data
     * @param data Response data
     * @param <T> Type of data
     * @return Success response with data
     */
    public static <T> R<T> success(T data) {
        return new R<>(SUCCESS_CODE, SUCCESS_MESSAGE, data);
    }
    
    /**
     * Create a success response with custom message and data
     * @param message Custom message
     * @param data Response data
     * @param <T> Type of data
     * @return Success response with custom message and data
     */
    public static <T> R<T> success(String message, T data) {
        return new R<>(SUCCESS_CODE, message, data);
    }
    
    /**
     * Create an error response with message
     * @param message Error message
     * @param <T> Type of data
     * @return Error response with message
     */
    public static <T> R<T> error(String message) {
        return new R<>(ERROR_CODE, message, null);
    }
    
    /**
     * Create an error response with custom code and message
     * @param code Error code
     * @param message Error message
     * @param <T> Type of data
     * @return Error response with custom code and message
     */
    public static <T> R<T> error(int code, String message) {
        return new R<>(code, message, null);
    }
}