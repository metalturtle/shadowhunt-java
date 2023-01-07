package client;

public class SentPacketSequence{
	int conid;
	long SEQUENCE;
	long start_timestamp,end_timestamp;
	
	SentPacketSequence(int conid,long SEQUENCE)
	{
		this.conid = conid;
		this.SEQUENCE = SEQUENCE;
	}
}
