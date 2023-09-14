package swm_nm.morandi.domain.compile.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InputDto {
    private String language;
    private String code;
    private String input;
}
