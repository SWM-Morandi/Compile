package swm_nm.morandi.domain.compile.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swm_nm.morandi.domain.compile.dto.InputDto;
import swm_nm.morandi.domain.compile.dto.OutputDto;
import swm_nm.morandi.domain.compile.dto.TestCaseInputDto;
import swm_nm.morandi.domain.compile.service.CompileService;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class CompileController {

    private final CompileService compileService;
    @PostMapping
    public ResponseEntity<OutputDto> getCompileResult(@RequestBody InputDto inputDto) {
        OutputDto outputDto = compileService.compile(inputDto);
        return new ResponseEntity<>(outputDto, OK);
    }
    @PostMapping("/tc")
    public ResponseEntity<List<OutputDto>> getCompileResult(@RequestBody TestCaseInputDto testCaseInputDto) {
        List<OutputDto> outputDto = compileService.testCaseCompile(testCaseInputDto);
        return new ResponseEntity<>(outputDto, OK);
    }
}
