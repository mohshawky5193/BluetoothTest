package com.ejada.gurux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gurux.common.GXCmdParameter;
import gurux.common.GXCommon;
import gurux.common.enums.TraceLevel;
import gurux.dlms.GXDLMSClient;
import gurux.dlms.GXSimpleEntry;
import gurux.dlms.enums.Authentication;
import gurux.dlms.enums.InterfaceType;
import gurux.dlms.enums.Security;
import gurux.dlms.enums.Standard;
import gurux.dlms.secure.GXDLMSSecureClient;
import gurux.net.GXNet;



public class Settings {

    public IGXMedia media = null;
    public TraceLevel trace = TraceLevel.INFO;
    public boolean iec = false;
    public GXDLMSSecureClient client = new GXDLMSSecureClient(true);
    String invocationCounter;
    // Objects to read.
    public List<Map.Entry<String, Integer>> readObjects =
            new ArrayList<Map.Entry<String, Integer>>();

    // Cache file.
    String outputFile;

    /**
     * Show help.
     */
    static void showHelp() {
        System.out.println(
                "GuruxDlmsSample reads data from the DLMS/COSEM device.");
        System.out.println(
                "GuruxDlmsSample -h [Meter IP Address] -p [Meter Port No] -c 16 -s 1 -r sn");
        System.out.println(" -h \t host name or IP address.");
        System.out.println(" -p \t port number or name (Example: 1000).");
        System.out.println(" -S \t serial port.");
        System.out.println(" -i IEC is a start protocol.");
        System.out.println(
                " -a \t Authentication (None, Low, High, HighMd5, HighSha1, HighGMac, HighSha256).");
        System.out.println(" -P \t Password for authentication.");
        System.out.println(" -c \t Client address. (Default: 16)");
        System.out.println(" -s \t Server address. (Default: 1)");
        System.out.println(" -n \t Server address as serial number.");
        System.out.println(
                " -r [sn, ln]\t Short name or Logical Name (default) referencing is used.");
        System.out.println(" -w WRAPPER profile is used. HDLC is default.");
        System.out
                .println(" -t [Error, Warning, Info, Verbose] Trace messages.");
        System.out.println(
                " -g \"0.0.1.0.0.255:1; 0.0.1.0.0.255:2\" Get selected object(s) with given attribute index.");
        System.out.println(
                " -C \t Security Level. (None, Authentication, Encrypted, AuthenticationEncryption)");
        System.out.println(
                " -v \t Invocation counter data object Logical Name. Ex. 0.0.43.1.1.255");
        System.out.println(" -I \t Auto increase invoke ID");
        System.out.println(
                " -o \t Cache association view to make reading faster. Ex. -o C:\\device.xml");
        System.out.println(
                " -T \t System title that is used with chiphering. Ex -D 4775727578313233");
        System.out.println(
                " -A \t Authentication key that is used with chiphering. Ex -D D0D1D2D3D4D5D6D7D8D9DADBDCDDDEDF");
        System.out.println(
                " -B \t Block cipher key that is used with chiphering. Ex -D 000102030405060708090A0B0C0D0E0F");
        System.out.println(
                " -D \t Dedicated key that is used with chiphering. Ex -D 00112233445566778899AABBCCDDEEFF");
        System.out.println(
                " -d \t Used DLMS standard. Ex -d India (DLMS, India, Italy, SaudiArabia, IDIS)");
        System.out.println("Example:");
        System.out.println("Read LG device using TCP/IP connection.");
        System.out.println(
                "GuruxDlmsSample -r sn -c 16 -s 1 -h [Meter IP Address] -p [Meter Port No]");
        System.out.println("Read LG device using serial port connection.");
        System.out.println("GuruxDlmsSample -r sn -c 16 -s 1 -S COM1 -i");
        System.out.println("Read Indian device using serial port connection.");
        System.out.println(
                "GuruxDlmsSample -S COM1 -c 16 -s 1 -a Low -P [password]");

    }

