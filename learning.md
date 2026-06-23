# Catatan Belajar — JetBizCard (Jetpack Compose)

Rangkuman tanya-jawab selama sesi belajar mengikuti tutorial JetBizCard (Compose Material3).

## 1. `Argument type mismatch: actual type is 'Dp', but 'CardElevation' was expected`

Tutorial (Nov 2024) pakai Material3 versi lama, di mana `Card(elevation = 4.dp)` masih valid. Di versi sekarang, parameter `elevation` butuh objek `CardElevation`, bukan `Dp` langsung.

```kotlin
// dulu
Card(elevation = 4.dp)

// sekarang
Card(elevation = CardDefaults.cardElevation(defaultElevation = 4.dp))
```

## 2. Padding vs Margin di Compose

Compose tidak punya `margin` terpisah seperti React Native — cuma ada `Modifier.padding()`. Efeknya jadi seperti margin atau padding biasa tergantung **urutan modifier** dan **di mana dipasang**:

- Padding dipasang di `modifier` komponen itu sendiri (sebelum komponen menggambar warnanya) → berfungsi seperti **margin** (ruang di luar).
- Padding dipasang di dalam **content/children** komponen → berfungsi seperti **padding** sungguhan (ruang di dalam).

## 3. Warna di Compose & alpha channel

`Color` di Compose bisa diisi dengan:
- Warna bawaan: `Color.Red`, `Color.Gray`, dst.
- Hex 8 digit `0xAARRGGBB`: `Color(0xFF6200EE)` — **wajib** sertakan 2 digit alpha di depan, kalau ditulis 6 digit saja (`Color(0xF6F1E8)`) maka alpha jadi `0x00` (transparan total) dan IDE akan kasih warning "Missing Color alpha channel".
- Komponen RGB(A): `Color(red = 98, green = 0, blue = 238)` atau versi float 0f–1f.
- Dari `colorResource(id = R.color.xxx)` atau `MaterialTheme.colorScheme.xxx`.

## 4. `Surface` tidak punya `elevation` lagi

Di Material2 dulu ada parameter tunggal `elevation`. Di Material3, dipecah jadi:
- `tonalElevation: Dp` — efek warna nge-tint (overlay warna primary, ala Material You).
- `shadowElevation: Dp` — efek bayangan/shadow klasik.

## 5. Header/TopAppBar tidak muncul

Bukan karena `enableEdgeToEdge()`. Dua sebab:
1. Tema native (`themes.xml`) pakai `NoActionBar` — ini sengaja, supaya tidak dobel dengan TopAppBar Compose. Tidak perlu diubah.
2. `Scaffold` tidak diberi parameter `topBar`. Solusinya isi `topBar = { TopAppBar(title = { Text("...") }) }`.

## 6. `TopAppBar` kena warning "experimental API"

`TopAppBar`/`CenterAlignedTopAppBar`/dst di Material3 masih ditandai `@ExperimentalMaterial3Api` (sudah dicek langsung ke bytecode library, masih berlaku di versi terbaru). Ini **bukan tanda bahaya** — ini cara resmi yang dipakai juga di sample/codelab Google.

Cara hilangkan warning, pilih salah satu:
- `@OptIn(ExperimentalMaterial3Api::class)` di fungsi yang memakainya.
- Atau opt-in di level Gradle (lebih umum di project besar, supaya tidak perlu anotasi di tiap file), di `app/build.gradle.kts`:
```kotlin
kotlin {
    compilerOptions {
        freeCompilerArgs.add("-opt-in=androidx.compose.material3.ExperimentalMaterial3Api")
    }
}
```

## 7. Text di TopAppBar tidak bisa center

`textAlign = TextAlign.Center` di `Text` tidak ngefek karena slot title `TopAppBar` tidak melebarkan `Text` ke seluruh lebar bar. Solusinya ganti komponennya jadi `CenterAlignedTopAppBar`, yang didesain khusus untuk title center.

## 8. Konten ketutup TopAppBar (rounded corner Card kepotong)

`Scaffold` ngasih `innerPadding` lewat content lambda untuk menghindari konten ketiban topBar/bottomBar, tapi parameter itu harus dipakai manual:

```kotlin
Scaffold(...) { innerPadding ->
    CreateBizCard(modifier = Modifier.padding(innerPadding))
}
```

## 9. Alignment tidak "menurun" ke cucu

