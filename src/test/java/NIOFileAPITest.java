import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.IntStream;

public class NIOFileAPITest {
    private static final String HOME = System.getProperty("user.home");
    private static final String PLAY_WITH_NIO = "TempPlayground";

    @Test
    public void pathCheckConfirm() throws IOException {
        //check file exists
        Path homePath = Paths.get(HOME);
        Assert.assertTrue(Files.exists(homePath));

        //delete file and check file not exists
        Path playPath = Paths.get(HOME + "/" + PLAY_WITH_NIO);
        if (Files.exists(playPath)) FileUtils.deleteQuietly(playPath.toFile());
        Assert.assertTrue(Files.notExists(playPath));

        //create file
        IntStream.range(1, 10).forEach(counter -> {
            Path tempFile = Paths.get(playPath + "/temp" + counter);
            Assert.assertTrue(Files.notExists(tempFile));

            File myFile = new File(tempFile.toString());
            try {
                FileUtils.touch(myFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Assert.assertTrue(Files.exists(tempFile));
        });

        //list files, directories as well as files with extension
        Files.list(playPath).filter(Files::isRegularFile).forEach(System.out::println);
        Files.newDirectoryStream(playPath).forEach(System.out::println);
        Files.newDirectoryStream(playPath, path -> path.toFile().isFile() && path.toString().startsWith("temp")).forEach(System.out::println);

    }
}
