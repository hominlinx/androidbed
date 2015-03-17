#ifndef _ADDTESTSERVICE_H__
#define _ADDTESTSERVICE_H__

#include "../Interface/IAddTest.h"

namespace android {

class AddTestService: public BnAddTest {
public:
    int Add(int a, int b);
};
}
