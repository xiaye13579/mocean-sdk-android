package com.moceanmobile.mast;

import android.app.Dialog;
import android.content.Context;

public class ModalDialog extends Dialog
{
	public ModalDialog(Context context)
	{
		super(context, android.R.style.Theme_NoTitleBar_Fullscreen);
	}
}
