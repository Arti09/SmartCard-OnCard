package MasterMind;

public interface IMasterMind
{
	// The MasterMind class id
	public static final byte MM_CLA = (byte)0x80;

	// The MasterMind instructionset
	public static final byte MM_INS_LOGIN = (byte)0x01;
	public static final byte MM_INS_CREATE = (byte)0x02;
	public static final byte MM_INS_UPDATE = (byte)0x03;
	
	// defines for records transmitted via apdu
	public static final byte MM_CDATA_START_NAME = 0;
	public static final byte MM_CDATA_END_NAME = 10;
	
	public static final byte MM_CDATA_START_PIN = 10;
	public static final byte MM_CDATA_END_PIN = 26;
	
	public static final byte MM_CDATA_START_HIGHSCORE = 26;
	public static final byte MM_CDATA_END_HIGHSCORE = 30;
	
	public static final byte MM_CDATA_START = 0;
	public static final byte MM_CDATA_END = 30;
	
	
	public static final byte MM_CDATA_LENGTH_NAME = MM_CDATA_END_NAME - MM_CDATA_START_NAME;
	public static final byte MM_CDATA_LENGTH_PIN = MM_CDATA_END_PIN - MM_CDATA_START_PIN;
	public static final byte MM_CDATA_LENGTH_HIGHSCORE = MM_CDATA_END_HIGHSCORE - MM_CDATA_START_HIGHSCORE;
	
	public static final byte MM_LENGTH_RESPONSE_LOGIN = 1 + MM_CDATA_LENGTH_HIGHSCORE;
	
	public static final byte MM_RESPONSE_EVERYTHING_FINE = 0x00;
	public static final byte MM_RESPONSE_FAILURE = 0x01;
}
