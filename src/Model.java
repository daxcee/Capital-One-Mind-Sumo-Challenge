import java.util.Date;

public class Model implements Comparable<Model> 
{
	public int ID;
	public int SubID;
	public int Amount;
	public Date TransactionDate;

	public Model(int ID,int SubID,int Amount, Date TransactionDate)
	{
		this.ID = ID;
		this.SubID = SubID;
		this.Amount = Amount;
		this.TransactionDate = TransactionDate;
	}
	
	public int getSubID() 
	{
		return SubID;
	}
	public Date getTransactionDate() 
	{
		return TransactionDate;
	}

	public int compareTo(Model o) 
	{
		Model e = (Model)o;
		if(this.SubID > e.getSubID())
			return 1;
		if(this.SubID < e.getSubID())
			return -1;
		if(this.SubID == e.getSubID())
			return 0;
		return 0;
	}
}
