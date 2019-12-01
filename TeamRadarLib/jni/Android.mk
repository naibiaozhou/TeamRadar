LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
APP_PLATFORM := android-16
APP_ABI := arm64-v8a armeabi armeabi-v7a 

LOCAL_MODULE    := TeamRadarLib
LOCAL_SRC_FILES := TeamRadarLib.cpp aeslib.c

LOCAL_CFLAGS += \
    -fno-short-enums \
    -D_ANDROID_ \
    

LOCAL_LDLIBS     := -llog 


include $(BUILD_SHARED_LIBRARY)
