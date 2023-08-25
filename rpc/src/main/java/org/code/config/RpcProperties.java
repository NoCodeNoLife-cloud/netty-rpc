package org.code.config;

import lombok.Getter;
import lombok.Setter;
import org.code.annitation.PropertiesField;
import org.code.annitation.PropertiesPrefix;
import org.code.common.constants.RegistryRules;
import org.code.common.constants.SerializationRules;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Rpc properties.
 */
@PropertiesPrefix("rpc")
@Getter
@Setter
public class RpcProperties {


    /**
     * The Rpc properties.
     */
    static RpcProperties rpcProperties;
    /**
     * netty service port
     */
    @PropertiesField
    private Integer port;
    /**
     * Registry address
     */
    @PropertiesField
    private String registerAddr;
    /**
     * Registry type
     */
    @PropertiesField
    private String registerType = RegistryRules.ZOOKEEPER;
    /**
     * Registry password
     */
    @PropertiesField
    private String registerPsw;
    /**
     * serialize
     */
    @PropertiesField
    private String serialization = SerializationRules.JSON;
    /**
     * Additional configuration data on the server
     */
    @PropertiesField("service")
    private Map<String, Object> serviceAttachments = new HashMap<>();
    /**
     * Additional configuration data of the client
     */
    @PropertiesField("client")
    private Map<String, Object> clientAttachments = new HashMap<>();

    private RpcProperties() {
    }

    /**
     * Gets instance.
     * @return the instance
     */
    public static RpcProperties getInstance() {
        if (rpcProperties == null) {
            rpcProperties = new RpcProperties();
        }
        return rpcProperties;
    }

    /**
     * Make a utility class that can parse the properties of any object
     * @param environment the environment
     */
    public static void init(Environment environment) {

    }

    /**
     * Sets register type.
     * @param registerType the register type
     */
    public void setRegisterType(String registerType) {
        if (registerType == null || registerType.equals("")) {
            registerType = RegistryRules.ZOOKEEPER;
        }
        this.registerType = registerType;
    }

    /**
     * Sets serialization.
     * @param serialization the serialization
     */
    public void setSerialization(String serialization) {
        if (serialization == null || serialization.equals("")) {
            serialization = SerializationRules.JSON;
        }
        this.serialization = serialization;
    }
}
