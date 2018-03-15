# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\InstallationPackage\adt-bundle-windows-x86_64-20130729\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontusemixedcaseclassnames
	-dontshrink
	-dontoptimize
	-dontwarn com.google.android.maps.**
	-dontwarn android.webkit.WebView
	-dontwarn com.umeng.**
	-dontwarn com.tencent.weibo.sdk.**
	-dontwarn com.facebook.**
	-keep public class javax.**
	-keep public class android.webkit.**
	-dontwarn android.support.v4.**
	-keep enum com.facebook.**
	-keepattributes Exceptions,InnerClasses,Signature
	-keepattributes *Annotation*
	-keepattributes SourceFile,LineNumberTable

	-keep class **.R
    -keep class **.R$* {
        <fields>;
    }

     -keepclasseswithmembernames class * {
      native <methods>;
     }

     # We want to keep methods in Activity that could be used in the XML attribute onClick
     -keepclassmembers class * extends android.app.Activity {
        public void *(android.view.View);
     }

     # keep setters in Views so that animations can still work.
     # see http://proguard.sourceforge.net/manual/examples.html#beans
     -keepclassmembers public class * extends android.view.View {
        void set*(***);
        *** get*();
     }

     -verbose
     -optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

	-keep public interface com.facebook.**
	-keep public interface com.tencent.**
	-keep public interface com.umeng.socialize.**
	-keep public interface com.umeng.socialize.sensor.**
	-keep public interface com.umeng.scrshot.**
	-keep class com.android.dingtalk.share.ddsharemodule.** { *; }
	-keep public class com.umeng.socialize.* {*;}


	-keep class com.facebook.**
	-keep class com.facebook.** { *; }
	-keep class com.umeng.scrshot.**
	-keep public class com.tencent.** {*;}
	-keep class com.umeng.socialize.sensor.**
	-keep class com.umeng.socialize.handler.**
	-keep class com.umeng.socialize.handler.*
	-keep class com.umeng.weixin.handler.**
	-keep class com.umeng.weixin.handler.*
	-keep class com.umeng.qq.handler.**
	-keep class com.umeng.qq.handler.*
	-keep class UMMoreHandler{*;}
	-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
	-keep class com.tencent.mm.sdk.modelmsg.** implements 	com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}
	-keep class im.yixin.sdk.api.YXMessage {*;}
	-keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}
	-keep class com.tencent.mm.sdk.** {
  	 *;
	}
	-dontwarn twitter4j.**
	-keep class twitter4j.** { *; }

	-keep class com.tencent.** {*;}
	-dontwarn com.tencent.**
	-keep public class com.umeng.com.umeng.soexample.R$*{
    public static final int *;
	}
	-keep public class com.linkedin.android.mobilesdk.R$*{
    public static final int *;
		}
	-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
	}

	-keep class com.tencent.open.TDialog$*
	-keep class com.tencent.open.TDialog$* {*;}
	-keep class com.tencent.open.PKDialog
	-keep class com.tencent.open.PKDialog {*;}
	-keep class com.tencent.open.PKDialog$*
	-keep class com.tencent.open.PKDialog$* {*;}

	-keep class com.sina.** {*;}
	-dontwarn com.sina.**
	-keep class  com.alipay.share.sdk.** {
	   *;
	}
	-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
	}
	-keepclassmembers class * implements java.io.Serializable {
        static final long serialVersionUID;
        private void writeObject(java.io.ObjectOutputStream);
        private void readObject(java.io.ObjectInputStream);
        java.lang.Object writeReplace();
        java.lang.Object readResolve();
    }


	-keep class com.linkedin.** { *; }
	-keepattributes Signature

	-ignorewarnings
    -dontwarn cn.jpush.**
    -keep class cn.jpush.** { *; }

    -dontwarn com.squareup.**
    -keep class com.squareup.** { *; }

    -dontwarn com.octo.**
    -keep class com.octo.** { *; }

    -dontwarn de.**
    -keep class de.** { *; }

    -dontwarn javax.**
    -keep class javax.** { *; }

    -dontwarn org.**
    -keep class org.** { *; }

    -dontwarn u.aly.**
    -keep class u.aly.** { *; }

    -dontwarn uk.**
    -keep class uk.** { *; }

    -dontwarn com.facebook.**
    -keep class com.facebook.** { *; }

    -dontwarn com.google.**
    -keep class com.google.** { *; }

    -keepattributes SourceFile,LineNumberTable

    -keep class com.baidu.** {*;}
    -keep class vi.com.** {*;}
    -dontwarn com.baidu.**

     -dontwarn cn.sharesdk.**
     -keep class cn.sharesdk.** { *; }

     -dontwarn com.nostra13.universalimageloader.**
     -keep class com.nostra13.universalimageloader.** { *; }
     -keep class it.sephiroth.android.library.exif2.**{ *; }

     -keep class org.lasque.tusdk.**{public *; protected *; }
     -keep class org.lasque.tusdk.core.utils.image.GifHelper{ *; }