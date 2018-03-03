# Annotations
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.concurrent.GuardedBy
-dontwarn javax.annotation.ParametersAreNonnullByDefault

# Firebase
-keepattributes Signature
-keepclassmembers class com.pandulapeter.firebase.data.models.** {
  *;
}