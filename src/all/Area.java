package all;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class Area {
	public static Set<String> lacset=new HashSet<String>();
	public static String  clound="imsi,imei,lac,cellid,uplink,downlink,updownlinktime,duration,nodeID,msisdn";
	static
	{
		BufferedReader bf=null;
		try {
			//bf=new BufferedReader(new FileReader("laci.csv"));
			bf=new BufferedReader(new InputStreamReader(Area.class.getResourceAsStream("/lacci.csv")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String line=null;
		String str[]=null;
		try {
			while((line=bf.readLine())!=null)
			{
				str=line.split(",");
				
				if(str!=null)
				{
					StringBuffer sb=new StringBuffer();
					sb.append(str[2]);
					sb.append(str[3]);
					lacset.add(sb.toString());
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
