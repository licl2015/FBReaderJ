/*
 * Copyright (C) 2007-2013 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.android.fbreader;

import android.content.Context;
import android.content.Intent;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yotadevices.sdk.utils.RotationAlgorithm;

import com.yotadevices.fbreader.FBReaderYotaService;

import org.geometerplus.zlibrary.core.resources.ZLResource;
import org.geometerplus.zlibrary.ui.android.R;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidLibrary;

import org.geometerplus.fbreader.fbreader.FBReaderApp;

class YotaSwitchScreenAction extends FBAndroidAction {
	private final boolean mySwitchToBack;

	YotaSwitchScreenAction(FBReader baseActivity, FBReaderApp fbreader, boolean switchToBack) {
		super(baseActivity, fbreader);
		mySwitchToBack = switchToBack;
	}

	@Override
	public boolean isVisible() {
		final ZLAndroidLibrary zlibrary = (ZLAndroidLibrary)ZLAndroidLibrary.Instance();
		return zlibrary.YotaDrawOnBackScreenOption.getValue() != mySwitchToBack;
	}

	@Override
	protected void run(Object ... params) {
		switchScreen(mySwitchToBack);
	}

	private void switchScreen(boolean toBack) {
		final Context context = BaseActivity.getApplicationContext();
		final Intent serviceIntent = new Intent(context, FBReaderYotaService.class);
		final View mainView = BaseActivity.findViewById(R.id.main_view);
		final View mainHiddenView = BaseActivity.findViewById(R.id.yota_main_hidden_view);

		if (toBack) {
			context.startService(serviceIntent);
			setupHiddenView(mainHiddenView);
			mainView.setVisibility(View.GONE);
			mainHiddenView.setVisibility(View.VISIBLE);
			RotationAlgorithm.getInstance(context).turnScreenOffIfRotated();
		} else {
			context.stopService(serviceIntent);
			mainView.setVisibility(View.VISIBLE);
			mainHiddenView.setVisibility(View.GONE);
		}

		final ZLAndroidLibrary zlibrary = (ZLAndroidLibrary)ZLAndroidLibrary.Instance();
		zlibrary.YotaDrawOnBackScreenOption.setValue(toBack);

		Reader.clearTextCaches();
	}

	private void setupHiddenView(View mainView) {
		final ZLResource yotaResource = ZLResource.resource("yota");

		final TextView text = (TextView)mainView.findViewById(R.id.yota_hidden_view_text);
		text.setText(yotaResource.getResource("frontScreenMessage").getValue());

		final Button button = (Button)mainView.findViewById(R.id.yota_hidden_view_button);
		button.setText(yotaResource.getResource("frontScreenButton").getValue());
		button.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				switchScreen(false);
			}
		});
	}
}
