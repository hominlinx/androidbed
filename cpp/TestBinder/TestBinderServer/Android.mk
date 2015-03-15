LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := eng


LOCAL_C_INCLUDES := \
    $(TOP)/frameworks/base/include/ \
    $(TOP)/frameworks/tv/include/ \




LOCAL_SRC_FILES:= \
	main_testBinder.cpp \
	TestBinderService.cpp \
	../TestBinderClient/ITestBinderService.cpp

LOCAL_SHARED_LIBRARIES := \
	libutils \
	libbinder \
	libandroid_runtime \
	libui


LOCAL_MODULE:= testbinderserver

LOCAL_PRELINK_MODULE := false

include $(BUILD_EXECUTABLE)

