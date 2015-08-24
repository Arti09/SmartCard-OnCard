package MasterMind;

public class CodeBreaker implements IMasterMind
{
	byte[] name = new byte[MM_CDATA_LENGTH_NAME];
	byte[] pin = new byte[MM_CDATA_LENGTH_PIN];
	byte[] highscore = new byte[MM_CDATA_LENGTH_HIGHSCORE];
}
