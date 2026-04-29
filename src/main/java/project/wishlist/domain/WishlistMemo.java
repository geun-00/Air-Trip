package project.wishlist.domain;

public record WishlistMemo(String value) {

    public static WishlistMemo from(String value) {
        return value == null ? null : new WishlistMemo(value);
    }

    public WishlistMemo {
        if (value.length() > 250) {
            throw new IllegalArgumentException("wishlist memo must be 250 characters or less");
        }
    }
}
