plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.protobuf") version "0.9.6" apply true
}

android {
    namespace = "com.training.ecommerce"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.training.ecommerce"
        minSdk = 23
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
        forEach{
            it.buildConfigField(
                "String","clientServerId","\"1094721950444-4lr9l4sffjv494gp24ff0v6t30jkgj2k.apps.googleusercontent.com\""
            )
            it.resValue(
                "string",
                "facebook_app_id",
                "\"604418305804424\""
            )
            it.resValue(
                "string",
                "fb_login_protocol_scheme",
                "\"fb604418305804424\""
            )
            it.resValue(
                "string",
                "facebook_client_token",
                "\"0f0fce95e73191c342974d58edfd39f1\""
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("androidx.core:core-splashscreen:1.0.0")
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
    implementation("com.google.firebase:firebase-firestore")
    //
    implementation("com.google.firebase:firebase-analytics")
    // crashlytics
    implementation("com.google.firebase:firebase-crashlytics")
    // reactive network
    implementation ("com.github.pwittchen:reactivenetwork-rx2:3.0.8")
    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")

    // Also add the dependency for the Google Play services library and specify its version
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    implementation("androidx.datastore:datastore-preferences-core:1.1.1")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("com.google.protobuf:protobuf-kotlin-lite:4.26.0")

    implementation ("androidx.datastore:datastore:1.0.0")
    implementation ("com.google.protobuf:protobuf-javalite:3.23.4")

    //facebook
    implementation("com.facebook.android:facebook-android-sdk:18.0.1")


}
// Setup protobuf configuration, generating lite Java and Kotlin classes
protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.26.1"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                register("java") {
                    option("lite")
                }
                register("kotlin") {
                    option("lite")
                }
            }
        }
    }
}