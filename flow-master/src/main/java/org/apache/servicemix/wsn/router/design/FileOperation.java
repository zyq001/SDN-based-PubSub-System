package org.apache.servicemix.wsn.router.design;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import org.apache.servicemix.wsn.router.admin.AdminMgr;

public class FileOperation {
	
	private PrintWriter output;
	private Scanner input;
	private String[] grpConfig=new String[10];
	
	public void WriteGroupConfiguration(GroupConfiguration grpConfig){
		String filename=grpConfig.GroupName;
		try {
			output=new PrintWriter(new FileOutputStream("./ConfigFile/"+filename+".txt"));
			output.println("代表地址:"+grpConfig.repAddr);
			output.println("TCP端口号:"+grpConfig.tPort);
			output.println("子节点数目:"+grpConfig.childrenSize);
			output.println("组播地址:"+grpConfig.mutltiAddr);
			output.println("组播端口号:"+grpConfig.uPort);
			output.println("加入超时时间:"+grpConfig.joinTimes);
			output.println("缓冲区大小:"+grpConfig.synPeriod);
			output.println("判定失效的阀值:"+grpConfig.lostThreshold);
			output.println("扫描周期:"+grpConfig.scanPeriod);
			output.println("发送周期:"+grpConfig.sendPeriod);
			
			
			output.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public GroupConfiguration ReadGroupConfiguration(String grpName){
		GroupConfiguration rtnConfig = new GroupConfiguration();
		FileInputStream isExists=LookForConfigFile(grpName);
		if(isExists==null){
			System.out.println("Default configureFile doesn't exists!");
			rtnConfig=null;
		}
		else{
			int i=0;
			input=new Scanner(isExists);
//			input.useDelimiter(System.getProperty("line.separator"));
			while(input.hasNext()){
				String line=input.next();
				if(line.trim().equals("")) {
					break;
				}
				Scanner lineManager=new Scanner(line);
				lineManager.useDelimiter(":");
				String attribute=lineManager.next();
			//	System.out.print("属性值为"+attribute);
				String value=lineManager.next();
//				System.out.println(attribute+":"+value);
				grpConfig[i]=value;
				i++;
			}
			rtnConfig.GroupName=grpName;
			rtnConfig.repAddr=grpConfig[0];
			rtnConfig.tPort=Integer.parseInt(grpConfig[1]);
			rtnConfig.childrenSize=Integer.parseInt(grpConfig[2]);
			rtnConfig.mutltiAddr=grpConfig[3];
			rtnConfig.uPort=Integer.parseInt(grpConfig[4]);
			rtnConfig.joinTimes=Integer.parseInt(grpConfig[5]);
			rtnConfig.synPeriod=Long.parseLong(grpConfig[6]);
			rtnConfig.lostThreshold=Long.parseLong(grpConfig[7]);
			rtnConfig.scanPeriod=Long.parseLong(grpConfig[8]);
			rtnConfig.sendPeriod=Long.parseLong(grpConfig[9]);
		}
		
		
		return rtnConfig;
		
	}
	
	public FileInputStream LookForConfigFile(String grpName){
		FileInputStream wantedFile = null;
		File file=new File("./ConfigFile/"+grpName+".txt");
		if(file.exists()){
			try {
				wantedFile=new FileInputStream(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		else{
//			wantedFile=null;
//		}
		return wantedFile;
	}
	
	public void WriteHistoryFile(GroupInfo deletedGroup){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("H:mm yyyy-M-dd");
			output=new PrintWriter(new FileOutputStream("./ConfigFile/History.txt",true));
			Date date=new Date();
			output.println("集群名称:"+deletedGroup.GroupName
							+"\t组代表地址:"+deletedGroup.GroupAddress
							+"\t删除时间:"+sdf.format(date));
			output.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