    static int getParameters(String[] args, Settings settings) {
        ArrayList<GXCmdParameter> parameters = (ArrayList<GXCmdParameter>) GXCommon.getParameters(args,
                "h:p:c:s:r:iIt:a:p:wP:g:S:n:C:v:o:T:A:B:D:d:");
        GXNet net = null;
        for (GXCmdParameter it : parameters) {
            switch (it.getTag()) {
                case 'w':
                    settings.client.setInterfaceType(InterfaceType.WRAPPER);
                    break;
                case 'r':
                    if ("sn".compareTo(it.getValue()) == 0) {
                        settings.client.setUseLogicalNameReferencing(false);
                    } else if ("ln".compareTo(it.getValue()) == 0) {
                        settings.client.setUseLogicalNameReferencing(true);
                    } else {
                        throw new IllegalArgumentException(
                                "Invalid reference option.");
                    }
                    break;
                /*case 'h':
                    // Host address.
                    if (settings.media == null) {
                        settings.media = new GXNet();
                    }
                    net = (GXNet) settings.media;
                    net.setHostName(it.getValue());
                    break;*/
                case 't':
                    // Trace.
                    if ("Error".compareTo(it.getValue()) == 0)
                        settings.trace = TraceLevel.ERROR;
                    else if ("Warning".compareTo(it.getValue()) == 0)
                        settings.trace = TraceLevel.WARNING;
                    else if ("Info".compareTo(it.getValue()) == 0)
                        settings.trace = TraceLevel.INFO;
                    else if ("Verbose".compareTo(it.getValue()) == 0)
                        settings.trace = TraceLevel.VERBOSE;
                    else if ("Off".compareTo(it.getValue()) == 0)
                        settings.trace = TraceLevel.OFF;
                    else
                        throw new IllegalArgumentException(
                                "Invalid Authentication option '" + it.getValue()
                                        + "'. (Error, Warning, Info, Verbose, Off).");
                    break;
                /*case 'p':
                    // Port.
                    if (settings.media == null) {
                        settings.media = new GXNet();
                    }
                    net = (GXNet) settings.media;
                    net.setPort(Integer.parseInt(it.getValue()));
                    break;*/
                case 'P':// Password
                    settings.client.setPassword(it.getValue().getBytes());
                    break;
                case 'i':
                    // IEC.
                    settings.iec = true;
                    break;
                case 'I':
                    // AutoIncreaseInvokeID.
                    settings.client.setAutoIncreaseInvokeID(true);
                    break;
                case 'C':
                    if ("None".compareTo(it.getValue()) == 0) {
                        settings.client.getCiphering().setSecurity(Security.NONE);
                    } else if ("Authentication".compareTo(it.getValue()) == 0) {
                        settings.client.getCiphering()
                                .setSecurity(Security.AUTHENTICATION);
                    } else if ("Encryption".compareTo(it.getValue()) == 0) {
                        settings.client.getCiphering()
                                .setSecurity(Security.ENCRYPTION);
                    } else if ("AuthenticationEncryption"
                            .compareTo(it.getValue()) == 0) {
                        settings.client.getCiphering()
                                .setSecurity(Security.AUTHENTICATION_ENCRYPTION);
                    } else {
                        throw new IllegalArgumentException(
                                "Invalid Ciphering option '" + it.getValue()
                                        + "'. (None, Authentication, Encryption, AuthenticationEncryption)");
                    }
                    break;
                case 'T':
                    settings.client.getCiphering()
                            .setSystemTitle(GXCommon.hexToBytes(it.getValue()));
                    break;
                case 'A':
                    settings.client.getCiphering().setAuthenticationKey(
                            GXCommon.hexToBytes(it.getValue()));
                    break;
                case 'B':
                    settings.client.getCiphering()
                            .setBlockCipherKey(GXCommon.hexToBytes(it.getValue()));
                    break;
                case 'D':
                    settings.client.getCiphering()
                            .setDedicatedKey(GXCommon.hexToBytes(it.getValue()));
                    break;
                case 'o':
                    settings.outputFile = it.getValue();
                    break;
                case 'd':
                    try {
                        settings.client
                                .setStandard(Standard.valueOfString(it.getValue()));
                    } catch (Exception e) {
                        throw new IllegalArgumentException(
                                "Invalid DLMS standard option: '" + it.getValue()
                                        + "'. (DLMS, India, Italy, SaudiArabia, IDIS)");
                    }
                    break;
                case 'v':
                    settings.invocationCounter = it.getValue();
                    // TODO:
                    // GXDLMSObject.ValidateLogicalName(settings.invocationCounter);
                    break;
                case 'g':
                    // Get (read) selected objects.
                    for (String o : it.getValue().split("[;,]")) {
                        String[] tmp = o.split("[:]");
                        if (tmp.length != 2) {
                            throw new IllegalArgumentException(
                                    "Invalid Logical name or attribute index.");
                        }
                        settings.readObjects.add(new GXSimpleEntry<String, Integer>(
                                tmp[0].trim(), Integer.parseInt(tmp[1].trim())));
                    }
                    break;
                case 'S':// Serial Port
                    settings.media = new GXSerial2();
                    GXSerial2 serial = (GXSerial2) settings.media;
                    serial.setPortName(it.getValue());
                    break;
                case 'a':
                    try {
                        settings.client.setAuthentication(
                                Authentication.valueOfString(it.getValue()));
                    } catch (Exception e) {
                        throw new IllegalArgumentException(
                                "Invalid Authentication option: '" + it.getValue()
                                        + "'. (None, Low, High, HighMd5, HighSha1, HighGmac, HighSha256)");
                    }
                    break;
                case 'c':
                    settings.client
                            .setClientAddress(Integer.parseInt(it.getValue()));
                    break;
                case 's':
                    settings.client
                            .setServerAddress(Integer.parseInt(it.getValue()));
                    break;
                case 'n':
                    settings.client.setServerAddress(GXDLMSClient
                            .getServerAddress(Integer.parseInt(it.getValue())));
                    break;
                case '?':
                    switch (it.getTag()) {
                        case 'c':
                            throw new IllegalArgumentException(
                                    "Missing mandatory client option.");
                        case 's':
                            throw new IllegalArgumentException(
                                    "Missing mandatory server option.");
                        case 'h':
                            throw new IllegalArgumentException(
                                    "Missing mandatory host name option.");
                        case 'p':
                            throw new IllegalArgumentException(
                                    "Missing mandatory port option.");
                        case 'r':
                            throw new IllegalArgumentException(
                                    "Missing mandatory reference option.");
                        case 'a':
                            throw new IllegalArgumentException(
                                    "Missing mandatory authentication option.");
                        case 'S':
                            throw new IllegalArgumentException(
                                    "Missing mandatory Serial port option.\n");
                        case 't':
                            throw new IllegalArgumentException(
                                    "Missing mandatory trace option.\n");
                        case 'g':
                            throw new IllegalArgumentException(
                                    "Missing mandatory OBIS code option.");
                        case 'C':
                            throw new IllegalArgumentException(
                                    "Missing mandatory Ciphering option.");
                        case 'v':
                            throw new IllegalArgumentException(
                                    "Missing mandatory invocation counter logical name option.");
                        case 'T':
                            throw new IllegalArgumentException(
                                    "Missing mandatory system title option.");
                        case 'A':
                            throw new IllegalArgumentException(
                                    "Missing mandatory authentication key option.");
                        case 'B':
                            throw new IllegalArgumentException(
                                    "Missing mandatory block cipher key option.");
                        case 'D':
                            throw new IllegalArgumentException(
                                    "Missing mandatory dedicated key option.");
                        case 'd':
                            throw new IllegalArgumentException(
                                    "Missing mandatory DLMS standard option.");
                        default:
                            showHelp();
                            return 1;
                    }
                default:
                    showHelp();
                    return 1;
            }
        }
        if (settings.media == null) {
            showHelp();
            return 1;
        }
        return 0;
    }
}
