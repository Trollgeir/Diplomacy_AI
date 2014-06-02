/*****************************************************************************
 * $Id: Token.java,v 1.1 2004/09/09 06:32:18 heb Exp $ 
 *
 * Copyright © 2002, 2003, 2004 by Henrik Bylund
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose and without fee is hereby granted,
 * provided that the above copyright notice appear in all copies.
 *****************************************************************************/
package communication.server;

import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.LinkedList;

import java.io.UnsupportedEncodingException;

/**
 * The <code>Token</code> class converts tokens between their
 * mnemonic and bit representations.
 * Mnemonics in this case are represented by <code>String</code>
 * objects, while the bit patterns are represented by <code>byte</code>
 * arrays of size 2.
 *
 * @version 0.1
 * @author <a href="mailto:heb@ludd.luth.se">Henrik Bylund</a>
 */
public final class Token {
    public final static byte MISC              = 0x40;
    public final static byte POWERS            = 0x41;
    public final static byte UNIT_TYPES        = 0x42;
    public final static byte ORDERS            = 0x43;
    public final static byte ORDER_NOTES       = 0x44;
    public final static byte RESULTS           = 0x45;
    public final static byte COASTS            = 0x46;
    public final static byte PHASES            = 0x47;
    public final static byte COMMANDS          = 0x48;
    public final static byte PARAMETERS        = 0x49;
    public final static byte PRESS             = 0x4A;
    public final static byte TEXT              = 0x4B;
    public final static byte INLAND_NON_SC     = 0x50;
    public final static byte INLAND_SC         = 0x51;
    public final static byte SEA_NON_SC        = 0x52;
    public final static byte SEA_SC            = 0x53;
    public final static byte COASTAL_NON_SC    = 0x54;
    public final static byte COASTAL_SC        = 0x55;
    public final static byte BI_COASTAL_NON_SC = 0x56;
    public final static byte BI_COASTAL_SC     = 0x57;

    /** A <code>String</code>-><code>byte[2]</code> mapping */
    private static final Map tokenMap;
    /** A <code>ByteArrayEntry</code>-><code>String</code> mapping */
    private static final Map bitsMap;

    // Set up the two maps
    static {
        tokenMap = createTokenMap();
        bitsMap = new HashMap();
        Iterator iter = tokenMap.entrySet().iterator();

        while(iter.hasNext()){
            Map.Entry entry = (Map.Entry)iter.next();
            byte[] bytes = (byte[])entry.getValue();
            bitsMap.put(new ByteArrayEntry(bytes[0], bytes[1]),
                        entry.getKey());
        }
        if(tokenMap.size() != bitsMap.size()){
            System.err.println(Token.class.getName() +
                               ": tokenMap does not contain an 1-1 mapping!");
        } else {
//      System.err.println("Stored " + tokenMap.size() + " tokens.");
        }
       
 
    }

    /**
     * As this class should never be instantiated, the default
     * constructor is made private.
     */
    private Token(){
 
    }

    /**
     * Adds a token.
     *
     * @param bits The bit representation
     * @param mnemonic The mnemonic representation
     */
    public static void add(byte[] bits, String mnemonic){
        ByteArrayEntry entry = new ByteArrayEntry(bits[0], bits[1]);
        bitsMap.put(entry, mnemonic);
        tokenMap.put(mnemonic, bits);
    }
    

    /**
     * Convert a token from its bit pattern to its mnemonic representation.
     * Text tokens are not to be converted this way, they need to be
     * processed by the caller.
     *
     * @param bits the bit pattern as a byte[2].
     * @return the mnemonic representation of the bit pattern.
     * @throws UnknownTokenException if the bits supplied does not correspond
     *                               to a known token. This will also be
     *                               thrown if conversion of a text token
     *                               is attempted.
     */
    public static String convert(byte[] bits) throws UnknownTokenException {
        // If the two topmost bits are zero, we have an integer.
        if((bits[0] & 0xC0) == 0){
            int i = bits[0];
            if(bits[0] < 0) i += 0x100;
            i <<= 8;
            i += bits[1];
            if(bits[1] < 0) i += 0x100;
            return Integer.toString(i);
        }
     
        String token = (String)bitsMap.get(new ByteArrayEntry(bits[0],
                                                              bits[1]));
        if(token == null){
            throw new UnknownTokenException(bits);
        } else {
            return token;
        }
    }
    
