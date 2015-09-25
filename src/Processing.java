// Mind Sumo - Use transaction data to categorize clients
// Author: Alec Reser - reserad@mail.uc.edu
// Date: 09/25/15
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class Processing <T extends Comparable<T>> {

	public static class ObtainedData
	{
		public int SubID;
		public int SubDays;
		public int SubLength;
	}
	
	public static void main(String[] args) 
	{
		ArrayList<Model> dataSet = new ArrayList<Model>();
		dataSet = Store();
		Collections.sort(dataSet);		
		ArrayList<ArrayList<Model>> OrganizedList = GetOrganizedList(dataSet);
		ArrayList<ObtainedData>_ObtainedData = GetSubscriptionType(OrganizedList);
		GetSubscriptionTime(OrganizedList, _ObtainedData);
		
		//Loop through entire collected data that was prompted and print based on Subscription Type.
		for(ObtainedData od: _ObtainedData)
		{
			String Interval = "";
			if (od.SubDays < 7 && od.SubDays > 0)
				Interval = "Daily";
			else if (od.SubDays == 7)
				Interval = "Weekly";
			else if (od.SubDays >= 28 && od.SubDays <= 32)
				Interval = "Monthly";
			else if (od.SubDays > 32)
				Interval = "Yearly";
			else
				Interval = "One-Off";
			System.out.println("Subscription ID: " + od.SubID + ", Subscription Type: " + Interval + ", " + od.SubLength + " Days");
		}
	}
	
	private static ArrayList<ObtainedData> GetSubscriptionType(ArrayList<ArrayList<Model>> OrganizedList)
	{
		//With organized data, seek out lists with either 1 or > 1 items and calculate difference between dates.
		ArrayList<ObtainedData> _ObtainedData = new ArrayList<ObtainedData>();
		for (ArrayList<Model> modelTotal : OrganizedList)
		{
			ObtainedData o = new ObtainedData();
			o.SubID = modelTotal.get(0).SubID;
			o.SubDays = 0;
			o.SubLength = 0;
			
			if (modelTotal.size() > 1)
			{
				int Difference = (int)((modelTotal.get(1).TransactionDate.getTime() - modelTotal.get(0).TransactionDate.getTime()) / 1000 / 3600 / 24);
				o.SubDays = Difference;
			}
			_ObtainedData.add(o);
		}
		return _ObtainedData;
	}
	private static void GetSubscriptionTime(ArrayList<ArrayList<Model>> OrganizedList, ArrayList<ObtainedData> _ObtainedData)
	{
		//Determine difference between sorted dates of each SubID for 1st and last entry 
		//if applicable. Therefore giving the range of the subscription.
		int count = 0;
		for (ArrayList<Model> modelTotal : OrganizedList)
		{
			Date d3 = null;
			Date d4 = null;
			int _i = 0;
			for(Model modelPartial : modelTotal)
			{
				if (_i == 0)
					d3 = modelPartial.getTransactionDate();
				_i++;
				if (_i == modelTotal.size()-1)
				{
					d4 = modelPartial.getTransactionDate();
				}
			}
			if (d4 == null)
				d4 = d3;
			_ObtainedData.get(count).SubLength = (int)((d4.getTime() - d3.getTime()) / 1000 / 3600 / 24);
			count++;
		}
	}
	
	private static ArrayList<ArrayList<Model>> GetOrganizedList(ArrayList<Model> dataSet) {
		//Organizes the sorted list of objects into a grouped hierarchy to easily obtain data for later methods.
		Model mPrevious = null;
		ArrayList<ArrayList<Model>> OrganizedList = new ArrayList<ArrayList<Model>>();
		ArrayList<Model> temp = new ArrayList<Model>();
		for (Model m: dataSet)
		{
			if (mPrevious != null && m.SubID > mPrevious.SubID)
			{
				OrganizedList.add(temp);
				temp = new ArrayList<Model>();
			}
			temp.add(m);
			mPrevious = m;
		}
		OrganizedList.add(temp);
		return OrganizedList;
	}
	public static ArrayList<Model> Store()
	{
		//Parse CSV file into Object ArrayList
		ArrayList<Model> dataSet = new ArrayList<Model>();
		String csvFile = "subscription_report.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		try 
		{
			SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd/yy");
			br = new BufferedReader(new FileReader(csvFile));
			line = br.readLine();
			while ((line = br.readLine()) != null) 
			{
				String[] data = line.split(cvsSplitBy);
				int ID = Integer.parseInt(data[0]);
				int SubID = Integer.parseInt(data[1]);
				int Amount = Integer.parseInt(data[2]);
				Date TransactionDate = sdf1.parse(data[3]);
				Model m = new Model(ID,SubID,Amount,TransactionDate);
				dataSet.add(m);
			}
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
		} 
		finally 
		{
			if (br != null) 
			{
				try 
				{
					br.close();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
		return dataSet;
	}
}
