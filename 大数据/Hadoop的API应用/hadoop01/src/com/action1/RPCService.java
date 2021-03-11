package com.action1;

import java.io.IOException;

import org.apache.hadoop.HadoopIllegalArgumentException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.RPC.Server;

public class RPCService implements Barty{

	public String sayHI(String name){
		return "HI~"+name;
	}
	
	public static void main(String[] args) throws Exception, IOException {
		// TODO Auto-generated method stub
		Configuration configuration=new Configuration();
		//RPCΪhadoop�ṩ�Ĺ����࣬��������һ������
		Server server=new RPC
				//ָ�������ߣ���ǰΪconfiguration���鿴����Ҫ��Ĳ������ɻ�Ϥ��Ҫ���Ĳ�������
				.Builder(configuration)
				//���÷������ĵ�ַ����ǰΪ����������ֻҪ�����ϴ��ڵ�IP�����ԣ���ʵ����������������
				//ע�⣺����˺Ϳͻ��˶��ڱ�������ʱ�κα������ڵ�IP�����ԣ�����ͻ�����Linux����������Ҫʹ������������Ķ˿ںţ��統ǰʹ��VMnet8�����ַΪ192.168.2.1
				.setBindAddress("192.168.2.1")
				//����ʵ������Ҫ����˭�ķ�������ǰΪRPCServer�е�sayHI����
				.setInstance(new RPCService())
				//���ö˿ںţ���Ҫ��֤��ǰ�˿�δ��ռ��
				.setPort(55555)
				//�ͻ��˵��÷���˵ķ�����Ҫ�õ�һ��������jdkĬ�ϵĶ�̬��������Ҫһ���ӿڣ����Ե�ǰ����Ҫʵ��һ���ӿڣ�Barty��Ϊ��ǰ��Ҫʵ�ֵĽӿ�
				.setProtocol(Barty.class)
				//��������
				.build();
		server.start();
	}

}
