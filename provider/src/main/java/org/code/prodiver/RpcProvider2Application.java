package org.code.prodiver;


import org.code.annitation.EnableProviderRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The type Rpc provider 2 application.
 */
@SpringBootApplication
@EnableProviderRpc
public class RpcProvider2Application {

    /**
     * The entry point of application.
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(RpcProvider2Application.class, args);
    }

}
