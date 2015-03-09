#define LOG_TAG "TestBinserService"

#include <utils/Log.h>
#include <nativehelper/jni.h>
#include <nativehelper/JNIHelp.h>
#include <android_runtime/AndroidRuntime.h>
#include <binder/IServiceManager.h>
#include "../TestBinderServer/ITestBinderService.h"


#include "TestBinderService.h"

using namespace android;

int main(int argc, char** argv)
 {
	int sum = 0;
	sp<ITestBinderService> mTestBinserService;
	if (mTestBinserService.get() == 0) {
		sp<IServiceManager> sm = defaultServiceManager();
		sp<IBinder> binder;
		do {
			binder = sm->getService(String16("my.test.binder"));
			if (binder != 0)
				break;
				LOGI("getService fail");
			usleep(500000); // 0.5 s
		} while (true);
		mTestBinserService = interface_cast<ITestBinderService> (binder);
		LOGE_IF(mTestBinserService == 0, "no ITestBinserService!?");
	}
	sum = mTestBinserService->add(3, 4);
	LOGI("sum = %d", sum);
	return 0;

}