`horizontalAlignment` di `Column`/`Row` hanya mengatur **anak langsungnya**, bukan anak dari anaknya (cucu). Jadi kalau mau atur posisi elemen yang lebih dalam, harus diatur di level parent langsungnya, bukan di level atasnya.

## 10. Kenapa perlu `fillMaxWidth()` supaya bisa center?

`horizontalAlignment = CenterHorizontally` di `Column` cuma berfungsi kalau `Column` punya ruang lebih dari ukuran child-nya. Tanpa `fillMaxWidth()`, lebar `Column` cuma ikut lebar child-nya sendiri → tidak ada ruang kosong untuk di-tengahkan. Sama halnya `Card` perlu `fillMaxWidth()` (bukan width fix) supaya selebar layar seperti contoh tutorial.

Catatan: `Surface` (Material3 versi project ini) ternyata sudah otomatis center-kan child tunggalnya sendiri tanpa perlu dibungkus `Box(contentAlignment = Center)` — sudah diverifikasi langsung lewat build & screenshot di emulator.

## 11. `MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)`

- `onSurface` = warna semantik Material3 yang aman dipakai **di atas** warna `surface` (kontras terjaga, otomatis beda di light/dark theme).
- `.copy(alpha = 0.5f)` = bikin salinan warna yang sama tapi opacity diubah jadi 50%.
- Kombinasi ini sering dipakai untuk placeholder/divider/disabled state karena otomatis menyesuaikan tema, beda dengan hardcode `Color.Gray` yang bisa salah kontras di dark mode.

## 12. Reformat code otomatis saat save (pengganti Prettier)

`Settings → Editor → Code Style → Kotlin` → pastikan scheme "Kotlin style guide".
`Settings → Tools → Actions on Save` → centang **Reformat code**.

Setelah aktif, kode otomatis dirapikan sesuai Kotlin Coding Conventions tiap kali `⌘S`.

## 13. `MaterialTheme.typography.h4` tidak ada

`h1`–`h6`, `body1/2`, `subtitle1/2`, dst itu skema **Material2** (`androidx.compose.material`). Material3 (`androidx.compose.material3`) pakai skema baru: `display/headline/title/body/label` masing-masing `Large/Medium/Small`.

Daftar lengkap nilai default (dicek dari bytecode library):

| Token | Size | Line Height | Letter Spacing | Weight |
|---|---|---|---|---|
| `displayLarge` | 57sp | 64sp | -0.2sp | Regular |
| `displayMedium` | 45sp | 52sp | 0sp | Regular |
| `displaySmall` | 36sp | 44sp | 0sp | Regular |
| `headlineLarge` | 32sp | 40sp | 0sp | Regular |
| `headlineMedium` | 28sp | 36sp | 0sp | Regular |
| `headlineSmall` | 24sp | 32sp | 0sp | Regular |
| `titleLarge` | 22sp | 28sp | 0sp | Regular |
| `titleMedium` | 16sp | 24sp | 0.2sp | Medium |
| `titleSmall` | 14sp | 20sp | 0.1sp | Medium |
| `bodyLarge` | 16sp | 24sp | 0.5sp | Regular |
| `bodyMedium` | 14sp | 20sp | 0.2sp | Regular |
| `bodySmall` | 12sp | 16sp | 0.4sp | Regular |
| `labelLarge` | 14sp | 20sp | 0.1sp | Medium |
| `labelMedium` | 12sp | 16sp | 0.5sp | Medium |
| `labelSmall` | 11sp | 16sp | 0.5sp | Medium |

Untuk teks nama (judul card), `titleLarge` paling setara dengan `h4` di Material2 lama.

## 14. Bikin teks bold dan italic

`Text` punya parameter langsung, tidak perlu ubah `style`:

```kotlin
Text(
    text = "...",
    fontWeight = FontWeight.Bold,   // bold
    fontStyle = FontStyle.Italic    // italic
)
```

Bisa juga gabung keduanya sekaligus seperti di `CardInfo()` untuk teks "The Super Human Soldier".

## 15. `MaterialTheme.typography.button` tidak ada

