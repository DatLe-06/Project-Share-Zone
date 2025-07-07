package org.example.share_zone.util;

import org.example.share_zone.model.Media;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Utils {
    public static Media.MediaType validExtensions(String extensions) {
        final List<String> ALLOWED_EXTENSIONS_IMAGE = Arrays.asList("jpg", "jpeg", "png");
        final List<String> ALLOWED_EXTENSIONS_VIDEO = Collections.singletonList("mp4");

        if (ALLOWED_EXTENSIONS_IMAGE.contains(extensions)) return Media.MediaType.IMAGE;
        else if (ALLOWED_EXTENSIONS_VIDEO.contains(extensions)) return Media.MediaType.VIDEO;
        else return null;
    }
}
