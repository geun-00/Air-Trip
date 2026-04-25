package project.member.application.in.command.model;

import project.common.application.model.UploadFile;

@FunctionalInterface
public interface ProfileImageSource {

    String uploadWith(String key, Handler handler);

    static ProfileImageSource url(String imageUrl) {
        return (key, handler) -> handler.uploadFromUrl(imageUrl, key);
    }

    static ProfileImageSource file(UploadFile file) {
        return (key, handler) -> handler.uploadFile(file, key);
    }

    static ProfileImageSource empty() {
        return (key, handler) -> null;
    }

    interface Handler {

        String uploadFromUrl(String imageUrl, String key);

        String uploadFile(UploadFile file, String key);
    }
}
