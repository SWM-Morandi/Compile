package swm_nm.morandi.domain.compile.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutputDto {
    private String result; // 성공, 실패, 시간초과
    private String output; // 실행 결과물
    private Double executeTime;
}
