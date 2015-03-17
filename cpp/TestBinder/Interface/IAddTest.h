#ifndef _IADDTEST_H__
#define _IADDTEST_H__

#include <utils/RefBase.h>
#include <binder/IInterface.h>
#include <binder/Parcel.h>

namespace android {
class Parcel;

enum {
    TRANSACTION_Add = IBinder::FIRST_CALL_TRANSACTION,
};

class IAddTest: public IInterface {
public:
    static const String16 descriptor;
    static sp<IAddTest> asInterface(const sp<IBinder>& obj);
    virtual const String16& getInterfaceDescriptor() const;
    IAddTest();
    virtual ~IAddTest();

    virtual int Add(int a, int b) = 0;
};

class BnAddTest: public BnInterface<IAddTest> {
public:
    virtual status_t onTransact(uint32_t code, const Parcel& data, Parcel* reply, uint32_t flags = 0);

};

class BpAddTest: public BpInterface<IAddTest> {
public:
    BpAddTest(const sp<IBinder>& impl)
        : BpInterface<IAddTest>(impl){}
    int Add(int a, int b);
};
}
#endif
