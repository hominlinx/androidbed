#
# Copyright (C) 2011 iPanel Inc.
#


LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := eng

LOCAL_SRC_FILES:= \
    ITestBinderService.cpp \
	testBinder.cpp


LOCAL_SHARED_LIBRARIES := \
	libui libcutils libutils libbinder libsurfaceflinger_client

LOCAL_MODULE:= testbinderclient

LOCAL_C_INCLUDES := \
	$(TOP)/hardware/libhardware/include/ \
	$(TOP)/frameworks/base/include/ \
	$(TOP)/frameworks/tv/include/

LOCAL_PRELINK_MODULE := false

include $(BUILD_EXECUTABLE)
