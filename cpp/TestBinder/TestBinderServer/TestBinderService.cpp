#define LOG_TAG "TestBinderService"

#include <utils/Log.h>
#include <binder/IServiceManager.h>
#include <binder/IPCThreadState.h>

#include "TestBinderService.h"
#include "../Interface/IAddTest.h"
static int debug_flag = 1;
namespace android {

void TestBinderService::instantiate() {
	LOGI("Enter TestBinderService::instantiate");
	status_t st = defaultServiceManager()->addService(
			String16("my.test.binder"), new TestBinderService());
	LOGD("ServiceManager addService ret=%d", st);
	LOGD("instantiate> end");
}

TestBinderService::TestBinderService() {
	LOGD(" TestBinderServicet");
}

TestBinderService::~TestBinderService() {
	LOGD("TestBinderService destroyed,never destroy normally");
}

int TestBinderService::add(int a,int b) {

	LOGI("TestBinderService::add a = %d, b = %d.", a , b);	
	return a+b;
}

sp<IAddTest> TestBinderService::BeginAddTest() {
    sp<IAddTest> addTest = new AddTestService();
    return addTest;
}

}
