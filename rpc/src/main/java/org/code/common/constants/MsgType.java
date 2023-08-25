package org.code.common.constants;

/**
 * The enum Msg type.
 */
public enum MsgType {

    /**
     * Request msg type.
     */
    REQUEST,
    /**
     * Response msg type.
     */
    RESPONSE,
    /**
     * Heartbeat msg type.
     */
    HEARTBEAT;

    /**
     * Find by type msg type.
     * @param type the type
     * @return the msg type
     */
    public static MsgType findByType(int type) {

        return MsgType.values()[type];
    }
}
