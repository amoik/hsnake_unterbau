LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := libssl
LOCAL_SRC_FILES := prec/$(TARGET_ARCH_ABI)/libssl.a
LOCAL_C_INCLUDES += include

include $(PREBUILT_STATIC_LIBRARY)
