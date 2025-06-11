
plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.educapoio"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.educapoio"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    viewBinding.isEnabled = true

    packaging {
        resources {
            excludes += "META-INF/DEPENDENCIES"
            excludes += "rome.properties" // Ignorar arquivos duplicados
            excludes += "rome-utils-0.0.0-SNAPSHOT.jar" // Excluir arquivo duplicado
        }
    }




}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.8.0")
    implementation("androidx.navigation:navigation-ui:2.8.0")
    implementation("androidx.activity:activity:1.9.2")
    implementation("com.google.ar.sceneform:filament-android:1.17.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore:25.1.0")
    implementation("com.google.firebase:firebase-storage-ktx")

    implementation("com.google.firebase:firebase-messaging:23.1.0") // Verifique se a versão está atualizada

    implementation("com.jakewharton.threetenabp:threetenabp:1.4.4")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    androidTestImplementation("org.testng:testng:6.9.6")
    androidTestImplementation("junit:junit:4.12")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    implementation("androidx.work:work-runtime:2.8.1")

    implementation("com.google.guava:guava:30.1.1-android") // Adicione esta linha

    implementation("com.jakewharton.timber:timber:5.0.1") // Dependência opcional para logging



    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
//    implementation("com.github.KwabenBerko:News-API-Java:1.0.0")
    implementation("com.github.rometools:rome:1.18.0")
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    implementation("com.getkeepsafe.taptargetview:taptargetview:1.13.3")









}
