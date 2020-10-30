package com.capgemini.assignment.payrole.fileio;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Ignore;
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
    @Ignore
    public void pathCheckConfirm() throws IOException {
        //check file exists
        Path homePath = Paths.get(HOME);
        Assert.assertTrue(Files.exists(homePath));

        //delete file and check file not exists
        Path playPath = Paths.get(HOME + "/" + PLAY_WITH_NIO);
        if (Files.exists(playPath)) FileUtils.deleteQuietly(playPath.toFile());
        Assert.assertTrue(Files.notExists(playPath));

        //create file
        IntStream.range(1, 10).mapToObj(counter -> Paths.get(playPath + "/temp" + counter)).forEach(tempFile -> {
            Assert.assertTrue(Files.notExists(tempFile));
            File myFile = new File(tempFile.toString());
            try {
                FileUtils.touch(myFile);
            } catch (IOException e) {
                //duck exception
            }
            Assert.assertTrue(Files.exists(tempFile));
        });

        //list files, directories as well as files with extension
        Files.list(playPath).filter(Files::isRegularFile).forEach(System.out::println);
        Files.newDirectoryStream(playPath).forEach(System.out::println);
        Files.newDirectoryStream(playPath, path -> path.toFile().isFile() && path.toString().startsWith("temp")).forEach(System.out::println);

    }

    @Test
    @Ignore
    public void watchedDirectoryListActivities() throws IOException {
        Path dir = Paths.get(HOME + "/" + PLAY_WITH_NIO);
        Files.list(dir).filter(Files::isRegularFile).forEach(System.out::println);
        new FileWatchService(dir).processEvents();
    }
}
