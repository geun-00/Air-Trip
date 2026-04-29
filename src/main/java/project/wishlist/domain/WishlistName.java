package project.wishlist.domain;

public record WishlistName(String value) {

    public WishlistName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("wishlist name must not be blank");
        }

        value = value.trim();

        if (value.length() > 50) {
            throw new IllegalArgumentException("wishlist name must be 50 characters or less");
        }
    }
}
