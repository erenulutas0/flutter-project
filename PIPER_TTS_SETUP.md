# Piper TTS Kurulum Rehberi

Bu rehber, projeye Piper TTS entegrasyonunu kurmak için gerekli adımları açıklar.

## Piper TTS Nedir?

Piper TTS, açık kaynaklı, yüksek kaliteli bir metin-konuşma (TTS) motorudur. Özellikle İngilizce için çok net sesler sunar ve offline çalışır.

## Önerilen Sesler

- **en_US-lessac-medium** - En iyi kalite, kadın sesi (ÖNERİLEN)
- **en_US-amy-medium** - Yüksek kalite, kadın sesi
- **en_GB-alan-medium** - Yüksek kalite, erkek sesi

## Kurulum Adımları

### 1. Piper TTS'i İndirin ve Kurun

#### Windows için:
1. [Piper TTS Releases](https://github.com/rhasspy/piper/releases) sayfasından en son sürümü indirin
2. İndirilen dosyayı açın ve `piper.exe` dosyasını PATH'e ekleyin veya proje klasörüne kopyalayın

#### Linux/Mac için:
```bash
# Homebrew ile (Mac)
brew install piper-tts

# Veya manuel olarak GitHub'dan indirin
```

### 2. Model Dosyalarını İndirin

1. [Piper TTS Voices](https://huggingface.co/rhasspy/piper-voices) sayfasına gidin
2. Aşağıdaki modelleri indirin:
   - `en_US-lessac-medium.onnx`
   - `en_US-amy-medium.onnx`
   - `en_GB-alan-medium.onnx`

### 3. Model Dosyalarını Yerleştirin

Model dosyalarını backend klasöründe `models/piper/` dizinine yerleştirin:

```
backend/
  models/
    piper/
      en_US-lessac-medium.onnx
      en_US-amy-medium.onnx
      en_GB-alan-medium.onnx
```

### 4. Backend Yapılandırması

`backend/src/main/java/com/ingilizce/calismaapp/service/PiperTtsService.java` dosyasında aşağıdaki ayarları kontrol edin:

```java
// Piper TTS path - sistem PATH'inde ise "piper" yeterli
private static final String PIPER_PATH = "piper";

// Model dizini - backend klasörüne göre
private static final String MODEL_DIR = "models/piper";
```

Eğer `piper` komutu PATH'te değilse, tam yolunu belirtin:
```java
private static final String PIPER_PATH = "C:/path/to/piper.exe"; // Windows
// veya
private static final String PIPER_PATH = "/usr/local/bin/piper"; // Linux/Mac
```

### 5. Test Etme

Backend'i başlattıktan sonra, aşağıdaki endpoint'i test edin:

```bash
# Status kontrolü
curl http://localhost:8082/api/tts/status

# TTS testi
curl -X POST http://localhost:8082/api/tts/synthesize \
  -H "Content-Type: application/json" \
  -d '{"text": "Hello, this is a test", "voice": "lessac"}' \
  --output test.wav
```

## Kullanım

Flutter uygulaması otomatik olarak Piper TTS'in mevcut olup olmadığını kontrol eder. Eğer Piper TTS mevcut değilse, otomatik olarak `flutter_tts` paketine geri döner.

### Ses Seçimi

Uygulamada ses seçimi yapılırken:
- **Kadın sesi** seçildiğinde → `lessac` modeli kullanılır (en iyi kalite)
- **Erkek sesi** seçildiğinde → `alan` modeli kullanılır

## Sorun Giderme

### Piper TTS bulunamıyor
- `piper` komutunun PATH'te olduğundan emin olun
- Veya `PIPER_PATH` değişkenini tam yol olarak ayarlayın

### Model dosyaları bulunamıyor
- Model dosyalarının `backend/models/piper/` dizininde olduğundan emin olun
- Dosya isimlerinin doğru olduğundan emin olun (`.onnx` uzantılı)

### Ses çalmıyor
- Backend loglarını kontrol edin
- Flutter uygulaması console'unda hata mesajlarını kontrol edin
- `audioplayers` paketinin doğru kurulduğundan emin olun

## Alternatif: Model Dosyalarını Assets Olarak Ekleme

Eğer model dosyalarını projeye dahil etmek isterseniz:

1. Model dosyalarını `backend/src/main/resources/models/piper/` dizinine kopyalayın
2. `PiperTtsService.java` dosyasında `MODEL_DIR` değerini güncelleyin:
   ```java
   private static final String MODEL_DIR = "src/main/resources/models/piper";
   ```

## Daha Fazla Bilgi

- [Piper TTS GitHub](https://github.com/rhasspy/piper)
- [Piper TTS Voices](https://huggingface.co/rhasspy/piper-voices)
- [Piper TTS Documentation](https://github.com/rhasspy/piper/blob/master/README.md)

