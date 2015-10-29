package OvsOperation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class OvsOperate {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		OvsOperate o = new OvsOperate();
		ArrayList<String> a = new ArrayList<String>();
		a.addAll(o.DeleteBridgePort("br0", "eth0"));
		for (String x : a) {
			System.out.println(x);
		}

	}

	public ArrayList<String> AddBridge(String bridge) throws IOException {

		ArrayList<String> result = new ArrayList<String>();
		Process process = null;

		//process = Runtime.getRuntime().exec("ovs-vsctl add-br " + bridge); 
		process = Runtime.getRuntime().exec("ovs-vsctl add-br " + bridge);
		BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = "";
		while ((line = input.readLine()) != null) {
			result.add(line);
		}
		input.close();

		return result;
	}

	public ArrayList<String> DeleteBridge(String bridge) throws IOException {

		ArrayList<String> result = new ArrayList<String>();
		Process process = null;

		//process = Runtime.getRuntime().exec("ovs-vsctl add-br " + bridge); 
		process = Runtime.getRuntime().exec("ovs-vsctl del-br " + bridge);
		BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = "";
		while ((line = input.readLine()) != null) {
			result.add(line);
		}
		input.close();

		return result;
	}

	public ArrayList<String> AddBridgePort(String bridge, String port) throws IOException {

		ArrayList<String> result = new ArrayList<String>();
		Process process = null;

		//process = Runtime.getRuntime().exec("ovs-vsctl add-br " + bridge); 
		process = Runtime.getRuntime().exec("ovs-vsctl add-port " + bridge + " " + port);
		//System.out.println("ovs-vsctl add-br "+bridge+" port");
		BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = "";
		while ((line = input.readLine()) != null) {
			result.add(line);
		}
		input.close();

		return result;
	}

	public ArrayList<String> DeleteBridgePort(String bridge, String port) throws IOException {

		ArrayList<String> result = new ArrayList<String>();
		Process process = null;

		//process = Runtime.getRuntime().exec("ovs-vsctl add-br " + bridge); 
		process = Runtime.getRuntime().exec("ovs-vsctl del-port " + bridge + " " + port);
		//System.out.println("ovs-vsctl add-br "+bridge+" port");
		BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = "";
		while ((line = input.readLine()) != null) {
			result.add(line);
		}
		input.close();

		return result;
	}

	public ArrayList<String> AddFlow(String cmd) throws IOException {

		ArrayList<String> result = new ArrayList<String>();
		Process process = null;

		//process = Runtime.getRuntime().exec("ovs-vsctl add-br " + bridge); 
		process = Runtime.getRuntime().exec(cmd);
		//System.out.println("ovs-vsctl add-br "+bridge+" port");
		BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = "";
		while ((line = input.readLine()) != null) {
			result.add(line);
		}
		input.close();

		return result;
	}

	public ArrayList<String> changeFlow(String cmd) throws IOException {

		ArrayList<String> result = new ArrayList<String>();
		Process process = null;

		//process = Runtime.getRuntime().exec("ovs-vsctl add-br " + bridge); 
		process = Runtime.getRuntime().exec(cmd);
		//System.out.println("ovs-vsctl add-br "+bridge+" port");
		BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = "";
		while ((line = input.readLine()) != null) {
			result.add(line);
		}
		input.close();

		return result;
	}

}
