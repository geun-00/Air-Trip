package project.common.application.model;

public record UploadFile(
        String originalFilename,
        String contentType,
        long size,
        byte[] bytes
) {
}
