# OneUI Launcher (سبک و مینیمال)

## ساخت APK
1. پوشهٔ پروژه را در Android Studio باز کن (File > Open) و صبر کن Gradle سینک شود.
2. Build > Build Bundle(s)/APK(s) > Build APK(s)
3. فایل `app/build/outputs/apk/debug/app-debug.apk` را روی گوشی نصب کن.
4. دکمهٔ هوم را بزن و «OneUI Launcher» را به‌عنوان پیش‌فرض انتخاب کن.

## تغییر آیکون‌ها
- PNGها در `app/src/main/assets/icons/` هستند (نام: حروف کوچک، بدون فاصله).
- نگاشت پکیج به آیکون در `IconMapper.kt` است.
- پکیج واقعی اپ را از `adb shell pm list packages` یا اپ Package Name Viewer پیدا کن.
