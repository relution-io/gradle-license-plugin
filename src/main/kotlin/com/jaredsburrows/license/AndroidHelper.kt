package com.jaredsburrows.license

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.FeatureExtension
import com.android.build.gradle.FeaturePlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.DomainObjectSet
import org.gradle.api.Project
import java.util.Locale

/** Configure for Android projects. */
internal fun Project.configureAndroidProject() {
  project.plugins.all {
    when (it) {
      is AppPlugin -> {
        project.extensions.getByType(AppExtension::class.java).run {
          configureVariant(applicationVariants)
        }
      }
      is FeaturePlugin -> {
        project.extensions.getByType(FeatureExtension::class.java).run {
          configureVariant(featureVariants)
          configureVariant(libraryVariants)
        }
      }
      is LibraryPlugin -> {
        project.extensions.getByType(LibraryExtension::class.java).run {
          configureVariant(libraryVariants)
        }
      }
    }
  }
}

private fun Project.configureVariant(variants: DomainObjectSet<out BaseVariant>? = null) {
  // Configure tasks for all variants
  variants?.all { variant ->
    val name = variant.name.replaceFirstChar {
      if (it.isLowerCase()) {
        it.titlecase(Locale.getDefault())
      } else {
        it.toString()
      }
    }

    tasks.register("license${name}Report", AndroidLicenseReportTask::class.java) {
      it.assetDirs = (extensions.getByName("android") as BaseExtension)
        .sourceSets
        .getByName("main")
        .assets
        .srcDirs
        .toList()
      it.variantName = variant.name
    }
  }
}
