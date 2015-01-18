package test;

public class testChoice {

	/**
	 * @param args
	 */
	public static String IPString(byte[]src,int start,int end)
	{
		StringBuilder stringBuilder = new StringBuilder(""); 
		int ipstart=end-8;
		int value=0;
		String str=null;
		for(int i=ipstart;i<=end;i++)
		{
			value=src[i];
			if(value<0) value+=256;
			str=String.format("%d", value);
			stringBuilder.append(str);
			stringBuilder.append(".");
		}
		int len=stringBuilder.length()-1;
		stringBuilder.deleteCharAt(len);
		String hexString=stringBuilder.toString();
		return hexString;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//byte []b={0x74,0x4f,0x02,-50,0x67};
		byte[]b={-123,0x01,0x02,-122,0x09};
		byte []c={0x02,0x13,-110,0x1f,0x73,-106,-44,-2,0x74};
		String sb=IPString(c,0,8);
		System.out.println(sb);

	}

}
