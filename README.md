# Match Counter MLBB — KYY VJESTT

Proyek Android WebView yang membungkus HTML Match Counter MLBB V4 menjadi aplikasi Android. HTML asli disimpan di `app/src/main/assets/index.html`.

## Cara mendapatkan APK lewat GitHub
1. Buat repository baru di GitHub.
2. Upload seluruh isi folder proyek ini ke repository (bukan folder ZIP di dalam repository).
3. Buka tab **Actions** dan pilih workflow **Build Android APK**.
4. Jika workflow belum berjalan otomatis, tekan **Run workflow**.
5. Tunggu sampai status selesai dengan tanda centang hijau.
6. Buka hasil run workflow, unduh artifact **MatchCounterMLBB-APK**, lalu ekstrak ZIP.
7. Instal `app-debug.apk` di HP Android. Jika diminta, izinkan instalasi dari sumber tersebut.

Workflow juga otomatis membuat APK setiap kali ada push ke branch `main` atau `master`.

## Build lokal
Gunakan Android Studio dengan JDK 17 dan Android SDK Platform 35, lalu buka folder proyek ini dan jalankan task `app > Tasks > build > assembleDebug`.

Package: `id.kyyvjestt.matchcounter`  
Versi: `1.0` (debug APK)


## Fitur pembekuan pelanggan

Setiap pelanggan memiliki tombol **Bekukan/Aktifkan**. Pelanggan yang dibekukan tidak akan terkena pengurangan match per pelanggan maupun pengurangan massal, dan penghitung match tetap tersimpan. Status beku juga tersimpan di perangkat.


## Fitur versi 5 (2 room)
- Dua room ditampilkan berdampingan dalam satu layar.
- Setiap room memiliki pelanggan, statistik, pengurangan massal, dan undo yang terpisah.
- Status pelanggan dibekukan tersimpan dan tidak ikut pengurangan massal.
- Data versi sebelumnya dimigrasikan otomatis ke Tim 1 / Room 1.


## Fitur versi 6 (penambahan match dan ikon jeda)
- Tombol **+ MATCH** pada setiap pelanggan untuk menambah match kapan saja, misalnya +10.
- Penambahan match menaikkan total dan sisa match pelanggan tanpa menghapus riwayat undo sebelumnya.
- Tombol pembekuan ditampilkan sebagai ikon **Ⅱ**; saat dibekukan, pelanggan mendapat label **DIBEKUKAN**. Tekan ikon lagi untuk mengaktifkan kembali.
- Fitur tersedia secara terpisah di kedua room.


## Update v7
- Added the supplied MATCH COUNTER artwork as the Android launcher icon (including adaptive icons).
- Adjusted the Android target SDK to 34 and explicitly kept the WebView content below the system status/navigation bars to prevent the app header from being covered by the status bar.


### Audio behavior (v10)
- Existing launch sound plays on each fresh app launch.
- The newly added audio starts 10 seconds after the app first opens and loops while the app remains in the foreground.
- When the app is minimized/backgrounded, the looping audio stops and is not restarted when returning to the app.
