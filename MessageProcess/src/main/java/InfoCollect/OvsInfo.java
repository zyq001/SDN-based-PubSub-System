package InfoCollect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class
		OvsInfo {

	public static void main(String args[]) throws Exception {

		OvsInfo a = new OvsInfo();

		long s = a.getByte();
		System.out.println(s);

//		for(String x : s){
//			System.out.println(x);
//		}


	}

	public static long getByte() throws IOException {

		Process process = null;
		ArrayList<String> result = new ArrayList<String>();

		process = Runtime.getRuntime().exec("ovs-ofctl dump-ports br0");
		BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = "";
		while ((line = input.readLine()) != null) {
			result.add(line);
		}
		input.close();


		int index = result.get(2).indexOf("byte");
		String tmp = result.get(2).substring(index + 6);
		int index2 = tmp.indexOf(",");
		tmp = tmp.substring(0, index2);

		long num = Long.parseLong(tmp);
//
//        System.out.println(tmp);


		return num;
	}

	public ArrayList<String> getAllBridge() throws Exception {

		Process process = null;
		ArrayList<String> result = new ArrayList<String>();

		process = Runtime.getRuntime().exec("ovs-vsctl list-br");
		BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = "";
		while ((line = input.readLine()) != null) {
			result.add(line);
		}
		input.close();

		return result;
	}

	public ArrayList<String> getAllBridgeInfo() throws Exception {

		Process process = null;
		ArrayList<String> result = new ArrayList<String>();

		process = Runtime.getRuntime().exec("ovs-vsctl show");
		BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = "";
		while ((line = input.readLine()) != null) {
			result.add(line);
		}
		input.close();

		return result;
	}

	public ArrayList<String> getBridgeDetailedInfo(String bridge) throws Exception {

		Process process = null;
		ArrayList<String> result = new ArrayList<String>();


		process = Runtime.getRuntime().exec("ovs-ofctl show " + bridge);
		BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = "";
		while ((line = input.readLine()) != null) {
			result.add(line);
		}
		input.close();

		return result;
	}

	public ArrayList<String> getBridgePorts(String bridge) throws Exception {

		Process process = null;
		ArrayList<String> result = new ArrayList<String>();


		process = Runtime.getRuntime().exec("ovs-vsctl list-ports " + bridge);
		BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = "";
		while ((line = input.readLine()) != null) {
			result.add(line);
		}
		input.close();

		return result;
	}

	public ArrayList<String> getBridgeIfaces(String bridge) throws Exception {

		Process process = null;
		ArrayList<String> result = new ArrayList<String>();


		process = Runtime.getRuntime().exec("ovs-vsctl list-ifaces " + bridge);
		BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = "";
		while ((line = input.readLine()) != null) {
			result.add(line);
		}
		input.close();

		return result;
	}

	public ArrayList<String> getBridgeTablesInfo(String bridge) throws Exception {

		Process process = null;
		ArrayList<String> result = new ArrayList<String>();


		process = Runtime.getRuntime().exec("ovs-ofctl dump-tables " + bridge);
		BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = "";
		while ((line = input.readLine()) != null) {
			result.add(line);
		}
		input.close();

		return result;
	}

	public ArrayList<String> getBridgePortDetailedInfo(String bridge) throws Exception {

		Process process = null;
		ArrayList<String> result = new ArrayList<String>();


		process = Runtime.getRuntime().exec("ovs-ofctl dump-ports " + bridge);
		BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = "";
		while ((line = input.readLine()) != null) {
			result.add(line);
		}
		input.close();

		return result;
	}

	public ArrayList<String> getBridgeFlowInfo(String bridge) throws Exception {

		Process process = null;
		ArrayList<String> result = new ArrayList<String>();


		process = Runtime.getRuntime().exec("ovs-ofctl dump-flows " + bridge);
		BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = "";
		while ((line = input.readLine()) != null) {
			result.add(line);
		}
		input.close();

		return result;
	}

	public HashMap<Double, Integer> getPacktNumber() throws Exception {

		HashMap<Double, Integer> result = new HashMap<Double, Integer>();

		result.put(1.0, 0);
		result.put(1.1, 0);

		result.put(2.0, 0);
		result.put(2.1, 0);

		result.put(3.0, 0);
		result.put(3.1, 0);

		result.put(4.0, 0);
		result.put(4.1, 0);

		OvsInfo oi = new OvsInfo();

		ArrayList<String> re = new ArrayList<String>();
		re = oi.getBridgeFlowInfo("br0");

		for (String x : re) {

			if (x.contains("tp_src=30003")) {

				String[] tmp = x.split(",");

				String packetNum = tmp[3].substring(11, tmp[3].length());

				//System.out.println(packetNum);

				result.put(3.0, Integer.parseInt(packetNum));

				String byteNum = tmp[4].substring(9, tmp[4].length());
				result.put(3.1, Integer.parseInt(byteNum));

			} else if (x.contains("tp_src=30002")) {

				String[] tmp = x.split(",");

				String packetNum = tmp[3].substring(11, tmp[3].length());
				result.put(2.0, Integer.parseInt(packetNum));

				String byteNum = tmp[4].substring(9, tmp[4].length());
				result.put(2.1, Integer.parseInt(byteNum));

			} else if (x.contains("tp_src=30001")) {

				String[] tmp = x.split(",");

				String packetNum = tmp[3].substring(11, tmp[3].length());
				result.put(1.0, Integer.parseInt(packetNum));

				String byteNum = tmp[4].substring(9, tmp[4].length());
				result.put(1.1, Integer.parseInt(byteNum));

			} else if (!x.contains("NXST_FLOW")) {

				String[] tmp = x.split(",");

				String packetNum = tmp[3].substring(11, tmp[3].length());
				result.put(4.0, Integer.parseInt(packetNum));

				String byteNum = tmp[4].substring(9, tmp[4].length());
				result.put(4.1, Integer.parseInt(byteNum));
			}
		}

		return result;
	}

}
