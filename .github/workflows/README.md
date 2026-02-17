# GitHub Actions Workflows

## الـ Workflows المتوفرة

### 1. Android Build (`android-build.yml`)
**الغرض:** بناء التطبيق واختباره تلقائياً

**متى يعمل:**
- عند كل `push` على `main` أو `develop`
- عند كل `pull request` لـ `main` أو `develop`
- يدوياً من تبويب Actions

**ما ينفذه:**
1. ✅ تحميل الكود
2. ✅ إعداد JDK 17
3. ✅ بناء Debug APK
4. ✅ تشغيل Unit Tests
5. ✅ رفع APK للتحميل (7 أيام)
6. ✅ رفع نتائج الاختبارات

**تحميل APK:**
- اذهب إلى: Actions → اختر الـ workflow run → Artifacts → حمّل `raseed-guard-debug-apk`

---

### 2. Android Release Build (`android-release.yml`)
**الغرض:** بناء نسخة Release للنشر

**متى يعمل:**
- عند إنشاء tag بصيغة `v*` (مثل `v1.0.0`)
- يدوياً من تبويب Actions

**ما ينفذه:**
1. ✅ تحميل الكود
2. ✅ إعداد JDK 17
3. ✅ بناء Release APK
4. ✅ رفع APK للتحميل (30 يوم)
5. ✅ إنشاء GitHub Release تلقائياً

**إنشاء Release:**
```bash
git tag v1.0.0
git push origin v1.0.0
```

---

## الإعداد المطلوب

### APK Signing (للـ Release)
لتوقيع الـ APK تلقائياً، أضف في `app/build.gradle.kts`:

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("KEYSTORE_FILE") ?: "keystore.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
    
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            // ...
        }
    }
}
```

ثم أضف Secrets في GitHub:
- `Settings` → `Secrets and variables` → `Actions` → `New repository secret`
- أضف: `KEYSTORE_FILE`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`

---

## الحالة
[![Android Build](https://github.com/USERNAME/Raseed-Guard/actions/workflows/android-build.yml/badge.svg)](https://github.com/USERNAME/Raseed-Guard/actions/workflows/android-build.yml)

(استبدل `USERNAME` باسم المستخدم الخاص بك)
