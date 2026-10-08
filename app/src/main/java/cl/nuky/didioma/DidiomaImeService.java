package cl.nuky.didioma;

import android.inputmethodservice.InputMethodService;
import android.view.View;
import android.view.inputmethod.InputConnection;

public class DidiomaImeService extends InputMethodService implements DidiomaKeyboardView.Listener {
    private DidiomaKeyboardView keyboard;

    @Override
    public View onCreateInputView() {
        keyboard = new DidiomaKeyboardView(this);
        keyboard.setListener(this);
        return keyboard;
    }

    @Override
    public void onCommit(String value) {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) ic.commitText(value, 1);
    }

    @Override
    public void onBackspace() {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) ic.deleteSurroundingText(1, 0);
    }

    @Override
    public void onEnter() {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) ic.commitText("\n", 1);
    }

    @Override
    public void onNextKeyboard() {
        if (android.os.Build.VERSION.SDK_INT >= 28) switchToNextInputMethod(false);
        else {
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.showInputMethodPicker();
        }
    }
}
