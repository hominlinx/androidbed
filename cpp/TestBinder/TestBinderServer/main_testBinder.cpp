
#include <binder/IPCThreadState.h>
#include <binder/ProcessState.h>
#include <binder/IServiceManager.h>
#include <utils/Log.h>


#include "TestBinderService.h"

using namespace android;

int main(int argc, char** argv)
 {
	
	sp<ProcessState> proc(ProcessState::self());
	sp<IServiceManager> sm = defaultServiceManager();
	LOGI("TestBinderService before");
	TestBinderService::instantiate();
	LOGI("TestBinderService End");
	ProcessState::self()->startThreadPool();
	IPCThreadState::self()->joinThreadPool();
	LOGI("TestBinderService End");
	return 0;

}


