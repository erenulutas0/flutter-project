# 🚀 Docker Hızlı Başlangıç

## Tek Komutla Başlat

```bash
docker-compose up -d
```

Bu komut şunları başlatır:
- ✅ PostgreSQL (port 5432)
- ✅ Redis (port 6379)  
- ✅ Backend API (port 8082)
- ✅ Flutter Web (port 80)

## Erişim

- **🌐 Web Uygulaması:** http://localhost:8080
- **🔧 Backend API:** http://localhost:8082
- **🗄️ PostgreSQL:** localhost:5432
- **💾 Redis:** localhost:6379

## Önemli Notlar

### Ollama Gereksinimi

Ollama **host'ta** çalışmalıdır (Docker container'ı değil):

```bash
# Ollama'yı başlat
ollama serve

# Model yükle (eğer yoksa)
ollama pull qwen2.5:32b
```

Backend, Ollama'ya `host.docker.internal:11434` üzerinden bağlanır.

### Piper TTS

Piper TTS şu anda Docker'da çalışmıyor. Host'ta çalıştırılmalı veya devre dışı bırakılmalı.

## Yaygın Komutlar

```bash
# Tüm servisleri durdur
docker-compose down

# Logları görüntüle
docker-compose logs -f

# Sadece backend'i yeniden build et
docker-compose build backend
docker-compose up -d backend

# Database'e bağlan
docker exec -it english-app-postgres psql -U postgres -d EnglishApp
```

## Sorun Giderme

**Backend başlamıyor?**
```bash
docker-compose logs backend
```

**Port çakışması?**
`docker-compose.yml` dosyasındaki port numaralarını değiştirin.

**Daha fazla bilgi:** `DOCKER_SETUP.md` dosyasına bakın.


