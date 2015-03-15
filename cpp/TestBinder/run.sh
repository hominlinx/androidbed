#!/bin/bash

PWDAA=`pwd`
TEMPFILE=$PWDAA/temp.txt
SERVERDIR=$PWDAA/TestBinderServer
CLIENTDIR=$PWDAA/TestBinderClient


######
echo "mm client"
cd $CLIENTDIR
mm 2>&1 | tee $TEMPFILE

#####
echo "mm server"
cd $SERVERDIR
mm 2>&1 | tee $TEMPFILE

adb push ~/opensource/android/cm/out/target/product/marvel/system/bin/testbinderclient /system/bin
adb push ~/opensource/android/cm/out/target/product/marvel/system/bin/testbinderserver /system/bin
