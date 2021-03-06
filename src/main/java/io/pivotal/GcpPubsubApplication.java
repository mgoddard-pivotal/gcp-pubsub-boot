package io.pivotal;

import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.pubsub.PubSub;
import com.google.cloud.pubsub.PubSubOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

@SpringBootApplication
public class GcpPubsubApplication {

    private static final Logger logger =
            LoggerFactory.getLogger(GcpPubsubApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(GcpPubsubApplication.class, args);
    }

	/*
     * References:
	 *
	 * https://github.com/GoogleCloudPlatform/google-cloud-java/tree/master/google-cloud-pubsub
	 * https://github.com/GoogleCloudPlatform/google-cloud-java/issues/1430
	 *
	 * Caveat(s):
	 * (1) If running via IntelliJ, you will get a "DEADLINE_EXCEEDED" error (just run on command line)
	 */
    @Bean
    public PubSub pubSubCloud(
            @Value("${vcap.services.${pubsub.instance.name}.credentials.PrivateKeyData}") String privateKeyData,
            @Value("${vcap.services.${pubsub.instance.name}.credentials.ProjectId}") String projectId)
            throws Exception {
        PubSub rv;
        String json = new String(Base64.getDecoder().decode(privateKeyData), "UTF-8");
        //logger.info("JSON: " + json);
        InputStream in = new ByteArrayInputStream(Base64.getDecoder().decode(privateKeyData));
        Credentials cred = ServiceAccountCredentials.fromStream(in);
        rv = PubSubOptions.newBuilder().setProjectId(projectId).setCredentials(cred).build().getService();
        logger.info("Just got a new PubSub instance");
        return rv;
    }

}
