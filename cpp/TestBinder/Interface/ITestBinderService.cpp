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
        case TRANSACTION_BeginAddTest: {
            CHECK_INTERFACE(ITestBinderService, data, reply);
            LOGI("Enter BnTestBinderService ");
            sp<IAddTest> _result = BeginAddTest();
            reply.writeStrongBinder(_result->asBinder());
            return NO_ERROR;
        }
        default:
            return BBinder::onTransact(code, data, reply, flags);
	}
}

}
