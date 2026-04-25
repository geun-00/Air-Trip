package project.member.application.in.command.model;

import project.common.application.model.UploadFile;

@FunctionalInterface
public interface ProfileImageChange {

    void handleWith(Long memberId, String oldImageUrl, Handler handler);

    static ProfileImageChange noOp() {
        return (memberId, oldImageUrl, handler) -> {};
    }

    static ProfileImageChange remove() {
        return (memberId, oldImageUrl, handler) -> handler.remove(memberId, oldImageUrl);
    }

    static ProfileImageChange replace(UploadFile file) {
        return (memberId, oldImageUrl, handler) -> handler.replace(memberId, oldImageUrl, file);
    }

    interface Handler {

        void remove(Long memberId, String oldImageUrl);

        void replace(Long memberId, String oldImageUrl, UploadFile file);
    }
}
