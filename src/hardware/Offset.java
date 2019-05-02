package hardware;



public class Offset {
	
	public boolean[] address;
	private Entry entry;
	
	public Offset()
	{
		
	}
	public void write(Entry entry)
	{
		this.entry=entry;
		
		
	}
	public Entry read()
	{
		if(entry!=null)
		{
			return entry;
			
		}
		else return null;
	}
	

}
