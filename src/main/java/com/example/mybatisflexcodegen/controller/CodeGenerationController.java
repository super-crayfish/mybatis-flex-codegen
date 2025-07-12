package com.example.mybatisflexcodegen.controller;

import com.example.mybatisflexcodegen.model.CodeGenerationRequest;
import com.example.mybatisflexcodegen.model.CodeGenerationResult;
import com.example.mybatisflexcodegen.common.R;
import com.example.mybatisflexcodegen.service.CodeGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Code Generation Controller
 */
@RestController
@RequestMapping("/api/codegen")
public class CodeGenerationController {

    @Autowired
    private CodeGenerationService codeGenerationService;

    /**
     * Generate code from SQL CREATE statements
     * @param request Code generation request
     * @return Generated code result
     */
    @PostMapping("/generate")
    public R<CodeGenerationResult> generateCode(@RequestBody CodeGenerationRequest request) {
        try {
            CodeGenerationResult result = codeGenerationService.generateCode(request);
            return R.success(result);
        } catch (Exception e) {
            return R.error("Code generation failed: " + e.getMessage());
        }
    }
}