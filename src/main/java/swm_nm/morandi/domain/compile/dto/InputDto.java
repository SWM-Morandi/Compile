package swm_nm.morandi.domain.compile.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InputDto {
    private String language;
    private String code;
    private List<String> input;
    private List<String> output;
}
