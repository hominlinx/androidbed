#!/bin/bash
trap exit ERR
export MODULE_NAME="epolldemo"
ndk-build
#adb push libs/armeabi/${MODULE_NAME} /data/local
#echo ""
#echo "------ run ${MODULE_NAME} -------"
#echo ""
#adb shell /data/local/${MODULE_NAME}
rm -r libs
rm -r obj
#echo ""