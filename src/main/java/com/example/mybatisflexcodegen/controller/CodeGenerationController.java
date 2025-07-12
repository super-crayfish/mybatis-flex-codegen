package com.example.mybatisflexcodegen.controller;

import com.example.mybatisflexcodegen.model.CodeGenerationRequest;
import com.example.mybatisflexcodegen.model.CodeGenerationResult;
import com.example.mybatisflexcodegen.service.CodeGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/codegen")
public class CodeGenerationController {

    @Autowired
    private CodeGenerationService codeGenerationService;

    @PostMapping("/generate")
    public CodeGenerationResult generateCode(@RequestBody CodeGenerationRequest request) {
        return codeGenerationService.generateCode(request);
    }
}