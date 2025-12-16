# Docker Setup Guide

Bu rehber, tüm uygulamayı Docker ile çalıştırmak için gerekli adımları içerir.

## Gereksinimler

- Docker Desktop (Windows/Mac) veya Docker Engine (Linux)
- Docker Compose
- Ollama (host'ta çalışıyor olmalı - port 11434)
- En az 4GB RAM (önerilen: 8GB)

## Hızlı Başlangıç

### 1. Tüm Servisleri Başlat

```bash
docker-compose up -d
```

Bu komut şunları başlatır:
- **PostgreSQL** (port 5432)
- **Redis** (port 6379)
- **Backend** (Spring Boot - port 8082)
- **Frontend** (Flutter Web - port 80)

### 2. Servisleri Kontrol Et

```bash
# Tüm container'ları listele
docker-compose ps

# Logları görüntüle
docker-compose logs -f

# Sadece backend logları
docker-compose logs -f backend
```

### 3. Uygulamaya Erişim

- **Frontend:** http://localhost:8080
- **Backend API:** http://localhost:8082
- **PostgreSQL:** localhost:5432
- **Redis:** localhost:6379

## Servis Detayları

### PostgreSQL

- **Database:** EnglishApp
- **Username:** postgres
- **Password:** postgres
- **Port:** 5432
- **Volume:** `postgres_data` (kalıcı veri)

### Redis

- **Port:** 6379
- **Volume:** `redis_data` (kalıcı veri)
- **Kullanım:** Cümle cache'i için

### Backend (Spring Boot)

- **Port:** 8082
- **Health Check:** `/actuator/health` (eğer actuator eklendiyse)
- **Ollama Bağlantısı:** `host.docker.internal:11434` (host'taki Ollama'ya bağlanır)

### Frontend (Flutter Web)

- **Port:** 8080 (host) -> 80 (container)
- **Nginx:** Static file serving
- **API Endpoint:** Backend'e `http://backend:8082` üzerinden bağlanır

## Flutter API Yapılandırması

Flutter uygulaması Docker'da çalışırken backend'e `http://backend:8082` üzerinden bağlanmalı. Ancak tarayıcıdan erişim için `http://localhost:8082` kullanılmalı.

`flutter_app/lib/services/api_service.dart` dosyasında:

```dart
// Docker için
static const String baseUrl = 'http://localhost:8082';

// Veya environment variable kullan
static const String baseUrl = String.fromEnvironment(
  'API_BASE_URL',
  defaultValue: 'http://localhost:8082',
);
```

## Ollama Yapılandırması

Ollama Docker container'ı içinde değil, host'ta çalışmalıdır çünkü:
1. Model dosyaları büyüktür (32B model ~19GB)
2. GPU erişimi gerekebilir
3. Piper TTS host'ta çalışıyor

### Ollama'yı Başlat

```bash
# Windows/Mac/Linux
ollama serve

# Veya Docker ile (opsiyonel)
docker run -d -p 11434:11434 --name ollama ollama/ollama
```

### Model Yükleme

```bash
ollama pull qwen2.5:32b
```

## Piper TTS Notu

Piper TTS şu anda Docker container'ı içinde çalışmıyor çünkü:
- Windows executable gerektirir
- Model dosyaları host'ta bulunuyor

**Çözüm seçenekleri:**
1. Piper TTS'i host'ta çalıştırın (mevcut durum)
2. Piper TTS için ayrı bir Docker container oluşturun (Linux için)
3. TTS özelliğini Docker'da devre dışı bırakın

## Yaygın Komutlar

### Tüm Servisleri Durdur

```bash
docker-compose down
```

### Servisleri Yeniden Başlat

```bash
docker-compose restart
```

### Sadece Backend'i Yeniden Build Et

```bash
docker-compose build backend
docker-compose up -d backend
```

### Sadece Frontend'i Yeniden Build Et

```bash
docker-compose build frontend
docker-compose up -d frontend
```

### Logları Temizle ve Yeniden Başlat

```bash
docker-compose down -v  # Volumes'ları da siler (dikkatli!)
docker-compose up -d --build
```

### Database'e Bağlan

```bash
docker exec -it english-app-postgres psql -U postgres -d EnglishApp
```

### Redis CLI

```bash
docker exec -it english-app-redis redis-cli
```

## Sorun Giderme

### Backend Başlamıyor

```bash
# Backend loglarını kontrol et
docker-compose logs backend

# PostgreSQL bağlantısını kontrol et
docker-compose exec backend ping postgres

# Redis bağlantısını kontrol et
docker-compose exec backend ping redis
```

### Frontend Backend'e Bağlanamıyor

Flutter web uygulaması tarayıcıda çalıştığı için `http://localhost:8082` kullanmalı. Container içinden değil, tarayıcıdan istek yapıldığı için `backend:8082` çalışmaz.

### Ollama Bağlantı Hatası

```bash
# Host'ta Ollama çalışıyor mu?
curl http://localhost:11434/api/tags

# Docker container'dan host'a erişim testi
docker-compose exec backend ping host.docker.internal
```

### Port Çakışması

Eğer portlar kullanılıyorsa `docker-compose.yml` dosyasındaki port mapping'leri değiştirin:

```yaml
ports:
  - "8083:8082"  # Host port:Container port
```

### Volume Sorunları

```bash
# Volume'ları listele
docker volume ls

# Volume'u sil (dikkatli - veri kaybı!)
docker volume rm flutter-project-main_postgres_data
```

## Production Deployment

Production için:

1. **Environment Variables:** `.env` dosyası kullanın
2. **Secrets:** Docker secrets veya external secret manager
3. **SSL/TLS:** Nginx reverse proxy ile HTTPS
4. **Monitoring:** Health checks ve logging
5. **Backup:** PostgreSQL ve Redis backup stratejisi

### .env Dosyası Örneği

```env
POSTGRES_PASSWORD=your_secure_password
REDIS_PASSWORD=your_redis_password
LOG_LEVEL=WARN
```

## Performans İpuçları

- **RAM:** En az 8GB önerilir (32B model için)
- **CPU:** Multi-core önerilir
- **Disk:** SSD önerilir (model yükleme için)
- **Network:** Ollama ve backend arasında düşük latency

## Daha Fazla Bilgi

- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- [Flutter Web Deployment](https://docs.flutter.dev/deployment/web)


