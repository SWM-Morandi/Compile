package swm_nm.morandi.domain.compile.service;

import org.springframework.stereotype.Service;
import swm_nm.morandi.domain.compile.dto.InputDto;
import swm_nm.morandi.domain.compile.dto.OutputDto;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

@Service
public class CompileService {
    public OutputDto compile(InputDto inputDto) {
        String input = inputDto.getInput();
        String code = inputDto.getCode();
        String language = inputDto.getLanguage();
        return getOutputDto(input, code, language);
    }
    private OutputDto getOutputDto(String input, String code, String language) {
        if (language.equals("Python")) return runPython(code, input);
        else if (language.equals("Cpp")) return runCpp(code, input);
        else if (language.equals("Java")) return runJava(code, input);
        return null;
    }
    private OutputDto runPython(String code, String input)
    {
        OutputDto outputDto = new OutputDto();

        try {
            // File codeFile = new File("temp.py");
            // code = new String(Files.readAllBytes(codeFile.toPath()), StandardCharsets.UTF_8);
            ProcessBuilder pb = new ProcessBuilder("python3", "-c", code);
            pb.redirectErrorStream(true);

            Process p = pb.start();

            // File inputFile = new File("input.txt");
            // input = new String(Files.readAllBytes(inputFile.toPath()), StandardCharsets.UTF_8);

            if (input != null) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
                writer.write(input);
                writer.newLine();
                writer.flush();
                writer.close();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder output = new StringBuilder();
            StringBuilder errorOutput = new StringBuilder();

            String line;
            long startTime = System.currentTimeMillis();
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                if (System.currentTimeMillis() - startTime > 10000) {
                    outputDto.setResult("실패");
                    outputDto.setOutput("시간 초과가 발생했습니다.");
                    outputDto.setErrorOutput(null);
                    outputDto.setRunTime(null);
                    return outputDto;
                }
            }

            int exitCode = p.waitFor();

            if (exitCode == 0) {
                outputDto.setResult("성공");
                outputDto.setOutput(output.toString());
                // 실행 시간 계산 및 설정 (현재 시간 - 시작 시간)
                double elapsedTimeInSeconds = (System.currentTimeMillis() - startTime) / 1000.0;
                outputDto.setRunTime(elapsedTimeInSeconds);
            } else {
                // 실행 실패 (컴파일 에러 또는 런타임 에러 등)
                outputDto.setResult("실패");
                outputDto.setOutput("코드 실행에 실패했습니다.");
                outputDto.setErrorOutput(output.toString());
                outputDto.setRunTime(null);
            }

            return outputDto;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            // 예외가 발생한 경우에도 outputDto를 채우고 반환
            outputDto.setResult("실패");
            outputDto.setOutput("코드 실행 중 오류가 발생했습니다.");
            outputDto.setErrorOutput(e.getMessage());
            outputDto.setRunTime(null);
            return outputDto;
        }
    }

    private OutputDto runCpp(String code, String input) {

        OutputDto outputDto = new OutputDto();
        try {
            String tempFileName = "temp.cpp";
            saveCodeToFile(tempFileName, code);

            String executableFileName = "temp.out";
            String compileCommand = "g++ -std=c++14 " + tempFileName + " -o " + executableFileName;
            Process compileProcess = Runtime.getRuntime().exec(compileCommand);
            compileProcess.waitFor();

            if (compileProcess.exitValue() != 0) {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(compileProcess.getErrorStream()));
                StringBuilder errorOutput = new StringBuilder();
                String errorLine;

                while ((errorLine = errorReader.readLine()) != null) {
                    errorOutput.append(errorLine).append("\n");
                }

                outputDto.setErrorOutput(errorOutput.toString());
                outputDto.setRunTime(null);
                return outputDto;
            }

            String runCommand = "./" + executableFileName;
            Process runProcess = Runtime.getRuntime().exec(runCommand);

            if (input != null) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(runProcess.getOutputStream()));
                writer.write(input);
                writer.newLine();
                writer.flush();
                writer.close();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            long startTime = System.currentTimeMillis();
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                if (System.currentTimeMillis() - startTime > 10000) {
                    outputDto.setResult("실패");
                    outputDto.setOutput("시간 초과가 발생했습니다.");
                    outputDto.setErrorOutput(null);
                    outputDto.setRunTime(null);
                    return outputDto;
                }
            }
          
            if (output.toString().equals("")) {
                outputDto.setResult("실패");
                outputDto.setOutput("코드 실행 중 오류가 발생했습니다.");
            }
            else {
                outputDto.setResult("성공");
                outputDto.setOutput(output.toString());

                // 실행 시간 계산 및 설정 (현재 시간 - 시작 시간)
                double elapsedTimeInSeconds = (System.currentTimeMillis() - startTime) / 1000.0;
                outputDto.setRunTime(elapsedTimeInSeconds);
            }

            return outputDto;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            outputDto.setResult("실패");
            outputDto.setOutput("코드 실행 중 오류가 발생했습니다.");
            outputDto.setErrorOutput(e.getMessage());
            outputDto.setRunTime(null);
            return outputDto;
        }
    }

    private void saveCodeToFile(String fileName, String code) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(code);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private OutputDto runJava(String code, String input) {
        OutputDto outputDto = new OutputDto();
        try {
            File javaFile = new File("Main.java");
            // code = new String(Files.readAllBytes(javaFile.toPath()), StandardCharsets.UTF_8);
            Files.write(javaFile.toPath(), code.getBytes(StandardCharsets.UTF_8));

            // 컴파일된 클래스 파일 생성
            ProcessBuilder compilePb = new ProcessBuilder("javac", javaFile.getName());
            compilePb.redirectErrorStream(true);
            Process compileProcess = compilePb.start();
            compileProcess.waitFor();

            // 컴파일 에러가 있는지 확인
            BufferedReader compileReader = new BufferedReader(new InputStreamReader(compileProcess.getInputStream()));
            StringBuilder compileOutput = new StringBuilder();

            String compileLine;

            while ((compileLine = compileReader.readLine()) != null) {
                compileOutput.append(compileLine).append("\n");
            }

            if (compileOutput.length() > 0) {
                outputDto.setResult("실패");
                outputDto.setOutput("코드 실행 중 오류가 발생했습니다.");
                outputDto.setErrorOutput(compileOutput.toString());
                outputDto.setRunTime(null);
                return outputDto;
            }

            // 실행
            ProcessBuilder pb = new ProcessBuilder("java", "Main");
            pb.redirectErrorStream(true);

            Process p = pb.start();

//            File inputFile = new File("input.txt");
//            Files.write(inputFile.toPath(), input.getBytes(StandardCharsets.UTF_8));
//
//            input = new String(Files.readAllBytes(inputFile.toPath()), StandardCharsets.UTF_8);

            if (input != null) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
                writer.write(input);
                writer.newLine();
                writer.flush();
                writer.close();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            long startTime = System.currentTimeMillis();
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                if (System.currentTimeMillis() - startTime > 10000) {
                    outputDto.setResult("실패");
                    outputDto.setOutput("시간 초과가 발생했습니다.");
                    outputDto.setErrorOutput(null);
                    outputDto.setRunTime(null);
                    return outputDto;
                }
            }

            int exitValue = p.waitFor();

            if (exitValue == 0) {
                // 프로세스가 성공적으로 종료되었을 경우
                outputDto.setResult("성공");
                outputDto.setOutput(output.toString());
                double elapsedTimeInSeconds = (System.currentTimeMillis() - startTime) / 1000.0;
                outputDto.setRunTime(elapsedTimeInSeconds);
            } else {
                // 프로세스가 오류로 종료되었을 경우
                outputDto.setResult("실패");
                outputDto.setOutput("코드 실행 중 오류가 발생했습니다.");
                outputDto.setErrorOutput(output.toString());
                outputDto.setRunTime(null);
            }

            return outputDto;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            outputDto.setResult("실패");
            outputDto.setOutput("코드 실행 중 오류가 발생했습니다.");
            outputDto.setErrorOutput(e.getMessage());
            outputDto.setRunTime(null);
            return outputDto;
        }
    }

}