package com.applikey.mattermost.views;

import android.content.Context;
import android.util.AttributeSet;

import com.applikey.mattermost.utils.MarkdownParser;
import com.vanniktech.emoji.EmojiTextView;

public class ContentTextView extends EmojiTextView {

    public ContentTextView(final Context context) {
        super(context);
        init();
    }

    public ContentTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ContentTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            setText(getParsedMarkdownText(getText().toString()));
        }
    }

    @Override
    public void setText(CharSequence rawText, BufferType type) {
        final CharSequence text = rawText == null ? "" : rawText;
        final CharSequence parsedMarkdownText = getParsedMarkdownText(text.toString());
        super.setText(parsedMarkdownText, type);
    }

    private CharSequence getParsedMarkdownText(String markdownText) {
        return MarkdownParser.parseToSpannable(getContext(), markdownText);
    }
}
