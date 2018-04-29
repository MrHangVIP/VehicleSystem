package com.lhj.vehiclesystem.util;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Songzhihang on 2018/3/3.
 */
public class LoginUtil {

    public static void listenEditView(final View button, final EditText... editTexts){
        for (EditText editText:editTexts){
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    boolean b = !TextUtils.isEmpty(editTexts[0].getText().toString());
                    for (int i = 1; i < editTexts.length; i++) {
                        b = b & !TextUtils.isEmpty(editTexts[i].getText().toString());
                    }
                    if (b == button.isEnabled()) {
                        return;
                    }
                    button.setEnabled(b);
                }
            });
        }

    }
}
