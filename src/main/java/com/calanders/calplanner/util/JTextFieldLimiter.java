package com.calanders.calplanner.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class JTextFieldLimiter extends PlainDocument {
    private final int limit;

    public JTextFieldLimiter(int limit) {
        super();
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit can not be <= 0");
        }
        this.limit = limit;
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (str == null) return;
        if ((getLength() + str.length()) <= limit) {
            super.insertString(offs, str, a);
        }
    }
}
