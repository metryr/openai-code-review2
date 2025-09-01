package plus.gaga.middleware.sdk;

import com.alibaba.fastjson2.JSON;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import plus.gaga.middleware.sdk.domain.model.ChatCompletionRequestDTO;
import plus.gaga.middleware.sdk.domain.model.ChatCompletionSyncResponseDTO;
import plus.gaga.middleware.sdk.domain.model.Model;
import plus.gaga.middleware.sdk.types.utils.BearerTokenUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class OpenAiCodeReview {

    public static void main(String[] args) throws Exception {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        System.out.println("测试执行");

        String token = System.getenv("GITHUB_TOKEN");
        if (null == token || token.isEmpty()) {
            throw new RuntimeException("token is null");
        }

        // 1. 代码检出
        ProcessBuilder processBuilder = new ProcessBuilder("git", "diff", "HEAD~1", "HEAD");
        processBuilder.directory(new File("."));

        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        StringBuilder diffCode = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            diffCode.append(line);
        }

        int exitCode = process.waitFor();
        System.out.println("Exited with code:" + exitCode);

        System.out.println("diffCode:" + diffCode);

        String log = codeReview(diffCode.toString());
        System.out.println(log);

        // 3. 写入评审日志
        String logUrl = writeLog(token, log);
        System.out.println("writeLog：" + logUrl);
    }

    private static String codeReview(String diffCode) throws IOException {
        String apiKeySecret = "659d93cbd99d4f3d910b32f81fb68a09.S03p9y0ZoPeDDNKv";
        String token = BearerTokenUtils.getToken(apiKeySecret);

        URL url = new URL("https://open.bigmodel.cn/api/paas/v4/chat/completions");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + token);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        connection.setDoOutput(true);


//        String jsonInpuString = "{"
//                + "\"model\":\"glm-4-flash\","
//                + "\"messages\": ["
//                + "    {"
//                + "        \"role\": \"user\","
//                + "        \"content\": \"你是一个高级编程架构师，精通各类场景方案、架构设计和编程语言请，请您根据git diff记录，对代码做出评审。代码为: diff --git a/openai-code-review-test/src/test/java/plus/gaga/middleware/test/ApiTest.java b/openai-code-review-test/src/test/java/plus/gaga/middleware/test/ApiTest.javaindex 7712679..49ee824 100644--- a/openai-code-review-test/src/test/java/plus/gaga/middleware/test/ApiTest.java+++ b/openai-code-review-test/src/test/java/plus/gaga/middleware/test/ApiTest.java@@ -13,7 +13,7 @@ public class ApiTest {      @Test     public void test() {-        System.out.println(Integer.parseInt(\\\"aaaa\\\"));+        System.out.println(Integer.parseInt(\\\"aaaa1111\\\")); " +  "\""
//                + "    }"
//                + "]"
//                + "}";

        ChatCompletionRequestDTO chatCompletionRequestDTO = new ChatCompletionRequestDTO();
        chatCompletionRequestDTO.setModel(Model.GLM_4_FLASH.getCode());
        chatCompletionRequestDTO.setMessages(new ArrayList<ChatCompletionRequestDTO.Prompt>(){{
            add(new ChatCompletionRequestDTO.Prompt("user","你是一个高级编程架构师，精通各类场景方案、架构设计和编程语言请，请您根据git diff记录，对代码做出评审。代码为:"));
            add(new ChatCompletionRequestDTO.Prompt("user",diffCode));
        }});
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = JSON.toJSONString(chatCompletionRequestDTO).getBytes(StandardCharsets.UTF_8);
            os.write(input);
        }

        int responseCode = connection.getResponseCode();
        System.out.println(responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;

        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        connection.disconnect();

        ChatCompletionSyncResponseDTO response = JSON.parseObject(content.toString(), ChatCompletionSyncResponseDTO.class);


        return  response.getChoices().get(0).getMessage().getContent();


    }
    private static String writeLog(String token, String log) throws Exception {
        Git git = Git.cloneRepository()
                .setURI("https://github.com/metryr/openai-code-review2-log.git.git")
                .setDirectory(new File("repo"))
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, ""))
                .call();

        String dateFolderName = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        File dateFolder = new File("repo/" + dateFolderName);
        if (!dateFolder.exists()) {
            dateFolder.mkdirs();
        }

        String fileName = generateRandomString(12) + ".md";
        File newFile = new File(dateFolder, fileName);
        try (FileWriter writer = new FileWriter(newFile)) {
            writer.write(log);
        }

        git.add().addFilepattern(dateFolderName + "/" + fileName).call();
        git.commit().setMessage("Add new file via GitHub Actions").call();
        git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, "")).call();

        System.out.println("Changes have been pushed to the repository.");

        return "https://github.com/metryr/openai-code-review2-log.git/blob/master/" + dateFolderName + "/" + fileName;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

}
