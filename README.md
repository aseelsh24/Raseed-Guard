# Raseed Guard

تطبيق تذكير بانتهاء الباقة والتنبؤ بنفاد الرصيد.

## المميزات الرئيسية
- تتبع تاريخ انتهاء الباقة
- تتبع الرصيد المتبقي
- تنبيهات ذكية عند قرب نفاد الرصيد
- واجهة عربية (RTL)
- يعمل بدون إنترنت (Offline-first) باستخدام Room Database

## بيئة التطوير
- Android Studio Iguana or later
- Kotlin 1.9+
- Jetpack Compose
- Java 17

## تعليمات البناء والتشغيل

### متطلبات النظام
لتشغيل المشروع وبناء ملف APK، تحتاج إلى تثبيت:
- JDK 17
- Android SDK (API Level 34)

### البناء (في Android Studio)
1. افتح المشروع في Android Studio.
2. انتظر حتى تكتمل عملية Gradle Sync.
3. اضغط على زر التشغيل (Run) لتثبيت التطبيق على المحاكي أو الجهاز الحقيقي.

### البناء (عبر سطر الأوامر)
**ملاحظة:** يتطلب هذا الأمر وجود Android SDK مثبتًا ومتغير `ANDROID_HOME` معدًا بشكل صحيح. لا يمكن تنفيذ هذا الأمر في بيئات لا تحتوي على Android SDK (مثل Jules).

```bash
./gradlew assembleDebug
```

## CI Build

يعتمد المشروع على GitHub Actions لبناء التطبيق وتشغيل الاختبارات تلقائيًا.

### كيفية عمل CI
- يتم تشغيل الـ Workflow عند عمل Push أو Pull Request على الفروع `main` و `feature/**`.
- يقوم الـ Workflow بتثبيت JDK 17 و Android SDK 34.
- يتم تشغيل اختبارات الوحدة (Unit Tests).
- يتم بناء ملف APK (Debug).

### تحميل الـ APK
بعد نجاح عملية البناء في GitHub Actions، يمكنك تحميل ملف الـ APK من قسم **Artifacts** في صفحة الـ Action Run. الملف يسمى `debug-apk`.

## ملاحظات الاختبار (Testing Notes)
**تنبيه:** اختبارات الوحدة (Unit Tests) تتطلب بيئة Android SDK لتشغيلها بنجاح. لا يمكن تشغيلها داخل بيئة Jules الحالية. يرجى تشغيلها محليًا أو في بيئة CI مجهزة.

```bash
./gradlew test
```

## الهيكلية
يتبع المشروع معمارية Clean Architecture:
- `core/`: منطق الأعمال والنطاق (Domain).
- `data/`: طبقة البيانات (Repositories & Local Data Source via Room).
- `ui/`: واجهات المستخدم باستخدام Jetpack Compose و ViewModels.

## تكامل البيانات (Data Integration)
- **Persistence**: يتم تخزين البيانات محليًا باستخدام Room.
- **Mapping**: يتم تحويل `Entity` إلى `Domain Model` في طبقة المستودعات (Repository Layer).
- **UI State**: تعتمد واجهة المستخدم على `Flow` لمراقبة التغييرات في قاعدة البيانات وتحديث الواجهة تلقائيًا.
