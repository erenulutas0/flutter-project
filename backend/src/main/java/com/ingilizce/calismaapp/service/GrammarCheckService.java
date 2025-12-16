package com.ingilizce.calismaapp.service;

import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.RuleMatch;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LanguageTool ile gramer kontrolü servisi (Opsiyonel)
 */
@Service
public class GrammarCheckService {
    
    private final JLanguageTool languageTool;
    private boolean enabled = true; // application.properties'ten kontrol edilebilir
    
    public GrammarCheckService() {
        try {
            this.languageTool = new JLanguageTool(new AmericanEnglish());
            System.out.println("LanguageTool initialized successfully");
        } catch (Exception e) {
            System.err.println("Failed to initialize LanguageTool: " + e.getMessage());
            throw new RuntimeException("LanguageTool initialization failed", e);
        }
    }
    
    /**
     * Bir cümlenin gramerini kontrol eder
     * @param sentence Kontrol edilecek cümle
     * @return Gramer hataları listesi (boş ise hata yok)
     */
    public Map<String, Object> checkGrammar(String sentence) {
        if (!enabled) {
            return createNoErrorResponse();
        }
        
        try {
            List<RuleMatch> matches = languageTool.check(sentence);
            
            if (matches.isEmpty()) {
                return createNoErrorResponse();
            }
            
            List<Map<String, Object>> errors = new ArrayList<>();
            for (RuleMatch match : matches) {
                Map<String, Object> error = new HashMap<>();
                error.put("message", match.getMessage());
                error.put("shortMessage", match.getShortMessage());
                error.put("fromPos", match.getFromPos());
                error.put("toPos", match.getToPos());
                error.put("suggestions", match.getSuggestedReplacements());
                errors.add(error);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("hasErrors", true);
            result.put("errors", errors);
            result.put("errorCount", errors.size());
            
            return result;
        } catch (IOException e) {
            System.err.println("Error checking grammar: " + e.getMessage());
            return createNoErrorResponse();
        }
    }
    
    /**
     * Birden fazla cümlenin gramerini kontrol eder
     */
    @SuppressWarnings("unchecked")
    public Map<String, List<Map<String, Object>>> checkMultipleSentences(List<String> sentences) {
        Map<String, List<Map<String, Object>>> results = new HashMap<>();
        
        for (String sentence : sentences) {
            Map<String, Object> checkResult = checkGrammar(sentence);
            Object errorsObj = checkResult.get("errors");
            if (errorsObj instanceof List) {
                List<Map<String, Object>> errors = (List<Map<String, Object>>) errorsObj;
                if (errors != null && !errors.isEmpty()) {
                    results.put(sentence, errors);
                }
            }
        }
        
        return results;
    }
    
    private Map<String, Object> createNoErrorResponse() {
        Map<String, Object> result = new HashMap<>();
        result.put("hasErrors", false);
        result.put("errors", new ArrayList<>());
        result.put("errorCount", 0);
        return result;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
}

