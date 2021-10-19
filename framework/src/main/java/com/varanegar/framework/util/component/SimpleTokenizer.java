package com.varanegar.framework.util.component;

public class SimpleTokenizer implements SearchBox.Tokenizer {
    @Override
    public String[] tokenize(String text) {
        if (text == null || text.isEmpty())
            return new String[]{};
        return text.split("\\s+");
    }
}
