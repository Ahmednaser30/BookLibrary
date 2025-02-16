package org.projects.book.bookshop.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FeedbackRequest {
    @Positive(message = "200")
    @Min(message = "201",value = 0)
    @Max(message = "202",value = 5)
   Double rate;
   @NotNull(message = "203")
   @NotEmpty(message = "203")
   @NotBlank(message = "203")
   String comment;
   @NotNull(message = "204")
   Long bookId;
}
