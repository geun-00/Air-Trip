package project.wishlist.adapter.in.web.request;

import jakarta.validation.constraints.Size;

public record MemoUpdateRequest(@Size(max = 250) String memo) {
}
