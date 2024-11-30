plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.appeng"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.appeng"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures{
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.storage)
    implementation(libs.recyclerview)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.gbutton)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.retrofit) // Thay 2.9.0 với phiên bản mới nhất nếu cần
    //noinspection UseTomlInstead
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Converter để xử lý JSON
    implementation(libs.firebase.database)
    implementation(libs.play.services.measurement)
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)
    implementation(libs.firebase.appcheck.playintegrity)
    implementation(libs.rxjava)
    implementation(libs.rxandroid)
    implementation(libs.androidx.annotation)
    implementation(libs.splashscreen)




}
