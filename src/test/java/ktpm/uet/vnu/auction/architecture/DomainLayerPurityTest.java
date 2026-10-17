package ktpm.uet.vnu.auction.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Đề bài yêu cầu: tầng nghiệp vụ không import framework web hay thư viện DB.
 * Test này quét mã nguồn tầng domain và fail nếu có import bị cấm.
 */
class DomainLayerPurityTest {

    private static final List<String> FORBIDDEN = List.of(
            "import org.springframework",
            "import jakarta.",
            "import javax.",
            "import org.hibernate",
            "import com.fasterxml",
            "import io.jsonwebtoken",
            "import com.mysql");

    @Test
    void domainLayerDoesNotImportFrameworks() throws IOException {
        Path domainRoot = Path.of("src/main/java/ktpm/uet/vnu/auction/domain");
        List<String> violations;
        try (Stream<Path> files = Files.walk(domainRoot)) {
            violations = files
                    .filter(p -> p.toString().endsWith(".java"))
                    .flatMap(DomainLayerPurityTest::violationsIn)
                    .toList();
        }
        assertTrue(violations.isEmpty(),
                "Tầng domain đang import framework:\n" + String.join("\n", violations));
    }

    private static Stream<String> violationsIn(Path file) {
        try {
            return Files.readAllLines(file).stream()
                    .map(String::trim)
                    .filter(line -> FORBIDDEN.stream().anyMatch(line::startsWith))
                    .map(line -> file.getFileName() + ": " + line);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
