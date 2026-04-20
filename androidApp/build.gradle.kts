plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.koin.compiler)
}

android {
    namespace = "io.capistudio.deckamushi"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "io.capistudio.deckamushi"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}


dependencies {
    implementation(project(":shared"))

    // Required for the Android Activity that hosts the shared Compose UI
    implementation(libs.androidx.activity.compose)

    // The shared module's HttpClient uses the OkHttp engine on Android; ensure the app has it on the classpath.
    implementation(libs.ktor.client.okhttp)

    // Needed so Compose Multiplatform resources (composeResources/) are available at runtime on Android
    implementation(libs.compose.components.resources)

    //deleta esses 2 depois
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material3)

    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)

    //deleta esses 2 depois
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)

    debugImplementation(libs.compose.uiTooling)
}


