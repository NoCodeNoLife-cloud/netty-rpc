package org.code;

import org.code.annitation.EnableConsumerRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The type Rpc consumer demo application.
 */
@SpringBootApplication
@EnableConsumerRpc
public class RpcConsumerDemoApplication {
	/**
	 * The entry point of application.
	 * @param args the input arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(RpcConsumerDemoApplication.class, args);
	}
}
