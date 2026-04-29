package project.wishlist.domain;

public record WishlistMemo(String value) {

    public WishlistMemo {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("wishlist memo must not be blank");
        }

        if (value.length() > 250) {
            throw new IllegalArgumentException("wishlist memo must be 250 characters or less");
        }
    }
}
