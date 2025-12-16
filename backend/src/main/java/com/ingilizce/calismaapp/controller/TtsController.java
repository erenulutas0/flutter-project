package com.ingilizce.calismaapp.controller;

import com.ingilizce.calismaapp.service.PiperTtsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tts")
public class TtsController {
    
    @Autowired
    private PiperTtsService piperTtsService;
    
    @PostMapping("/synthesize")
    public ResponseEntity<?> synthesize(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        String voice = request.get("voice"); // Optional: lessac, amy, alan
        
        if (text == null || text.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Text is required");
            return ResponseEntity.badRequest().body(error);
        }
        
        try {
            // Check if Piper TTS is available
            if (!piperTtsService.isAvailable()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Piper TTS is not available. Please install Piper TTS.");
                error.put("available", false);
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
            }
            
            // Generate speech
            String audioBase64 = piperTtsService.synthesizeSpeech(text.trim(), voice);
            
            // Decode and return as WAV file
            byte[] audioData = Base64.getDecoder().decode(audioBase64);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "speech.wav");
            headers.setContentLength(audioData.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(audioData);
                    
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to synthesize speech: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        boolean available = piperTtsService.isAvailable();
        status.put("available", available);
        status.put("voices", new String[]{"lessac", "amy", "alan"});
        
        if (!available) {
            status.put("message", "Piper TTS is not installed. Please install it to use high-quality TTS.");
        }
        
        return ResponseEntity.ok(status);
    }
}

