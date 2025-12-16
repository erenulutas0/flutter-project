import 'dart:convert';
import 'dart:typed_data';
import 'package:http/http.dart' as http;
import 'package:flutter/foundation.dart';

class PiperTtsService {
  final String baseUrl;
  
  PiperTtsService({this.baseUrl = 'http://localhost:8082'});
  
  /// Check if Piper TTS is available on the backend
  Future<bool> isAvailable() async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl/api/tts/status'),
        headers: {'Content-Type': 'application/json'},
      ).timeout(const Duration(seconds: 5));
      
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return data['available'] == true;
      }
      return false;
    } catch (e) {
      debugPrint('Piper TTS availability check failed: $e');
      return false;
    }
  }
  
  /// Synthesize speech using Piper TTS
  /// Returns audio data as Uint8List (WAV format)
  Future<Uint8List?> synthesize(
    String text, {
    String voice = 'lessac', // lessac, amy, or alan
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/api/tts/synthesize'),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'text': text,
          'voice': voice,
        }),
      ).timeout(const Duration(seconds: 30));
      
      if (response.statusCode == 200) {
        return response.bodyBytes;
      } else {
        debugPrint('Piper TTS synthesis failed: ${response.statusCode}');
        debugPrint('Response: ${response.body}');
        return null;
      }
    } catch (e) {
      debugPrint('Piper TTS synthesis error: $e');
      return null;
    }
  }
  
  /// Get available voices
  Future<List<String>> getVoices() async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl/api/tts/status'),
        headers: {'Content-Type': 'application/json'},
      ).timeout(const Duration(seconds: 5));
      
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        if (data['voices'] != null) {
          return List<String>.from(data['voices']);
        }
      }
      return ['lessac', 'amy', 'alan']; // Default voices
    } catch (e) {
      debugPrint('Failed to get voices: $e');
      return ['lessac', 'amy', 'alan'];
    }
  }
}





