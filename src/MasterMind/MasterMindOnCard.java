package MasterMind;

import javacard.framework.APDU;
import javacard.framework.ISO7816;
import javacard.framework.Applet;
import javacard.framework.ISOException;
import javacard.framework.Util;

public class MasterMindOnCard extends Applet implements IMasterMind
{
	private CodeBreaker[] breakerDB;
	
	public MasterMindOnCard()
	{
		breakerDB = new CodeBreaker[10];
	}

	
	public static void install(byte[] bArray, short bOffset, byte bLength)
	{
		// GP-compliant JavaCard applet registration
		new MasterMindOnCard().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
	}

	
	public void process(APDU apdu)
	{
		// Good practice: Return 9000 on SELECT
		if (selectingApplet())
		{
			return;
		}
		
		byte responseCode = MM_RESPONSE_FAILURE;
		byte[] buf = apdu.getBuffer();
		
		if (MM_CLA != buf[ISO7816.OFFSET_CLA])
		{
			ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
		}
		
		switch (buf[ISO7816.OFFSET_INS])
		{
		 // Example create User: name Marco pin 1 2 3 4 highscore 0000 -> 800100001E4d6172636f0000000000c20ad4d76fe97759aa27a0c99bff671001
		case MM_INS_CREATE:
		{
			CodeBreaker codeBreaker = new CodeBreaker();
			// search for free space in db
			for (byte i=0; i<breakerDB.length; i++)
			{
				if (null == breakerDB[i])
				{
					// if a free space was found add received breaker to it
					// nothing found no user is created and 
					breakerDB[i] = codeBreaker;
					Util.arrayCopy(buf, (short)(ISO7816.OFFSET_CDATA + MM_CDATA_START_NAME),
							breakerDB[i].name, (short)0, (short)MM_CDATA_LENGTH_NAME);
					Util.arrayCopy(buf, (short)(ISO7816.OFFSET_CDATA + MM_CDATA_START_PIN),
							breakerDB[i].pin, (short)0, (short)MM_CDATA_LENGTH_PIN);
					Util.arrayCopy(buf, (short)(ISO7816.OFFSET_CDATA + MM_CDATA_START_HIGHSCORE),
							breakerDB[i].highscore, (short)0, (short)MM_CDATA_LENGTH_HIGHSCORE);
					responseCode = MM_RESPONSE_EVERYTHING_FINE;
					break;
				}
			}
			buf[0] = responseCode;
			apdu.setOutgoingAndSend((byte)0x00, (byte)5);
			break;
		}
		case MM_INS_LOGIN: // Example Login: 800100001B4d6172636f000000000c20ad4d76fe97759aa27a0c99bff671005
		{
			byte[] name = new byte[MM_CDATA_LENGTH_NAME];
			byte[] pin = new byte[MM_CDATA_LENGTH_PIN];
		
			// get name and pin from apdu
			Util.arrayCopy(buf,(short)(ISO7816.OFFSET_CDATA + MM_CDATA_START_NAME),
					name, (short)0, (short)MM_CDATA_LENGTH_NAME);
			Util.arrayCopy(buf,(short)(ISO7816.OFFSET_CDATA + MM_CDATA_START_PIN),
					pin, (short)0, (short)MM_CDATA_LENGTH_PIN);
		
			// traverse breakerDB to check if name and pin is valid
			for (short i = 0; i < breakerDB.length; i++)
			{
				boolean nameEqual = (0 == Util.arrayCompare(breakerDB[i].name, (short)0, name, (short)0, (short)name.length)); 
				boolean pinEqual = (0 == Util.arrayCompare(breakerDB[i].pin, (short)0, pin, (short)0, (short)pin.length)); 
				if (breakerDB[i] != null && nameEqual && pinEqual)
				{
					Util.arrayCopy(breakerDB[i].highscore, (short)0, buf, (short)1, (short)MM_CDATA_LENGTH_HIGHSCORE);
					responseCode = 0x00;
					break;
				}
			}
			buf[0] = responseCode;
			apdu.setOutgoingAndSend((byte)0x00, MM_LENGTH_RESPONSE_LOGIN);
			break;
		}
		case MM_INS_UPDATE: // Example Update: 800300001E4d6172646f000000000000c20ad4d76fe97759aa27a0c99bff671001
		{
			responseCode = MM_RESPONSE_FAILURE;
			byte[] name = new byte[MM_CDATA_LENGTH_NAME];
			byte[] pin = new byte[MM_CDATA_LENGTH_PIN];
			byte[] highscore = new byte[MM_CDATA_LENGTH_HIGHSCORE];

			Util.arrayCopy(buf,(short)(ISO7816.OFFSET_CDATA + MM_CDATA_START_NAME),
					name, (short)0, (short)MM_CDATA_LENGTH_NAME);
			Util.arrayCopy(buf,(short)(ISO7816.OFFSET_CDATA + MM_CDATA_START_PIN),
					pin, (short)0, (short)MM_CDATA_LENGTH_PIN);
			Util.arrayCopy(buf,(short)(ISO7816.OFFSET_CDATA + MM_CDATA_START_HIGHSCORE),
					highscore, (short)0, (short)MM_CDATA_LENGTH_HIGHSCORE);

			for (short i = 0; i < breakerDB.length; i++)
			{
				boolean nameEqual = (Util.arrayCompare(breakerDB[i].name, (short)0, name,
												(short)0, (short)name.length) == 0); 
				boolean pinEqual = (Util.arrayCompare(breakerDB[i].pin, (short)0, pin, (short)0,
												(short)pin.length) == 0); 
				if ((breakerDB[i] != null) && nameEqual && pinEqual)
				{
					CodeBreaker codeBreaker = breakerDB[i];
					codeBreaker.highscore = highscore;
					responseCode = MM_RESPONSE_EVERYTHING_FINE;
				}
			}
			buf[0] = responseCode;
			apdu.setOutgoingAndSend((byte)0x00, (byte)0x01);
			break;
		}
		default:
			// good practice: If you don't know the INStruction, say so:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}
}