package com.applikey.mattermost.utils.image;

import android.support.annotation.Nullable;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.web.ServerUrlFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImagePathHelper {

    private static final String IMAGE_EXTESTION_PATTERN =
            "([^\\s]+(\\.(?i)(jpg|png))$)";
    private final Pattern mImageExtensionPattern;

    private final ServerUrlFactory mServerUrlFactory;
    private final Prefs mPrefs;

    public ImagePathHelper(ServerUrlFactory serverUrlFactory, Prefs prefs) {
        mServerUrlFactory = serverUrlFactory;
        mPrefs = prefs;
        mImageExtensionPattern = Pattern.compile(IMAGE_EXTESTION_PATTERN);
    }

    public String getProfilePicPath(String userId) {
        return mServerUrlFactory.getServerUrl() + "api/v3/users/" + userId + "/image";
    }

    @Nullable
    public String getAttachmentImageUrl(String teamId, String filename) {
        final int majorVersion = mPrefs.getServerVersionMajor();
        final int minorVersion = mPrefs.getServerVersionMinor();
        // TODO: In 3.5 the whole concept of working with files was changed, so we need to have file id instead of
        // filename here. So, in case of 3.5+ we just return null now, and ignore an ability to get file. 3.5
        // attachments should be supported later
        if (majorVersion == 3 && minorVersion >= 5) {
            return null;
        }
        return mServerUrlFactory.getServerUrl() + "api/v3/teams/" + teamId + "/files/get" + filename;
    }

    public boolean isImage(String fileName) {
        final Matcher matcher = mImageExtensionPattern.matcher(fileName);
        return matcher.matches();
    }
}