Sama kasusnya dengan `h4` (lihat #13) — `button`, `caption`, `overline` itu token Material2. Yang dipakai `Button` Material3 secara default di-internal adalah `labelLarge`, jadi itu pengganti yang paling tepat.

## 16. Text di dalam `Button` tidak muncul / terpotong

Penyebabnya bukan di `Button`-nya, tapi parent `Column` yang membungkusnya punya `.height(300.dp)` (nilai tetap/fix). Begitu konten bertambah (foto + divider + 3 baris teks + button), total tinggi kontennya melebihi 300dp, sehingga bagian yang kelebihan (termasuk `Button`) terpotong oleh batas `Card`.

Fix: hapus `.height(300.dp)` dari `Column`, biarkan dia mengukur tinggi sendiri sesuai isinya:

```kotlin
Column(
    modifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp), // tanpa height fix
    ...
)
```

Pelajaran umum: kalau ada child yang "hilang"/terpotong padahal kodenya benar, cek dulu apakah ada parent dengan ukuran **fix** (`height`/`width`/`size`) yang keduluan dipasang sebelum konten bertambah banyak.

## 17. `Box`, `Surface`, `Card`, `Button` dibanding React Native

| Compose | Fungsi | Mirip di RN | Beda penting |
|---|---|---|---|
| `Box` | Container primitif, anak ditumpuk (z-axis), posisi diatur lewat `contentAlignment`/`Modifier.align()` | `View` dengan children `position: absolute` | RN `View` default flex column (anak ditumpuk vertikal). `Box` numpuk di titik yang sama (default `Alignment.TopStart`) kecuali diatur |
| `Surface` | Primitif visual Material: background color semantik, `shape` (clipping), `border`, `tonalElevation`/`shadowElevation` | Tidak ada built-in di RN core — paling mirip `Surface` dari `react-native-paper` | Di RN core harus styling manual tiap kali. Di Compose, `Surface` adalah fondasi yang dipakai ulang oleh `Card`, `Button`, `Scaffold`, `TopAppBar`, `Dialog`, dll |
| `Card` | `Surface` + preset: rounded corner, elevation, warna container | Komponen custom `<Card>` (View + style) atau `Card` dari `react-native-paper` | Sama prinsipnya — `Card` Compose itu literally `Surface` dengan preset |
| `Button` | `Surface` + handler klik + ripple/state otomatis + text style (`labelLarge`) + minimum touch target 48dp | `TouchableOpacity`/`Pressable` + styling manual | RN: tampilan & feedback klik kamu bikin sendiri. Compose: tampilan Material & ripple otomatis ada |

Inti beda filosofi:
- RN core itu "kosong" (headless) — perlu library (`react-native-paper`, dst) atau styling manual untuk dapat look Material/iOS.
- Compose Material3 "berisi" by default — pakai `Button`/`Card`/`Surface` langsung dapat warna dari `MaterialTheme.colorScheme`, font dari `MaterialTheme.typography`, dan ripple effect.
- Styling: RN pakai object `StyleSheet` (flexbox, mirip CSS). Compose pakai `Modifier` yang **berantai dan urutannya berpengaruh** (lihat #2).
- Shadow/elevation: RN butuh prop beda per platform (`elevation` Android vs `shadowColor/shadowOffset/...` iOS). Compose satukan lewat `tonalElevation` + `shadowElevation`.

## 18. Refactor kode jadi composable baru (Extract Function)

Tidak ada menu khusus "Extract Composable" — yang dipakai refactor generik Kotlin **Extract Function**, dan IDE otomatis nambahin `@Composable` kalau kode yang diselect ada di scope composable.

1. Select baris-baris kode UI yang mau dipisah.
2. Klik kanan → **Refactor → Extract → Function...**, atau shortcut **⌘⌥M** (Mac).
3. Isi nama fungsi baru.
4. IDE bikin fungsi baru lengkap dengan `@Composable`, dan ganti kode asal jadi panggilan ke fungsi itu.

Alternatif kalau menu tidak kelihatan: tekan **⌃T** (Ctrl+T di Mac) untuk buka daftar semua opsi refactor yang valid di konteks kursor saat itu.

Catatan: parameter `modifier: Modifier = Modifier` **tidak otomatis** ditambahkan — itu manual kalau composable barunya perlu menerima modifier dari luar.

## 19. Generate fungsi dari pemanggilan yang belum ada (Create function from usage)

Beda dengan #18 — ini buat kasus kamu **menulis pemanggilan dulu** (misal `Portfolio(data = listOf("Project 1", "Project 2"))`) sebelum fungsinya dibuat.

1. Tulis pemanggilannya — nama fungsi akan digarisbawahi merah (unresolved reference).
2. Taruh kursor di nama fungsi tsb, tekan **⌥↩** (Option+Enter di Mac).
3. Pilih **"Create function 'Portfolio'"** dari menu intention actions.

IDE generate stub fungsi dengan parameter yang sesuai dari cara dipanggil. Yang perlu dicek/tambah manual:
- Tipe parameter hasil infer (kadang jadi `List<Any>` kalau IDE tidak yakin, harus diperbaiki ke `List<String>`).
- Anotasi `@Composable` — tidak selalu otomatis ditambahkan, cek dulu.
- Isi body fungsi (stub yang dihasilkan kosong).

## 20. Kapan pakai `Box`, `Surface`, `Scaffold`?

Ketiganya beda level/tujuan, bukan saling menggantikan:

- **`Scaffold`** — struktur halaman (top-level, biasanya 1x per screen). Nyediain slot `topBar`, `bottomBar`, `floatingActionButton`, dll + otomatis `innerPadding` biar konten tidak ketiban app bar. RN tidak punya bawaan ini — biasanya dirakit sendiri dari `SafeAreaView` + header custom.
- **`Surface`** — fondasi visual Material (background, shape, elevation, border) untuk kasus yang tidak ada preset komponennya. Contoh: `ImageProfile` di project ini pakai `Surface` langsung karena butuh bentuk lingkaran + border + shadow custom yang tidak ada di komponen Material bawaan manapun.
- **`Box`** — container polos, tanpa visual semantik, cuma buat numpuk (z-axis)/posisi elemen atau wrapper alignment sederhana.

Urutan keputusan praktis:
1. Screen baru → mulai dari `Scaffold`.
2. Butuh tampilan card/button standar di dalamnya → pakai `Card`/`Button` langsung.
3. Butuh tampilan Material custom yang tidak ada presetnya → turun ke `Surface`.
4. Cuma butuh numpuk/posisi elemen tanpa visual khusus → `Box`.

## 21. Bikin popup/modal: pakai `Dialog`/`AlertDialog`, bukan `Box`/`Surface`

`Box` dan `Surface` **bukan** alat yang tepat untuk modal sungguhan — keduanya cuma elemen biasa di composition tree, tidak punya behavior modal (window terpisah, dismiss via back button, dim background, focus trap).

Yang benar dipakai:

```kotlin
// Dialog standar (title + text + button)
AlertDialog(
    onDismissRequest = { /* tutup modal */ },
    title = { Text("Judul") },
    text = { Text("Isi pesan") },
    confirmButton = { Button(onClick = { }) { Text("OK") } }
)

// Dialog dengan konten custom
Dialog(onDismissRequest = { /* tutup */ }) {
    Surface(shape = RoundedCornerShape(16.dp)) {
        // konten custom
    }
}
```

`Dialog`/`AlertDialog` menyediakan hal yang tidak bisa dilakukan `Box`:
- Render di window/layer terpisah di atas seluruh konten (termasuk system UI).
- Dim/scrim background otomatis.
- Auto-dismiss saat tombol back ditekan atau tap di luar area (`onDismissRequest`).
- Focus trap & accessibility handling.

Ini setara komponen `Modal` di React Native — bukan `View` posisi absolute nutupin layar (itu cara hack yang tidak dapat behavior modal proper).

`Surface`/`Card` tetap dipakai **di dalam** `Dialog` untuk ngasih tampilan (background, shape, elevation) — karena `Dialog` sendiri cuma window kosong tanpa visual apa-apa.

`Box` hanya cocok untuk overlay yang **bukan** modal sungguhan (tetap terbatas di bounds parent, misal tooltip kecil atau loading indicator yang nutupin sebagian konten).

## 22. Garis bawah di `.value` (misal `buttonClickedState.value = !buttonClickedState.value`)

Bukan error/warning — itu fitur **State Read/Write highlighting** di Android Studio untuk Compose. IDE mendeteksi `.value` adalah properti `State`/`MutableState` (dari `remember { mutableStateOf(...) }`):

- Sisi yang **dibaca** (state read) → trigger recomposition kalau dipakai di composable lain.
- Sisi yang **ditulis** (state write) → ini yang men-trigger recomposition.

Sifatnya cuma informatif, bantu kamu paham bagian mana dari UI yang bakal recompose saat state berubah. Hover untuk lihat tooltip detailnya.

## 23. Container putih tidak sampai tepi kanan (kasus nyata di `Portfolio()`)

Diverifikasi langsung lewat emulator: kotak putih (background sebuah `Row`) berhenti sebelum tepi kanan, sisanya abu-abu.

Penyebab:
1. `Card` item sudah `.fillMaxWidth()` → lebar penuh, ini benar.
2. `Row` di dalamnya **tidak** punya `.fillMaxWidth()` → cuma selebar isinya (avatar+teks), bukan selebar `Card`.
3. `.background(MaterialTheme.colorScheme.surface)` di `Row` cuma mengecat seluas `Row` itu sendiri (sempit). Sisa area di kanan yang terlihat abu-abu adalah warna default `Card` sendiri (Material3 `Card` tanpa `colors=` custom defaultnya abu-abu muda, bukan putih).

Fix: tambahkan `.fillMaxWidth()` di modifier `Row`, sebelum `.background()`.

## 24. Urutan `fillMaxWidth()` vs `padding()` — kapan berpengaruh, kapan tidak

`fillMaxWidth()` dan `padding()` itu sama-sama modifier **ukuran** (sizing) — urutan keduanya **terhadap satu sama lain** tidak mengubah hasil visual akhir, karena secara matematis efeknya saling membatalkan (padding mengurangi constraint yang diteruskan, tapi menambah balik jumlah yang sama saat melaporkan ukurannya ke parent).

Yang **benar-benar berpengaruh**: posisi keduanya **relatif terhadap modifier yang "mengecat"** seperti `.background()` atau `.border()`. Aturannya cuma satu:

> `fillMaxWidth()` harus ada **sebelum** `.background()` dalam chain, supaya `.background()` mengecat area yang sudah dilebarkan, bukan area asli yang masih wrap-content.

## 25. Padding sebagai margin vs padding sungguhan — generalisasi (bukan soal "padding ke-1/ke-2")

Aturan dari #2 dan #23 sebenarnya bukan soal **urutan penulisan ke berapa**, tapi soal **posisi relatif terhadap titik "mengecat"** (`.background()`/`.border()`):

- Padding **sebelum** titik mengecat → jadi margin (area kosong, tidak ikut dicat).
- Padding **sesudah** titik mengecat → jadi padding sungguhan (area di dalam cat, sebelum konten).

Kalau nambah padding lagi, hasilnya tergantung di mana diselipkan — padding tambahan sebelum titik cat akan menumpuk jadi total margin yang lebih besar; padding tambahan sesudah titik cat menumpuk jadi total padding dalam yang lebih besar. Kalau ada **dua** `.background()` dengan padding di antaranya, itu jadi teknik bikin efek "cincin"/border warna (warna pertama nongol sebagai pinggiran warna kedua).

## 26. Ada panduan/aturan resmi untuk tampilan yang rapi?

Ada — berbasis spesifikasi **Material Design**, bukan cuma rasa estetika:

- **m3.material.io** — spesifikasi resmi tiap komponen (Button, Card, TopAppBar, dst): padding internal, tinggi minimum, ukuran icon, jarak antar elemen dalam angka pasti.
- **Grid 8dp** — konvensi luas (bukan cuma Material): semua nilai spacing/padding sebaiknya kelipatan 4dp/8dp (`4, 8, 12, 16, 24, 32, 48, 64`) supaya konsisten secara visual.
- **Touch target minimum 48dp x 48dp** — untuk elemen yang bisa diklik (Button, IconButton), supaya nyaman disentuh jari. Ini alasan Compose `Button` punya minimum height bawaan.
- **Typography scale & color roles** (lihat #13, #11) — memakai `MaterialTheme.typography.xxx` dan `MaterialTheme.colorScheme.xxx` (bukan hardcode) otomatis mengikuti aturan kontras dan skala ukuran yang sudah dirancang.
- **Material Theme Builder** (`material-foundation.github.io/material-theme-builder`) — masukin 1 warna seed, otomatis generate seluruh `colorScheme` yang kontrasnya proporsional dan harmonis.
- **Prinsip layout umum** (berlaku universal, bukan cuma Material):
  - *Proximity*: elemen yang related dikasih spacing kecil, yang tidak related dikasih spacing lebih besar.
  - *Hierarki visual*: ukuran/warna/ketebalan font dipakai untuk nunjukin mana yang paling penting.
  - Hindari dua CTA setara berdampingan kalau bukan pasangan yang memang sengaja setara (biasanya satu primary/filled, satu secondary/outlined).
  - Whitespace itu sengaja, bukan tanda "kurang konten".
