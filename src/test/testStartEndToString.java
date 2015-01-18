package test;

public class testStartEndToString {

	/**
	 * @param args
	 */
	public static String byteStartEndToString(byte[]src,int start,int end)
	{
		StringBuilder stringBuilder = new StringBuilder("");  
	    if (src == null || src.length <= 0) {  
	        return null;  
	    }  
	    for (int i = start; i < src.length; i++) {  
	        int v = src[i] & 0xFF;  
	        char hv = (char)(v);  
	       
	        stringBuilder.append(hv);  
	    }  
	    String hexString=stringBuilder.toString();
	   // System.out.println(hexString);
	    return hexString;  
	} 
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		byte src[]={0x05,0x33,0x67,0x6e,0x65,0x74};
		String content=testStartEndToString.byteStartEndToString(src, 1, 5);
		System.out.println(content);
		

	}

}
