plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.imagetool.bgremover"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.imagetool.bgremover"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {

        debug {
            buildConfigField(type = "String",name = "AD_ID_BANNER", value = "\"ca-app-pub-3940256099942544/9214589741\"")
            buildConfigField(type = "String",name = "AD_ID_INTERSTITIAL", value = "\"ca-app-pub-3940256099942544/1033173712\"")

        }

        release {

            buildConfigField(type = "String",name = "AD_ID_BANNER", value = "\"ca-app-pub-7088294054901025/9771153660\"")
            buildConfigField(type = "String",name = "AD_ID_INTERSTITIAL", value = "\"ca-app-pub-7088294054901025/2647342113\"")

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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.core.splashscreen)
    implementation (libs.lottie.compose)
    implementation(libs.coil.compose)
    implementation(libs.android.image.cropper)
    implementation(libs.play.services.mlkit.subject.segmentation)
    implementation (libs.accompanist.permissions)
    implementation (libs.play.services.ads)
}