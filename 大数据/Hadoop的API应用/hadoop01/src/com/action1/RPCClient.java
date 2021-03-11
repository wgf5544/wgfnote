package com.action1;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

public class RPCClient {

	public static void main(String[] args) throws IOException {
		
		Barty proxy = RPC
				/**��ȡ�������
				 * ����1ΪjdkĬ���Զ�������Ҫ�Ľӿڣ���Ҫ���÷�������ʵ�ֵĽӿ�
				 * ����2ΪversionID���ڽӿ��ж����versionID��ò��ֻҪ��ID�ͺã�ֵ����ν���д�ȷ��
				 * ����3Ϊ���������ã�����1Ϊ��ַ���ַ�����������2Ϊ�������˿ںţ����������Ǳ����õ�
				 * ����4λconfiguration����
				 */
				.getProxy(Barty.class, 1,
				new InetSocketAddress("192.168.2.1", 55555), 
				new Configuration());
		String sayHi = proxy.sayHI("tomcat");
		RPC.stopProxy(proxy);
		System.out.println(sayHi);
	}
}
