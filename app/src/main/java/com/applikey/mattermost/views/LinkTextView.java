package com.applikey.mattermost.views;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.applikey.mattermost.utils.MarkdownParser;
import com.vanniktech.emoji.EmojiTextView;

public class LinkTextView extends EmojiTextView {

    public LinkTextView(final Context context) {
        super(context);
        init();
    }

    public LinkTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LinkTextView(final Context context, final AttributeSet attrs, final int defStyle) {
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
        setMovementMethod(NoLongClickMovementMethod.getInstance());
    }

    private CharSequence getParsedMarkdownText(String markdownText) {
        return MarkdownParser.parseToSpannable(getContext(), markdownText);
    }

    private static class NoLongClickMovementMethod extends LinkMovementMethod {

        private final long mLongPressTimeout = ViewConfiguration.getLongPressTimeout();
        private long mStartTime;

        private static final NoLongClickMovementMethod mLinkMovementMethod = new NoLongClickMovementMethod();

        @Override
        public boolean onTouchEvent(android.widget.TextView widget, android.text.Spannable buffer, MotionEvent event) {
            final int action = event.getAction();

            if (action == MotionEvent.ACTION_DOWN) {
                mStartTime = System.currentTimeMillis();
            }

            if (action == MotionEvent.ACTION_UP) {
                final long currentTime = System.currentTimeMillis();
                if (currentTime - mStartTime >= mLongPressTimeout) {
                    return true;
                }
            }
            return super.onTouchEvent(widget, buffer, event);
        }

        public static android.text.method.MovementMethod getInstance() {
            return mLinkMovementMethod;
        }
    }
}
