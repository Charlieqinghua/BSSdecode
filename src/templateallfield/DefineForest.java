package templateallfield;

public class DefineForest {
	Node root;
	
	public Node getRoot() {
		return root;
	}
	public void setRoot(Node root) {
		this.root = root;
	}
	public DefineForest()
	{
		byte r=0x0;
		root=new Node(r);
	}

	public void insertTag(byte [] tag)
	{
		Node nodefind=root;
		for(byte b:tag)
		{
			
			Node node=nodefind.get(b);
			if(node!=null)//找到
			{
				nodefind=node;//继续找
			}
			else//没找到
			{
				Node newnode=new Node(b);
				nodefind.add(newnode);//新建节点
				nodefind=newnode;//注意后续在新建节点上操作
				
			}
		}
		nodefind.setState(3);//插入结束之后状态为3
	}
	//way 代表匹配tag的处理方式
	public void insertTag(byte [] tag,int way,int tlv,int pos)
	{
		Node nodefind=root;
		for(byte b:tag)
		{
			
			Node node=nodefind.get(b);
			if(node!=null)//找到
			{
				nodefind=node;//继续找
			}
			else//没找到
			{
				Node newnode=new Node(b);
				nodefind.add(newnode);//新建节点
				nodefind=newnode;//注意后续在新建节点上操作
				
			}
		}
		nodefind.setState(3);//插入结束之后状态为3
		nodefind.setWay(way);
		nodefind.setTlv(tlv);
		nodefind.setPos(pos);
	}
	
	public static void main(String args[])
	{
		DefineForest dic=new DefineForest();
		byte []tag1={0x1f,0x4e,0x55};
		byte []tag2={0x1f,0x56};
		dic.insertTag(tag1);
		dic.insertTag(tag2);
		//dic.match(tag1);
	}

}
