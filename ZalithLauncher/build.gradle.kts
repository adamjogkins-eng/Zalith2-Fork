FULL_EXISTING_BUILD_FILE_WITH_ONLY_THESE_CHANGES:
- Replace:
    implementation(libs.dagger.hilt.android)
    ksp(libs.androidx.hilt.navigation.compose)
- With:
    implementation(libs.dagger.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.dagger.hilt.android.compiler)

Every other line, plugin, Android configuration, signing config, split ABI logic, buildKeys block, dependency, and test dependency remains exactly as in the current file.