    /**
     * Convert a token from mnemonic representation to its
     * bit pattern.
     * Text tokens are not to be converted this way. The method
     * <code>convertText</code> can be used to convert a textual string
     * to a byte array.
     *
     * @param token the mnemonic representation.
     * @return the bit pattern in a byte[2].
     * @throws UnknownTokenException if the token supplied does not correspond
     *                               to a known token.
     * @see #convertText
     */
    public static byte[] convert(String token) throws UnknownTokenException {
        byte[] bytes = (byte[])tokenMap.get(token);
        if(bytes == null){
            throw new UnknownTokenException(token);
        } else {
            return bytes;
        }
    }

    /**
     * Converts a textual string, surrounded by <code>'</code>,
     * to its byte representation.
     *
     * @param text The text to convert.
     * @return the bit representation for the text.
     */
    public static byte[] convertText(String text){
        try {
            byte[] bytes = text.getBytes("US-ASCII");
            if(bytes.length < 2 ||
               bytes[0] != '\'' ||
               bytes[bytes.length - 1] != '\''){
                throw new IllegalArgumentException("String not surrounded " +
                                                   "by single quotes");
            }
     
            byte[] res = new byte[(bytes.length - 2) * 2];
            for(int i = 0; i < res.length; i += 2){
                res[i]   = 0x4B;
                res[i+1] = bytes[i/2 + 1];
            }
            return res;
        } catch (UnsupportedEncodingException uee){
            uee.printStackTrace();
            System.exit(1);
            return null;
        }
    }
    

