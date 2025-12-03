plugins {
    alias(libs.plugins.android.application)

    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.afmobile"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.afmobile"
        minSdk = 24
        targetSdk = 36
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform("com.google.firebase:firebase-bom:34.6.0"))
    // Importar a BoM do Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-firestore")

    // Gson para JSON da API (Aula 11)
    implementation("com.google.code.gson:gson:2.10.1")

    // Picasso (Para carregar a imagem do Pokémon)
    implementation ("com.squareup.picasso:picasso:2.8")
}