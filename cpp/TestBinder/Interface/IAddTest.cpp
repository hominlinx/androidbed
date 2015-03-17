#define LOG "IAddTest"
#include <utils/Log.h>

#include "IAddTest.h"

namespace android {

const String16 IAddTest::descriptor("AddTest");
const String16& IAddTest::getInterfaceDescriptor() const {
    return IAddTest::descriptor;
}

sp<IAddTest> IAddTest::asInterface(const sp<IBinder>& obj) {
    sp<IAddTest> intr;
    if (obj != NULL) {
        intr = static_cast<IAddTest*>(obj->queryLocalInterface(IAddTest::descriptor).get());
        if (intr == NULL) {
            intr = new BpAddTest(obj);
        }
    }
    return intr;
}

IAddTest::IAddTest(){}
IAddTest::~IAddTest(){}

status_t BnAddTest::onTransact(uint32_t code, const Parcel& data, Parcel* reply, uint32_t flags) {
    switch(code) {
        case TRANSACTION_Add: {
            CHECK_INTERFACE(IAddTest, data, reply);
            int a = data.readInt32();
            int b = data.readInt32();
            LOGI("Enter BnAddTest a[%d] b[%d]", a, b);
            int sum = Add(a, b);
            reply->writeInt32(sum);
            return sum;
        }
        default:
            return BBinder::onTransact(code, data, reply, flags);
    }
}

int BpAddTest::Add(int a, int b) {
    Parcel data, reply;
    LOGI("Enter BpAddTest a[%d] b[%d]",a, b);
    data.writeInterfaceToken(IAddTest::getInterfaceDescriptor());
    data.writeInt32(a);
    data.writeInt32(b);
    remote()->transact(TRANSACTION_Add, data, &reply);
    int sum = reply.readInt32();
    LOGI("BpAddTest sum=%d", sum);
}
}
