# 📱 Honda ScanTool Android App

Aplikasi Android untuk monitoring data real-time dari ESP32 Honda Motorcycle Scan Tool.

## 🎯 Fitur

- ✅ Dashboard real-time dengan 13+ parameter sensor
- ✅ Auto-update setiap 300ms
- ✅ History data dengan timestamp
- ✅ Material Design 3 (modern UI)
- ✅ Connection status indicator
- ✅ Dark theme support

## 📋 Prasyarat

- Android 7.0 (API 24) atau lebih tinggi
- ESP32 Honda ScanTool sudah running
- WiFi "ScantoolHonda" aktif

## 🚀 Cara Penggunaan

1. **Install APK** di smartphone Android Anda
2. **Hubungkan ke WiFi "ScantoolHonda"** dari ESP32
3. **Buka aplikasi** Honda ScanTool
4. **Data akan muncul otomatis** jika koneksi berhasil

## 🔧 Build dari Source

### Requirements:
- Android Studio Hedgehog (2023.1.1) atau lebih baru
- JDK 17 atau lebih baru
- Android SDK API 34

### Build Steps:
```bash
# 1. Buka project dengan Android Studio
File > Open > Pilih folder android_app

# 2. Sync Gradle
File > Sync Project with Gradle Files

# 3. Build APK
Build > Build Bundle(s) / APK(s) > Build APK(s)

# APK akan tersimpan di:
# android_app/app/build/outputs/apk/debug/app-debug.apk
```

### Build via Command Line:
```bash
cd android_app
./gradlew assembleDebug

# APK output:
# app/build/outputs/apk/debug/app-debug.apk
```

## 📊 Data yang Ditampilkan

| Parameter | Satuan | Keterangan |
|-----------|--------|------------|
| RPM | rpm | Putaran mesin |
| TPS | % / mV | Throttle Position Sensor |
| ECT | °C / mV | Engine Coolant Temperature |
| IAT | °C / mV | Intake Air Temperature |
| MAP | kPa / mV | Manifold Absolute Pressure |
| Battery | Volt | Tegangan battery |
| Injector | ms | Durasi injeksi |
| Ignition | ° | Timing pengapian |
| Speed | km/h | Kecepatan |

## 🏗️ Arsitektur

```
MVVM Architecture Pattern:
├── UI Layer (Fragment/Activity)
├── ViewModel Layer (LiveData)
├── Repository Layer (Data coordination)
└── Data Layer (Retrofit API Service)
```

## 📦 Dependencies

- Retrofit 2.9.0 - HTTP client
- Gson 2.10.1 - JSON parsing
- Coroutines - Async operations
- Material Components - UI components
- MPAndroidChart (optional) - Grafik

## 🔒 Permissions

App memerlukan permission:
- `INTERNET` - Untuk HTTP request ke ESP32
- `ACCESS_WIFI_STATE` - Untuk cek status WiFi
- `ACCESS_NETWORK_STATE` - Untuk cek koneksi

## ⚙️ Konfigurasi

Default ESP32 endpoint: `http://192.168.4.1`

Untuk mengubah IP atau port, edit di:
`app/src/main/java/com/scantool/honda/data/ApiService.kt`

```kotlin
private const val BASE_URL = "http://192.168.4.1/"
```

## 🐛 Troubleshooting

### App tidak bisa connect:
1. Pastikan WiFi "ScantoolHonda" terkoneksi
2. Cek IP ESP32 (default: 192.168.4.1)
3. Test dengan browser dulu: http://192.168.4.1

### Data tidak muncul:
1. Pastikan kontak motor ON
2. Restart ESP32
3. Cek Serial Monitor ESP32 untuk error

### WiFi disconnect otomatis:
- Android modern bisa auto-disconnect WiFi tanpa internet
- Matikan "Auto switch to mobile data" di WiFi settings
- Atau gunakan "Stay connected" option

## 📄 License

MIT License - Bebas digunakan untuk tujuan personal dan edukasi.

## 🤝 Credits

- ESP32 Backend: Autotronic Community
- Android App: Compatible with ESP32 Honda ScanTool v1.0

---

**Made with ❤️ for Honda Riders**