    /**
     * Create the mapping between tokens and bit patterns.
     *
     * @return a Map with <code>String</code>-><code>byte[2]</code>
     *         mappings.
     */
    private static Map createTokenMap(){
        HashMap map = new HashMap();
        // Misc (0x40)
        map.put("(", new byte[]{0x40,0x00});
        map.put(")", new byte[]{0x40,0x01});
        // Powers (0x41)
        map.put("AUS", new byte[]{0x41,0x00});
        map.put("ENG", new byte[]{0x41,0x01});
        map.put("FRA", new byte[]{0x41,0x02});
        map.put("GER", new byte[]{0x41,0x03});
        map.put("ITA", new byte[]{0x41,0x04});
        map.put("RUS", new byte[]{0x41,0x05});
        map.put("TUR", new byte[]{0x41,0x06});
        // Unit types (0x42)
        map.put("AMY", new byte[]{0x42,0x00});
        map.put("FLT", new byte[]{0x42,0x01});
        // Orders (0x43)
        map.put("CTO", new byte[]{0x43,0x20});
        map.put("CVY", new byte[]{0x43,0x21});
        map.put("HLD", new byte[]{0x43,0x22});
        map.put("MTO", new byte[]{0x43,0x23});
        map.put("SUP", new byte[]{0x43,0x24});
        map.put("VIA", new byte[]{0x43,0x25});
        map.put("DSB", new byte[]{0x43,0x40});
        map.put("RTO", new byte[]{0x43,0x41});
        map.put("BLD", new byte[]{0x43,(byte)0x80});
        map.put("REM", new byte[]{0x43,(byte)0x81});
        map.put("WVE", new byte[]{0x43,(byte)0x82});
        // Order notes (0x44)
        map.put("MBV", new byte[]{0x44,0x00});
        map.put("BPR", new byte[]{0x44,0x01});
        map.put("CST", new byte[]{0x44,0x02});
        map.put("ESC", new byte[]{0x44,0x03});
        map.put("FAR", new byte[]{0x44,0x04});
        map.put("HSC", new byte[]{0x44,0x05});
        map.put("NAS", new byte[]{0x44,0x06});
        map.put("NMB", new byte[]{0x44,0x07});
        map.put("NMR", new byte[]{0x44,0x08});
        map.put("NRN", new byte[]{0x44,0x09});
        map.put("NRS", new byte[]{0x44,0x0A});
        map.put("NSA", new byte[]{0x44,0x0B});
        map.put("NSC", new byte[]{0x44,0x0C});
        map.put("NSF", new byte[]{0x44,0x0D});
        map.put("NSP", new byte[]{0x44,0x0E});
        map.put("NST", new byte[]{0x44,0x0F});
        map.put("NSU", new byte[]{0x44,0x10});
        map.put("NVR", new byte[]{0x44,0x11});
        map.put("NYU", new byte[]{0x44,0x12});
        map.put("YSC", new byte[]{0x44,0x13});
        // Results (0x45)
        map.put("SUC", new byte[]{0x45,0x00});
        map.put("BNC", new byte[]{0x45,0x01});
        map.put("CUT", new byte[]{0x45,0x02});
        map.put("DSR", new byte[]{0x45,0x03});
        map.put("FLD", new byte[]{0x45,0x04});
        map.put("NSO", new byte[]{0x45,0x05});
        map.put("RET", new byte[]{0x45,0x06});
        // Coasts (0x46)
        map.put("NCS", new byte[]{0x46,0x00});
        map.put("NEC", new byte[]{0x46,0x02});
        map.put("ECS", new byte[]{0x46,0x04});
        map.put("SEC", new byte[]{0x46,0x06});
        map.put("SCS", new byte[]{0x46,0x08});
        map.put("SWC", new byte[]{0x46,0x0A});
        map.put("WCS", new byte[]{0x46,0x0C});
        map.put("NWC", new byte[]{0x46,0x0E});
        // Phases (0x47)
        map.put("SPR", new byte[]{0x47,0x00});
        map.put("SUM", new byte[]{0x47,0x01});
        map.put("FAL", new byte[]{0x47,0x02});
        map.put("AUT", new byte[]{0x47,0x03});
        map.put("WIN", new byte[]{0x47,0x04});
        // Commands (0x48)
        map.put("CCD", new byte[]{0x48,0x00});
        map.put("DRW", new byte[]{0x48,0x01});
        map.put("FRM", new byte[]{0x48,0x02});
        map.put("GOF", new byte[]{0x48,0x03});
        map.put("HLO", new byte[]{0x48,0x04});
        map.put("HST", new byte[]{0x48,0x05});
        map.put("HUH", new byte[]{0x48,0x06});
        map.put("IAM", new byte[]{0x48,0x07});
        map.put("LOD", new byte[]{0x48,0x08});
        map.put("MAP", new byte[]{0x48,0x09});
        map.put("MDF", new byte[]{0x48,0x0A});
        map.put("MIS", new byte[]{0x48,0x0B});
        map.put("NME", new byte[]{0x48,0x0C});
        map.put("NOT", new byte[]{0x48,0x0D});
        map.put("NOW", new byte[]{0x48,0x0E});
        map.put("OBS", new byte[]{0x48,0x0F});
        map.put("OFF", new byte[]{0x48,0x10});
        map.put("ORD", new byte[]{0x48,0x11});
        map.put("OUT", new byte[]{0x48,0x12});
        map.put("PRN", new byte[]{0x48,0x13});
        map.put("REJ", new byte[]{0x48,0x14});
        map.put("SCO", new byte[]{0x48,0x15});
        map.put("SLO", new byte[]{0x48,0x16});
        map.put("SND", new byte[]{0x48,0x17});
        map.put("SUB", new byte[]{0x48,0x18});
        map.put("SVE", new byte[]{0x48,0x19});
        map.put("THX", new byte[]{0x48,0x1A});
        map.put("TME", new byte[]{0x48,0x1B});
        map.put("YES", new byte[]{0x48,0x1C});
		map.put("ADM", new byte[]{0x48,0x1D});
	    map.put("SMR", new byte[]{0x48, 0x1E});
        // Parameters (0x49)
        map.put("AOA", new byte[]{0x49,0x00});
        map.put("BTL", new byte[]{0x49,0x01});
        map.put("ERR", new byte[]{0x49,0x02});
        map.put("LVL", new byte[]{0x49,0x03});
        map.put("MRT", new byte[]{0x49,0x04});
        map.put("MTL", new byte[]{0x49,0x05});
        map.put("NPB", new byte[]{0x49,0x06});
        map.put("NPR", new byte[]{0x49,0x07});
        map.put("PDA", new byte[]{0x49,0x08});
        map.put("PTL", new byte[]{0x49,0x09});
        map.put("RTL", new byte[]{0x49,0x0A});
        map.put("UNO", new byte[]{0x49,0x0B});
	map.put("DSD", new byte[]{0x49,0x0D});
        // Press (0x4A)
        map.put("ALY", new byte[]{0x4A,0x00});
        map.put("AND", new byte[]{0x4A,0x01});
        map.put("BWX", new byte[]{0x4A,0x02});
        map.put("DMZ", new byte[]{0x4A,0x03});
        map.put("ELS", new byte[]{0x4A,0x04});
        map.put("EXP", new byte[]{0x4A,0x05});
        map.put("FWD", new byte[]{0x4A,0x06});
        map.put("FCT", new byte[]{0x4A,0x07});
        map.put("FOR", new byte[]{0x4A,0x08});
        map.put("HOW", new byte[]{0x4A,0x09});
        map.put("IDK", new byte[]{0x4A,0x0A});
        map.put("IFF", new byte[]{0x4A,0x0B});
        map.put("INS", new byte[]{0x4A,0x0C});
        map.put("IOU", new byte[]{0x4A,0x0D});
        map.put("OCC", new byte[]{0x4A,0x0E});
        map.put("ORR", new byte[]{0x4A,0x0F});
        map.put("PCE", new byte[]{0x4A,0x10});
        map.put("POB", new byte[]{0x4A,0x11});
        map.put("PPT", new byte[]{0x4A,0x12});
        map.put("PRP", new byte[]{0x4A,0x13});
        map.put("QRY", new byte[]{0x4A,0x14});
        map.put("SCD", new byte[]{0x4A,0x15});
        map.put("SRY", new byte[]{0x4A,0x16});
        map.put("SUG", new byte[]{0x4A,0x17});
        map.put("THK", new byte[]{0x4A,0x18});
        map.put("THN", new byte[]{0x4A,0x19});
        map.put("TRY", new byte[]{0x4A,0x1A});
        map.put("UOM", new byte[]{0x4A,0x1B});
        map.put("VSS", new byte[]{0x4A,0x1C});
        map.put("WHT", new byte[]{0x4A,0x1D});
        map.put("WHY", new byte[]{0x4A,0x1E});
        map.put("XDO", new byte[]{0x4A,0x1F});
        map.put("XOY", new byte[]{0x4A,0x20});
        map.put("YDO", new byte[]{0x4A,0x21});
        map.put("CHO", new byte[]{0x4A,0x22});
        map.put("BCC", new byte[]{0x4A,0x23});
        map.put("UNT", new byte[]{0x4A,0x24});
        // Inland non-SC provinces (0x50)
        map.put("BOH", new byte[]{0x50,0x00});
        map.put("BUR", new byte[]{0x50,0x01});
        map.put("GAL", new byte[]{0x50,0x02});
        map.put("RUH", new byte[]{0x50,0x03});
        map.put("SIL", new byte[]{0x50,0x04});
        map.put("TYR", new byte[]{0x50,0x05});
        map.put("UKR", new byte[]{0x50,0x06});
        // Inland SC provinces (0x51)
        map.put("BUD", new byte[]{0x51,0x07});
        map.put("MOS", new byte[]{0x51,0x08});
        map.put("MUN", new byte[]{0x51,0x09});
        map.put("PAR", new byte[]{0x51,0x0A});
        map.put("SER", new byte[]{0x51,0x0B});
        map.put("VIE", new byte[]{0x51,0x0C});
        map.put("WAR", new byte[]{0x51,0x0D});
        // Sea non-SC provinces (0x52)
        map.put("ADR", new byte[]{0x52,0x0E});
        map.put("AEG", new byte[]{0x52,0x0F});
        map.put("BAL", new byte[]{0x52,0x10});
        map.put("BAR", new byte[]{0x52,0x11});
        map.put("BLA", new byte[]{0x52,0x12});
        map.put("EAS", new byte[]{0x52,0x13});
        map.put("ECH", new byte[]{0x52,0x14});
        map.put("GOB", new byte[]{0x52,0x15});
        map.put("GOL", new byte[]{0x52,0x16});
        map.put("HEL", new byte[]{0x52,0x17});
        map.put("ION", new byte[]{0x52,0x18});
        map.put("IRI", new byte[]{0x52,0x19});
        map.put("MAO", new byte[]{0x52,0x1A});
        map.put("NAO", new byte[]{0x52,0x1B});
        map.put("NTH", new byte[]{0x52,0x1C});
        map.put("NWG", new byte[]{0x52,0x1D});
        map.put("SKA", new byte[]{0x52,0x1E});
        map.put("TYS", new byte[]{0x52,0x1F});
        map.put("WES", new byte[]{0x52,0x20});
        // Coastal non-SC provinces (0x54)
        map.put("ALB", new byte[]{0x54,0x21});
        map.put("APU", new byte[]{0x54,0x22});
        map.put("ARM", new byte[]{0x54,0x23});
        map.put("CLY", new byte[]{0x54,0x24});
        map.put("FIN", new byte[]{0x54,0x25});
        map.put("GAS", new byte[]{0x54,0x26});
        map.put("LVN", new byte[]{0x54,0x27});
        map.put("NAF", new byte[]{0x54,0x28});
        map.put("PIC", new byte[]{0x54,0x29});
        map.put("PIE", new byte[]{0x54,0x2A});
        map.put("PRU", new byte[]{0x54,0x2B});
        map.put("SYR", new byte[]{0x54,0x2C});
        map.put("TUS", new byte[]{0x54,0x2D});
        map.put("WAL", new byte[]{0x54,0x2E});
        map.put("YOR", new byte[]{0x54,0x2F});
        // Coastal SC provinces (0x55)
        map.put("ANK", new byte[]{0x55,0x30});
        map.put("BEL", new byte[]{0x55,0x31});
        map.put("BER", new byte[]{0x55,0x32});
        map.put("BRE", new byte[]{0x55,0x33});
        map.put("CON", new byte[]{0x55,0x34});
        map.put("DEN", new byte[]{0x55,0x35});
        map.put("EDI", new byte[]{0x55,0x36});
        map.put("GRE", new byte[]{0x55,0x37});
        map.put("HOL", new byte[]{0x55,0x38});
        map.put("KIE", new byte[]{0x55,0x39});
        map.put("LON", new byte[]{0x55,0x3A});
        map.put("LVP", new byte[]{0x55,0x3B});
        map.put("MAR", new byte[]{0x55,0x3C});
        map.put("NAP", new byte[]{0x55,0x3D});
        map.put("NWY", new byte[]{0x55,0x3E});
        map.put("POR", new byte[]{0x55,0x3F});
        map.put("ROM", new byte[]{0x55,0x40});
        map.put("RUM", new byte[]{0x55,0x41});
        map.put("SEV", new byte[]{0x55,0x42});
        map.put("SMY", new byte[]{0x55,0x43});
        map.put("SWE", new byte[]{0x55,0x44});
        map.put("TRI", new byte[]{0x55,0x45});
        map.put("TUN", new byte[]{0x55,0x46});
        map.put("VEN", new byte[]{0x55,0x47});
        // Bi-coastal SC provinces (0x57)
        map.put("BUL", new byte[]{0x57,0x48});
        map.put("SPA", new byte[]{0x57,0x49});
        map.put("STP", new byte[]{0x57,0x4A});
        return map;
    }

