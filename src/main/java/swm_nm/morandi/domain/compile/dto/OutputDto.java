package swm_nm.morandi.domain.compile.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutputDto {
    private String result; // 실행 성공 여부
    private String output; // 코드 결과
    private String errorOutput;
    private Double runTime; // 실행 시간
}
