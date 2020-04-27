package com.ejada.gurux;

import gurux.common.IGXMediaListener;
import gurux.common.ReceiveParameters;
import gurux.common.enums.TraceLevel;

public interface IGXMedia {

    /**
     * Start to listen media events.
     *
     * @param listener
     *            Listener class.
     */
    void addListener(IGXMediaListener listener);

    /**
     * Stop to listen media events.
     *
     * @param listener
     *            Listener class.
     */
    void removeListener(IGXMediaListener listener);

    /**
     * Copies the content of the media to target media.
     *
     * @param target
     *            Target media.
     */
    void copy(Object target);

    /**
     * Returns name of the media. Media name is used to identify media
     * connection, so two different media connection can not return same media
     * name.
     *
     * @return Media name.
     */
    String getName();

    /**
     * Trace level of the IGXMedia.
     *
     * @return Trace level.
     * @see IGXMediaListener#onTrace
     */
    TraceLevel getTrace();

    /**
     * Set new trace level.
     *
     * @param value
     *            Trace level.
     */
    void setTrace(TraceLevel value);

    /**
     * Opens the media.
     *
     * @throws Exception
     *             Occurred exception.
     */
    void open() throws Exception;

    /**
     * Checks if the connection is established.
     *
     * @return True, if the connection is established.
     */
    boolean isOpen();

    /**
     * Closes the active connection.
     *
     * @see gurux.common.IGXMedia#open open
     */
    void close();

    /**
     * Sends data asynchronously. No reply from the receiver, whether or not the
     * operation was successful, is expected.
     *
     * @param data
     *            Data to send to the device.
     * @param receiver
     *            Media depend information of the receiver (optional).
     * @throws Exception
     *             Occurred exception.
     * @see gurux.common.IGXMedia#receive receive
     */
    void send(Object data, String receiver) throws Exception;

    /**
     * Returns media type as a string.
     *
     * @return Type of the media.
     */
    String getMediaType();

    /**
     * Get media settings.
     *
     * @return Media settings as a XML string.
     */
    String getSettings();

    /**
     * Set media settings.
     *
     * @param value
     *            Media settings as a XML string.
     */
    void setSettings(String value);

    /**
     * Locking this property makes the connection synchronized and stops sending
     * OnReceived events.
     *
     * @return Locking object.
     */
    Object getSynchronous();

    /**
     * Checks if the connection is in synchronous mode.
     *
     * @return True, if the connection is in synchronous mode.
     */
    boolean getIsSynchronous();

    /**
     * Waits for more reply data After SendSync if whole packet is not received
     * yet.
     *
     * @param <T>
     *            Media type.
     * @param args
     *            Receive data arguments.
     * @return True, if the send operation was successful.
     * @see gurux.common.IGXMedia#send send
     * @see gurux.common.IGXMedia#getIsSynchronous getIsSynchronous
     */
    <T> boolean receive(ReceiveParameters<T> args);

    /**
     * Resets synchronous buffer.
     */
    void resetSynchronousBuffer();

    /**
     * Sent byte count.
     *
     * @return Bytes sent.
     * @see gurux.common.IGXMedia#getBytesReceived() getBytesReceived()
     * @see gurux.common.IGXMedia#resetByteCounters() resetByteCounters()
     */
    long getBytesSent();

    /**
     * Received byte count.
     *
     * @return Bytes received.
     * @see gurux.common.IGXMedia#getBytesSent() getBytesSent()
     * @see gurux.common.IGXMedia#resetByteCounters() resetByteCounters()
     */
    long getBytesReceived();

    /**
     * Resets BytesReceived and BytesSent counters.
     *
     * @see gurux.common.IGXMedia#getBytesSent() getBytesSent()
     * @see gurux.common.IGXMedia#getBytesReceived() getBytesReceived()
     */
    void resetByteCounters();

    /**
     * Validate Media settings for connection open. Returns table of media
     * properties that must be set before media is valid to open.
     */
    void validate();

    /**
     * Get used end of packet.
     *
     * @return Used end of packet.
     */
    Object getEop();

    /**
     * Set End of packet.
     *
     * @param value
     *            Used end of packet.
     */
    void setEop(Object value);

    /**
     * Get visible controls on the properties dialog.
     *
     * @return Visible controls.
     */
    int getConfigurableSettings();

    /**
     * Set visible controls on the properties dialog.
     *
     * @param value
     *            Visible controls.
     */
    void setConfigurableSettings(int value);

}