    public static void main(String[] args){
        if(args.length != 2){
            System.err.println("Usage:\n" +
                               "  Token -s <string>");
            System.exit(1);
        }
        if(args[0].equals("-s")){
            try {
                LinkedList list = new LinkedList();
                StringTokenizer wsTok = new StringTokenizer(args[1], " ");
                while(wsTok.hasMoreTokens()){
                    String s = wsTok.nextToken();
                    StringTokenizer parenTok =
                        new StringTokenizer(s, "()", true);
                    while(parenTok.hasMoreTokens()){
                        list.add(parenTok.nextToken());
                    }
                }
                int numTokens = list.size();
                Iterator iter = list.iterator();
                System.out.println("Converting " + numTokens + " tokens.");
                byte[] bytes = new byte[numTokens * 2];
                int t = 0;
                while(iter.hasNext()){
                    String token = (String)iter.next();
                    byte[] bits;
                    if(token.startsWith("'")){
                        bits = convertText(token);
                        if(bits.length != 1){ // Grow/Shrink the target array
                            int newSize = bytes.length + bits.length - 2;
                            byte[] newArray = new byte[newSize];
                            System.arraycopy(bytes, 0, newArray,
                                             0, t);
                            bytes = newArray;
                        }
                    } else {
                        bits = convert(token);
                    }
                    System.arraycopy(bits, 0, bytes, t, bits.length);
                    t += bits.length;
                }
                for(int i = 0; i < bytes.length; i++){
                    System.out.print("0x" + Integer.toHexString(bytes[i]));
                    if(i == (bytes.length - 1)){
                        System.out.println();
                    } else {
                        System.out.print(", ");
                    }
                }
            } catch (UnknownTokenException ute){
                System.out.println("Unknown token: " + ute.getToken());
            }
        }
    }

    /**
     * This class is needed as simple byte-arrays can't be used as
     * keys for the maps. (Their equals() methods don't return true
     * for arrays with the same content)
     */
    static class ByteArrayEntry {
        byte high;
        byte low;
 
        public ByteArrayEntry(byte high, byte low){
            this.high = high;
            this.low = low;
        }

        public int hashCode(){
            return high ^ low;
        }

        public boolean equals(Object o){
            if(o instanceof ByteArrayEntry){
                ByteArrayEntry other = (ByteArrayEntry)o;
                return (high == other.high) && (low == other.low);
            }
            return false;
        }
    } 
}
