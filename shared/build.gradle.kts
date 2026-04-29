import com.codingfeline.buildkonfig.compiler.FieldSpec
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    id("com.android.kotlin.multiplatform.library")
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.koin.compiler)
}

kotlin {
    // Required by the new AGP KMP Android library plugin
    android {
        namespace = "io.capistudio.deckamushi.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        androidResources.enable = true
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            linkerOpts("-lsqlite3")
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Compose
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.compose.material3.icons)
            implementation(libs.androidx.navigation.compose)

            // Koin
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)

            // SQLDelight
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines.extensions)
            implementation(libs.sqldelight.paging)

            // Networking / Serialization
            implementation(libs.ktor.client.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.serialization.kotlinx.json)

            //Logging
            implementation(libs.kermit)

            //Paging
            implementation(libs.androidx.paging.common)
            implementation(libs.androidx.paging.compose)
        }

        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.sqldelight.android.driver)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.koin.android)
            implementation(libs.coil)
            implementation(libs.coil.okhttp)
            implementation(libs.androidx.camera)
            implementation(libs.androidx.camera.lifecycle)
            implementation(libs.androidx.camera.view)
            implementation(libs.text.recognition)
            implementation(libs.google.accompanist)
        }

        iosMain.dependencies {
            implementation(libs.sqldelight.native.driver)
            implementation(libs.ktor.client.darwin)
            implementation(libs.coil)
            implementation(libs.coil.ktor)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "deckamushi.shared.generated.resources"
    generateResClass = always
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("io.capistudio.deckamushi.db")
        }
    }
}

buildkonfig {
    val localProperties = Properties().apply {
        val file = rootProject.file("local.properties")
        if (file.exists()) load(file.inputStream())
    }

    val isDemo = project.findProperty("demoMode")?.toString()?.toBoolean() ?: false

    val (owner, repo) = if (isDemo) {
        Pair(
            localProperties.getProperty("GITHUB_DATA_OWNER"),
            localProperties.getProperty("GITHUB_DATA_REPO")
        )
    } else {
        Pair("geshner", "deckamushi")
    }

    val finalBaseUrl = "https://api.github.com/repos/$owner/$repo/contents"

    packageName = "io.capistudio.deckamushi"
    defaultConfigs {
        buildConfigField(
            FieldSpec.Type.STRING,
            "API_KEY", localProperties.getProperty("GITHUB_PAT") ?: ""
        )
        buildConfigField(
            FieldSpec.Type.STRING,
            "BASE_URL", finalBaseUrl
        )
        buildConfigField(
            FieldSpec.Type.BOOLEAN,
            "IS_DEMO", "$isDemo"
        )
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}
