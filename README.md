# Smart Expense Manager

Ứng dụng Android quản lý thu/chi cá nhân, sử dụng Kotlin và XML. Định hướng offline-first: Room sẽ là nguồn dữ liệu chính để UI quan sát khi triển khai lưu trữ.

## Kiến trúc

Project dùng **single-module (`:app`), MVVM + Repository** ngay từ đầu.

```text
Activity / Fragment (XML)
    -> ViewModel (UiState, xử lý sự kiện)
    -> Repository
    -> Local data source (Room / DataStore)
    -> Remote data source (khi triển khai API)

Room -> Flow -> ViewModel -> UI
```

Fragment/Activity hiển thị dữ liệu và chuyển sự kiện cho ViewModel. ViewModel không truy cập DAO, API hay DataStore trực tiếp. Repository điều phối nguồn dữ liệu và mapping; UI không dùng trực tiếp Entity hoặc DTO.

## Cấu trúc thư mục

Tất cả package bên dưới nằm trong `app/src/main/java/com/example/smartexpensemanager/`:

```text
smartexpensemanager/
├── MainActivity.kt             # Activity khởi động hiện tại
├── ui/
│   ├── dashboard/              # Tổng thu/chi, số dư, thống kê
│   ├── transactions/           # Danh sách, tìm kiếm, lọc, undo
│   ├── editor/                 # Thêm/sửa giao dịch
│   ├── budget/                 # Ngân sách theo tháng/danh mục
│   ├── scan/                   # Camera, OCR và màn xác nhận
│   ├── settings/               # Cài đặt, trạng thái đồng bộ
│   ├── onboarding/             # Màn giới thiệu bảo mật từ Figma
│   └── common/                 # Thành phần UI dùng chung
├── data/
│   ├── model/                  # Model Kotlin thuần dùng bởi Repository và UI
│   ├── repository/             # Repository interface và implementation
│   ├── local/
│   │   ├── dao/                # Room DAO
│   │   ├── entity/             # Room Entity
│   │   ├── converter/          # Room TypeConverter
│   │   └── preferences/        # DataStore
│   ├── remote/
│   │   ├── api/                # Retrofit service
│   │   └── dto/                # Request/response DTO
│   ├── mapper/                 # Entity/DTO <-> model
│   └── sync/                   # Điều phối đồng bộ local/remote
├── di/                         # Cung cấp dependency, Hilt khi tích hợp
├── worker/                     # WorkManager worker
└── util/                       # Tiện ích nhỏ dùng chung, không chứa business rule
```

Room database sẽ đặt trực tiếp trong `data/local/`. XML layout tiếp tục nằm trong `app/src/main/res/layout/`; tài nguyên Android dùng cấu trúc `res` chuẩn.

Mỗi package màn hình sẽ chứa Fragment, ViewModel, UiState và Adapter riêng khi cần. Ví dụ: `TransactionsFragment`, `TransactionsViewModel`, `TransactionsUiState`, `TransactionAdapter`. Repository có thể dùng cặp `TransactionRepository` và `DefaultTransactionRepository` trong `data/repository/`; ViewModel phụ thuộc interface.

Các package chưa triển khai được giữ bằng `.gitkeep`. Màn onboarding bảo mật hiện mở khi khởi động app, dùng XML và Fragment. 10 icon SVG gốc từ Figma đã được chuyển sang Android VectorDrawable trong `res/drawable/onboarding_*.xml`, giữ nguyên path, màu và viewport; UI sử dụng ImageView và icon của MaterialButton. Không cần thư viện render SVG.

Dependency và plugin được quản lý tại `gradle/libs.versions.toml`. Module app đã bật ViewBinding, Hilt/KSP và Java 17; thêm thư viện cho MVVM/Flow, Navigation, RecyclerView, Room 2.x, Preferences DataStore, CameraX, ML Kit OCR bundled, Retrofit/Gson, OkHttp, WorkManager và Biometric. Thư viện kiểm thử gồm Coroutines Test, MockK, Turbine, MockWebServer, Room Testing và Work Testing. Đây là cấu hình dependency; các tính năng tương ứng chưa được triển khai. Logging interceptor chỉ có ở debug và chưa được gắn vào HTTP client.

Kiểm tra dependency: `./gradlew :app:assembleDebug :app:testDebugUnitTest` đã thành công. Trên emulator Pixel 7 x86_64 hiện tại, Android báo tương thích page size 16 KB đối với các native library `libdatastore_shared_counter.so`, `libsurface_util_jni.so`, `libimage_processing_util_jni.so` và `libmlkit_google_ocr_pipeline.so`; app chạy bằng compatibility mode. Cần xử lý và kiểm tra lại các dependency native trước khi phát hành cho thiết bị 16 KB.

Các nút onboarding hiện mở thông báo cho màn chưa triển khai; liên kết pháp lý thông báo chưa có nội dung. Chưa có đăng nhập, mã hóa AES-256, sinh trắc học hoặc cloud sync. Các tuyên bố này là nội dung thiết kế, chưa phải tính năng đã hoạt động. Màn tĩnh chưa cần ViewModel; thêm ViewModel khi có trạng thái onboarding thực tế.

Nguồn thiết kế: https://www.figma.com/design/YydkfW96kGL24Tbr9Rt7Xl/Untitled?node-id=1-240 (chỉ báo bước 3/3 theo thiết kế). UI dùng font sans hệ thống; font Inter của Figma chưa được đóng gói.

## Khi nào thêm Domain và UseCase

Chưa tạo `domain/` hoặc lớp UseCase cho các thao tác CRUD/getter đơn giản. ViewModel gọi Repository trực tiếp ở giai đoạn đầu.

Thêm Domain khi có quy tắc nghiệp vụ cần tách riêng để tái sử dụng hoặc kiểm thử độc lập, ví dụ tính mức vượt ngân sách, quy tắc quy đổi tiền, hay xử lý ứng viên tổng tiền từ hóa đơn. Khi đó:

- Thêm `domain/usecase/` cho logic thực tế, tránh lớp chỉ chuyển tiếp lời gọi Repository.
- Chuyển các model và Repository interface liên quan sang `domain/model/` và `domain/repository/`; implementation vẫn ở `data/repository/`.
- Domain là Kotlin thuần, không phụ thuộc Android, Room hoặc Retrofit.
- Luồng có nghiệp vụ trở thành `ViewModel -> UseCase -> Repository`; luồng đơn giản vẫn có thể gọi Repository trực tiếp.

Tiền sẽ dùng `Long` theo đơn vị tiền nhỏ nhất (VND là đồng), tránh `Float`/`Double` cho phép tính tiền. OCR phải được người dùng xác nhận trước khi lưu giao dịch.
