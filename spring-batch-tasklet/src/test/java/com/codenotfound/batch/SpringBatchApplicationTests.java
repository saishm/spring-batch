package com.codenotfound.batch;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.FileSystemUtils;
import com.codenotfound.batch.job.ConvertNamesJobConfig;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBatchApplicationTests.BatchTestConfig.class})
public class SpringBatchApplicationTests {

  private static Path csvFilesPath, testInputPath;

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @BeforeClass
  public static void copyFiles() throws URISyntaxException, IOException {
    csvFilesPath = Paths.get(new ClassPathResource("csv").getURI());
    testInputPath = Paths.get("target/test-input");
    try {
      Files.createDirectory(testInputPath);
    } catch (Exception e) {
      // if directory exists do nothing
    }

    FileSystemUtils.copyRecursively(csvFilesPath, testInputPath);
  }

  @Test
  public void testHelloWorldJob() throws Exception {
    JobExecution jobExecution = jobLauncherTestUtils.launchJob();
    assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");

    File testInput = testInputPath.toFile();
    assertThat(testInput.list().length).isEqualTo(0);
  }

  @Configuration
  @Import(ConvertNamesJobConfig.class)
  static class BatchTestConfig {

    @Autowired
    private Job convertNamesJob;

    @Bean
    JobLauncherTestUtils jobLauncherTestUtils() throws NoSuchJobException {
      JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();
      jobLauncherTestUtils.setJob(convertNamesJob);

      return jobLauncherTestUtils;
    }
  }
}