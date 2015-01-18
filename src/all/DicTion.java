package all;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import templateallfield.DefineForest;
import tools.Dir;
public class DicTion {
	public static DefineForest scdrdic=new DefineForest();
	public static DefineForest sgwdic=new DefineForest();
	public static DefineForest zxsdrdic=new DefineForest();
	public static DefineForest pgwdic=new DefineForest();
	public static DefineForest egcdrdic=new DefineForest();
	public static DefineForest trafficdatavolumesdic=new DefineForest();
	public static DefineForest sgwtrafficdatavolumesdic=new DefineForest();
	public static DefineForest pgwtrafficdatavolumesdic=new DefineForest();
	public static DefineForest zxscdrtrafficdatavolumesdic=new DefineForest();
	public static DefineForest egcdrtrafficdatavolumesdic=new DefineForest();
	public static String scdrhead="recordType,imsi,imei,sgsnip,msNetworkCapability,routingArea,lac,cellid,chargingID,ggsnip,apn,pdpType,pdpip,uplink,downlink,linkdate,updownlinktime,recordOpeningTime,duration,causeForRecClosing,recSequenceNumList,nodeID,localSequenceNumberList,apnSelectionMode,accessPointName,msisdn,chargingCharacteristics,rATType,chChSelectionMode,dynamicAddressFlag,sgsnPLMNIdentifier,consolidationResult,filename";
	public static String sgwhead="recordType,imsi,imei,sgwip,servingNodeAddress,routingArea,lac,cellid,chargingID,servedPDPPDNAddress,apn,pdpPDNType,pdpip,uplink,downlink,linkdate,updownlinktime,recordOpeningTime,duration,causeForRecClosing,recSequenceNumList,nodeID,localSequenceNumberList,apnSelectionMode,accessPointName,msisdn,chargingCharacteristics,rATType,chChSelectionMode,dynamicAddressFlag,sgsnPLMNIdentifier,servedIMEISV,mSTimeZone,userLocationInformation,cAMELChargingInformation,servingNodeType,pGWAddressUsed,pGWPLMNIdentifier,startTime,stopTime,pDNConnectionChargingID,filename";
	public static String pgwhead="recordType,imsi,imei,sgwip,servingNodeAddress,routingArea,lac,cellid,chargingID,servedPDPPDNAddress,apn,pdpPDNType,pdpip,uplink,downlink,linkdate,updownlinktime,recordOpeningTime,duration,causeForRecClosing,recSequenceNumList,nodeID,localSequenceNumberList,apnSelectionMode,accessPointName,msisdn,chargingCharacteristics,rATType,chChSelectionMode,dynamicAddressFlag,sgsnPLMNIdentifier,servedIMEISV,mSTimeZone,userLocationInformation,cAMELChargingInformation,servingNodeType,pGWAddressUsed,pGWPLMNIdentifier,startTime,stopTime,pDNConnectionChargingID,filename";
	public static String egcdrhead="recordType,imsi,imei,sgwip,servingNodeAddress,routingArea,lac,cellid,chargingID,servedPDPPDNAddress,apn,pdpPDNType,pdpip,uplink,downlink,linkdate,updownlinktime,recordOpeningTime,duration,causeForRecClosing,recSequenceNumList,nodeID,localSequenceNumberList,apnSelectionMode,accessPointName,msisdn,chargingCharacteristics,rATType,chChSelectionMode,dynamicAddressFlag,sgsnPLMNIdentifier,servedIMEISV,mSTimeZone,userLocationInformation,filename";
	public static String suffix=".gz"; 
	public static int fieldsize=0;
	public static int scdrfieldsize=32;
	public static int zxscdrfieldsize=32;
	public static int sgwrfieldsize=44;
	public static String movedirstr="";//需要移动到的目录；
	public static String outdir=null;//解码生成的decode文件的目录；
	public static Dir movedir=null;
	
	static
	{
		initSGWDic(sgwdic);
		initPGWDic(pgwdic);
		initSCDRDic(scdrdic);
		initZXSCDRDic(zxsdrdic);
		initEGCDRDic(egcdrdic);
		initTrafficdatavolumes(trafficdatavolumesdic);
		initSgwTrafficdatavolumes(sgwtrafficdatavolumesdic);
		initPgwTrafficdatavolumes(pgwtrafficdatavolumesdic);
		initZxScdrTrafficdatavolumes(zxscdrtrafficdatavolumesdic);
		initTrafficdatavolumes("trafficdatavolumes-egcdr",egcdrtrafficdatavolumesdic);
	}
	public static void initTrafficdatavolumes(String filename,DefineForest dic)
	{
		readTagDistinctTLVTV("trafficdatavolumes",dic);
	}
	public static void initTrafficdatavolumes(DefineForest dic)
	{
		readTagDistinctTLVTV("trafficdatavolumes",dic);
	}
	public static void initSgwTrafficdatavolumes(DefineForest dic)
	{
		readTagDistinctTLVTV("trafficdatavolumes-sgw",dic);
	}
	public static void initPgwTrafficdatavolumes(DefineForest dic)
	{
		readTagDistinctTLVTV("trafficdatavolumes-pgw",dic);
	}
	public static void  initZxScdrTrafficdatavolumes(DefineForest dic)
	{
		readTagDistinctTLVTV("trafficdatavolumes-zxscdr",dic);
	}
	public static void initSCDRDic(DefineForest dic)
	{
		readTagDistinctTLVTV("scdrr9",dic);
	}
	public static void initZXSCDRDic(DefineForest dic)
	{
		readTagDistinctTLVTV("zxscdr",dic);
	}
	public static void initSGWDic(DefineForest dic)
	{
		readTagDistinctTLVTV("sgw",dic);
	}
	public static void initPGWDic(DefineForest dic)
	{
		readTagDistinctTLVTV("pgw",dic);
	}
	public static void initEGCDRDic(DefineForest dic)
	{
		readTagDistinctTLVTV("egcdr",dic);
	}
	//没有 tlv参数的默认为0
	public static void readTag(String name,DefineForest dic)
	{
		InputStream in;
		BufferedReader bf;
		in=DicTion.class.getResourceAsStream("/"+name+".txt");
		bf=new BufferedReader(new InputStreamReader(in));
		String line=null;
		byte []tagb=null;
		int way=0;
		int pos=0;
		try {
			for(;(line=bf.readLine())!=null;)
			{
				String []tag=line.split("\\|");
				tagb=changeToTag(tag[0]);
				way=Integer.parseInt(tag[1]);
				try {
					pos=Integer.parseInt(tag[3]);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				dic.insertTag(tagb,way,0,pos);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void readTagDistinctTLVTV(String name,DefineForest dic)
	{
		InputStream in;
		BufferedReader bf;
		in=DicTion.class.getResourceAsStream("/"+name+".txt");
		bf=new BufferedReader(new InputStreamReader(in));
		String line=null;
		byte []tagb=null;
		int way=0;
		int tlv=0;
		int pos=0;
		try {
			for(;(line=bf.readLine())!=null;)
			{
				String []tag=line.split("\\|");
				tagb=changeToTag(tag[0]);
				way=Integer.parseInt(tag[1]);
				tlv=Integer.parseInt(tag[2]);
				pos=Integer.parseInt(tag[3]);
				dic.insertTag(tagb,way,tlv,pos);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static byte[]changeToTag(String tagstr)
	{
		byte []b=HexString.hexStr2Bytes(tagstr);
		return b;
	}
	public static void main(String args[])
	{
		DefineForest forest=DicTion.scdrdic;
	}
	
}
