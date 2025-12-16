# Redis ve LanguageTool Kurulum Rehberi

## Redis Kurulumu (Windows)

### 1. Redis İndirme ve Kurulum

**Seçenek 1: WSL2 ile Redis (Önerilen)**
```bash
# WSL2'de Redis kurulumu
wsl
sudo apt update
sudo apt install redis-server
sudo service redis-server start
```

**Seçenek 2: Memurai (Windows için Redis)**
- https://www.memurai.com/ adresinden indirin
- Kurulum sonrası otomatik başlar

**Seçenek 3: Docker ile Redis**
```bash
docker run -d -p 6379:6379 --name redis redis:latest
```

### 2. Redis Bağlantı Testi

```bash
# Redis CLI ile test
redis-cli ping
# Cevap: PONG

# Veya Docker kullanıyorsanız
docker exec -it redis redis-cli ping
```

### 3. Backend Konfigürasyonu

`application.properties` dosyasında Redis ayarları:
```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.password=
```

## LanguageTool

LanguageTool otomatik olarak Maven dependency'lerinden yüklenir. İlk kullanımda biraz yavaş olabilir (model yükleme).

## Cache Davranışı

- **Cache Key Formatı:** `sentences:{word}` (küçük harf)
- **TTL (Time To Live):** 7 gün (604800 saniye)
- **Cache Kontrolü:** Aynı kelime için 7 gün içinde tekrar istek gelirse cache'den döner

## API Kullanımı

### Normal Kullanım (Cache Aktif)
```json
POST /api/chatbot/generate-sentences
{
  "word": "inherent in"
}
```

### Gramer Kontrolü ile
```json
POST /api/chatbot/generate-sentences
{
  "word": "inherent in",
  "checkGrammar": true
}
```

### Response Formatı
```json
{
  "sentences": [
    "The problem is inherent in the system. (sistemde var)",
    "Her talent is inherent in her nature. (doğasında var)"
  ],
  "count": 2,
  "cached": false  // true ise cache'den geldi
}
```

## Redis Olmadan Çalışma

Redis yüklü değilse veya bağlantı hatası varsa:
- Backend normal çalışmaya devam eder
- Sadece cache özelliği devre dışı kalır
- Her istek LLM'e gider (yavaş ama çalışır)

## Sorun Giderme

### Redis Bağlantı Hatası
```
Error: Unable to connect to Redis
```
**Çözüm:** Redis servisinin çalıştığından emin olun:
```bash
# WSL2
sudo service redis-server status

# Docker
docker ps | grep redis
```

### LanguageTool Hatası
```
Failed to initialize LanguageTool
```
**Çözüm:** LanguageTool dependency'leri Maven'den indirilirken hata olabilir. `mvn clean install` çalıştırın.


