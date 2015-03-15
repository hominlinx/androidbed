#ifndef ANDROID_ITESTBINDERSERVICE_H_
#define ANDROID_ITESTBINDERSERVICE_H_

#include <utils/RefBase.h>
#include <binder/IInterface.h>
#include <binder/Parcel.h>


namespace android {

class Parcel;

enum {
	TEST_ADD = IBinder::FIRST_CALL_TRANSACTION,
};

class ITestBinderService: public IInterface {
public:
	//DECLARE_META_INTERFACE(TestBinderService);
    static const String16 descriptor;
    static sp<ITestBinderService> asInterface(const sp<IBinder>& obj);
    virtual const String16& getInterfaceDescriptor() const;
    ITestBinderService();
    virtual ~ITestBinderService();

	virtual int add(int a, int b) = 0;
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

	int add(int a, int b) {

		Parcel data, reply;
		LOGI("Enter BpTestBinderService add,a = %d , b = %d", a, b);
		data.writeInterfaceToken(ITestBinderService::getInterfaceDescriptor());
		data.writeInt32(a);
		data.writeInt32(b);
		remote()->transact(TEST_ADD, data, &reply);
		int sum = reply.readInt32();
		LOGI("BpTestBinderService sum = %d", sum);
		return sum;
	}
};

}

#endif /* ANDROID_ITESTBINDERSERVICE_H_ */
