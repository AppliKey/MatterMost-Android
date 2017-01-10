package com.applikey.mattermost.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.applikey.mattermost.App;
import com.applikey.mattermost.R;
import com.applikey.mattermost.web.images.ImageLoader;

import javax.inject.Inject;

public class ImageAttachmentDialogFragment extends DialogFragment {

    public static final String ARG_FILE_URL = "arg-file-url";

    private String mAbsoluteFileUrl;

    @Inject
    ImageLoader mImageLoader;

    private ImageView mImage;

    public static ImageAttachmentDialogFragment newInstance(String url) {
        final ImageAttachmentDialogFragment dialog = new ImageAttachmentDialogFragment();
        final Bundle args = new Bundle();
        args.putString(ARG_FILE_URL, url);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAbsoluteFileUrl = getArguments().getString(ARG_FILE_URL);

        App.getUserComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.dialog_attachment_image, container, false);
        mImage = (ImageView) root.findViewById(R.id.image);

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mImageLoader.displayImage(mAbsoluteFileUrl, mImage);
    }
}
