plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

repositories{
    mavenCentral()
    flatDir{
        dirs("libs")
    }
}

android {
    namespace = "com.ash.photobomb"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ash.photobomb"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    constraints {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.0") {
            because("kotlin-stdlib-jdk7 is now a part of kotlin-stdlib")
        }
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.0") {
            because("kotlin-stdlib-jdk8 is now a part of kotlin-stdlib")
        }
    }

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("com.google.android.gms:play-services-vision:20.1.3")
    implementation("com.google.firebase:firebase-ml-vision-barcode-model:16.1.2")
    implementation("com.google.mlkit:barcode-scanning-common:17.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    androidTestImplementation("de.hdodenhof:circleimageview:3.1.0")
    implementation ("androidx.navigation:navigation-ui:2.7.7")

    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-messaging:23.4.1")
    implementation("com.google.firebase:firebase-dynamic-links:latest_version")
    implementation("com.google.firebase:firebase-invites:17.0.0")
    implementation("com.google.firebase:firebase-dynamic-links:21.2.0")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-core:21.1.1")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.firebase:firebase-analytics:21.5.1")

    // to add load image url on imageView
    implementation("com.squareup.picasso:picasso:2.71828")
    // for circle Image
    implementation ("de.hdodenhof:circleimageview:3.1.0")


    implementation ("com.squareup.retrofit2:retrofit:2.7.1")
    implementation ("com.squareup.retrofit2:converter-gson:2.1.0")


    implementation ("com.squareup.okhttp3:okhttp:4.9.2")

    //glid
    implementation ("com.github.bumptech.glide:glide:4.10.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.10.0")


    // S3 dependencies ...
    implementation ("com.amazonaws:aws-android-sdk-s3:2.16.7")
    implementation ("com.amazonaws:aws-android-sdk-ddb:2.16.7")
    implementation ("com.amazonaws:aws-android-sdk-core:2.16.7")
    implementation ("com.amazonaws:aws-android-sdk-mobile-client:2.16.7")
    implementation ("com.amazonaws:aws-android-sdk-cognitoauth:2.16.7")
    implementation ("com.amazonaws:aws-android-sdk-cognitoidentityprovider:2.16.7")



    implementation ("com.airbnb.android:lottie:2.8.0")
    implementation ("com.github.Gkemon:Easy-Android-Loading:1.2")

    
    implementation ("io.github.tashilapathum:please-wait:0.4.0")

    // for faceBook login
    implementation ("com.facebook.android:facebook-android-sdk:latest.release")
    implementation ("com.facebook.android:facebook-android-sdk:[8,9)")
//    implementation ("com.facebook.android:facebook-login:5.0.0")


    implementation ("com.google.android.gms:play-services-auth:21.0.0")


    // FOR PAYMENT
    implementation ("com.razorpay:checkout:1.5.16")


}