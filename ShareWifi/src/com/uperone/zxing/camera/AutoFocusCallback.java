/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.uperone.zxing.camera;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

final class AutoFocusCallback implements Camera.AutoFocusCallback {
	void setHandler(Handler autoFocusHandler, int autoFocusMessage) {
		mAutoFocusHandler = autoFocusHandler;
		mAutoFocusMessage = autoFocusMessage;
	}

	public void onAutoFocus(boolean success, Camera camera) {
		if (mAutoFocusHandler != null) {
			Message message = mAutoFocusHandler.obtainMessage(
					mAutoFocusMessage, success);
			mAutoFocusHandler
					.sendMessageDelayed(message, AUTOFOCUS_INTERVAL_MS);
			mAutoFocusHandler = null;
		} else {
			Log.d(TAG, "Got auto-focus callback, but no handler for it");
		}
	}

	private static final String TAG = AutoFocusCallback.class.getSimpleName();
	private static final long AUTOFOCUS_INTERVAL_MS = 1500L;
	private int mAutoFocusMessage = 0;
	private Handler mAutoFocusHandler = null;
}
