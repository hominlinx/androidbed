#define LOG_TAG "ITeeveePlayerService"

#include <utils/Log.h>

#include "../TestBinderServer/ITestBinderService.h"

namespace android {

//IMPLEMENT_META_INTERFACE(TestBinderService, "android.test.ITestBinderService");
// frameworks/base/include/binder/IInterface.h
//#define IMPLEMENT_META_INTERFACE(INTERFACE, NAME)           /

const String16 ITestBinderService::descriptor("TestBinderService");
const String16& ITestBinderService::getInterfaceDescriptor() const {
    return ITestBinderService::descriptor;
}

sp<ITestBinderService> ITestBinderService::asInterface(const sp<IBinder>& obj) {
    sp<ITestBinderService> intr;
    if (obj != NULL) {
        intr = static_cast<ITestBinderService*> (
            obj->queryLocalInterface(ITestBinderService::descriptor).get()
            );
        if (intr == NULL) {
            intr = new BpTestBinderService(obj);
        }
    }
    return intr;
}

ITestBinderService::ITestBinderService(){}
ITestBinderService::~ITestBinderService() {}

// ----------------------------------------------------------------------

status_t BnTestBinderService::onTransact(uint32_t code, const Parcel& data,
		Parcel* reply, uint32_t flags) {
	switch (code) {
	case TEST_ADD: {

		CHECK_INTERFACE(ITestBinderService, data, reply);
		int a = data.readInt32();
		int b = data.readInt32();
		LOGI("Enter BnTestBinderService add,a = %d , b = %d", a, b);
		int sum = 0;
		sum  = add(a, b);
		LOGI("BnTestBinderService sum = %d", sum);
		 reply->writeInt32(sum);
		return sum;
	}
	default:
		return BBinder::onTransact(code, data, reply, flags);
	}
}

}
