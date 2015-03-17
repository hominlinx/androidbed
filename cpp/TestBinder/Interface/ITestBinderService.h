#ifndef ANDROID_ITESTBINDERSERVICE_H_
#define ANDROID_ITESTBINDERSERVICE_H_

#include <utils/RefBase.h>
#include <binder/IInterface.h>
#include <binder/Parcel.h>


namespace android {

class Parcel;

enum {
	TRANSACTION_BeginAddTest = IBinder::FIRST_CALL_TRANSACTION,
};

class ITestBinderService: public IInterface {
public:
	//DECLARE_META_INTERFACE(TestBinderService);
    static const String16 descriptor;
    static sp<ITestBinderService> asInterface(const sp<IBinder>& obj);
    virtual const String16& getInterfaceDescriptor() const;
    ITestBinderService();
    virtual ~ITestBinderService();

    virtual sp<IAddTest> BeginAddTest() = 0;
};

//BnInterface : public ITestBinderService , public BBinder
//BnInterface 重写了IInterface 的onAsBinder
//
class BnTestBinderService: public BnInterface<ITestBinderService> {
public:
	virtual status_t onTransact(uint32_t code, const Parcel& data,
			Parcel* reply, uint32_t flags = 0);
};

//BpInterface : public ITestBinderService , public BpRefBase
//BpRefBase: public virtual RefBase
//里面有mRemote指向了IBinder
class BpTestBinderService: public BpInterface<ITestBinderService> {
public:
	BpTestBinderService(const sp<IBinder>& impl) :
		BpInterface<ITestBinderService> (impl) {
	}

    sp<IAddTest> BeginAddTest() {
        Parcel data, reply;
        data.writeInterfaceToke(ITestBinderService::getInterfaceDescriptor());
        remote()->transact(TRANSACTION_BeginAddTest, data, &reply);
        sp<IAddTest> sp = IAddTest::asInterface(reply.readStrongBinder());
        return sp;
    }
};

}

#endif /* ANDROID_ITESTBINDERSERVICE_H_ */
