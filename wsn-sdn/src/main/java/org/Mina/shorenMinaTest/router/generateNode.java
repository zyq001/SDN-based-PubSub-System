package org.Mina.shorenMinaTest.router;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class generateNode {

	public static MsgSubsForm emptyNode = new MsgSubsForm();

	public generateNode() {
		emptyNode = generateNodeEmpty();
	}

	public static MsgSubsForm generateNodeEmpty() {

		String nodeName = "";
		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;

	}

	public static MsgSubsForm generateTestNodeAct(String nodeName) {

		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;

	}


	public static MsgSubsForm generateNodeSelf(String nodeName) {

		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		subs.add("e");
		routeNext.add("f");
		routeNext.add("g");
		routeNext.add("h");


		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;

	}

	public static MsgSubsForm generateTestNode(String nodeName) {
		MsgSubsForm testNode = generateTestNodeAct(nodeName);
		return testNode;
	}

	public static void generateNodeList(ArrayList<MsgSubsForm> nodeList) {


		MsgSubsForm node1 = generateNode1();
		MsgSubsForm node2 = generateNode2();
		MsgSubsForm node3 = generateNode3();
		MsgSubsForm node4 = generateNode4();
		MsgSubsForm node5 = generateNode5();
		MsgSubsForm node6 = generateNode6();
		MsgSubsForm node7 = generateNode7();
		MsgSubsForm node8 = generateNode8();
		MsgSubsForm node9 = generateNode9();
		MsgSubsForm node10 = generateNode10();
		MsgSubsForm node11 = generateNode11();
		MsgSubsForm node12 = generateNode12();
		MsgSubsForm node13 = generateNode13();
		MsgSubsForm node14 = generateNode14();
		MsgSubsForm node15 = generateNode15();
		MsgSubsForm node16 = generateNode16();
		MsgSubsForm node17 = generateNode17();
		MsgSubsForm node18 = generateNode18();
		MsgSubsForm node19 = generateNode19();
		MsgSubsForm node20 = generateNode20();
		MsgSubsForm node21 = generateNode21();
		MsgSubsForm node22 = generateNode22();
		MsgSubsForm node23 = generateNode23();
		MsgSubsForm node24 = generateNode24();
		MsgSubsForm node25 = generateNode25();
		MsgSubsForm node26 = generateNode26();
		MsgSubsForm node27 = generateNode27();
		MsgSubsForm node28 = generateNode28();
		MsgSubsForm node29 = generateNode29();
		MsgSubsForm node30 = generateNode30();

		nodeList.add(node1);

	}

	public static MsgSubsForm generateNode30() {

		String nodeName = "30";
		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		topicChildList.put(nodeName, emptyNode);

		subs.add("a");
		subs.add("e");
		routeNext.add("10.109.253.19");


		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;
	}

	public static MsgSubsForm generateNode29() {

		String nodeName = "29";
		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		topicChildList.put(nodeName, emptyNode);

		subs.add("a");
		routeNext.add("b");
		routeNext.add("c");
		routeNext.add("d");

		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;

	}

	public static MsgSubsForm generateNode28() {

		String nodeName = "28";
		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		topicChildList.put(nodeName, emptyNode);

		subs.add("a");
		routeNext.add("b");
		routeNext.add("c");
		routeNext.add("d");

		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;

	}

	public static MsgSubsForm generateNode27() {

		String nodeName = "27";
		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		topicChildList.put(nodeName, emptyNode);

		subs.add("a");
		routeNext.add("b");
		routeNext.add("c");
		routeNext.add("d");

		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;

	}

	public static MsgSubsForm generateNode26() {

		String nodeName = "26";
		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		topicChildList.put(nodeName, emptyNode);

		subs.add("a");
		//routeNext.add("10.109.253.15");
		routeNext.add("10.109.253.14");
		//routeNext.add("10.109.253.16");


		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;

	}

	public static MsgSubsForm generateNode25() {

		String nodeName = "25";
		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		topicChildList.put(nodeName, emptyNode);

		subs.add("e");
		routeNext.add("f");
		routeNext.add("g");
		routeNext.add("h");

		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;

	}

	public static MsgSubsForm generateNode24() {

		String nodeName = "24";
		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		//topicChildList.put(nodeName, generateNodeSelf("24"));
		topicChildList.put("30", generateNode30());
		subs.add("e");
		routeNext.add("f");
		routeNext.add("g");
		routeNext.add("h");

		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;

	}

	public static MsgSubsForm generateNode23() {

		String nodeName = "23";
		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		//topicChildList.put(nodeName, generateNodeSelf("23"));
		topicChildList.put("29", generateNode29());

		subs.add("e");
		routeNext.add("f");
		routeNext.add("g");
		routeNext.add("h");

		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;
	}

	public static MsgSubsForm generateNode22() {

		String nodeName = "22";
		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		//topicChildList.put(nodeName, generateNodeSelf("22"));
		topicChildList.put("28", generateNode28());

		subs.add("a");
		routeNext.add("b");
		routeNext.add("c");
		routeNext.add("d");

		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;
	}

	public static MsgSubsForm generateNode21() {

		String nodeName = "21";
		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		//topicChildList.put(nodeName, generateNodeSelf("21"));
		topicChildList.put("27", generateNode27());
		subs.add("a");
		routeNext.add("b");
		routeNext.add("c");
		routeNext.add("d");

		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;
	}

	public static MsgSubsForm generateNode20() {

		String nodeName = "20";
		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		//topicChildList.put(nodeName, generateNodeSelf("20"));
		topicChildList.put("26", generateNode26());
		subs.add("a");
		routeNext.add("b");
		routeNext.add("c");
		routeNext.add("d");

		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;
	}

	public static MsgSubsForm generateNode19() {

		String nodeName = "19";
		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		//topicChildList.put(nodeName, generateNodeSelf("19"));
		topicChildList.put("25", generateNode25());
		subs.add("a");
		routeNext.add("b");
		routeNext.add("c");
		routeNext.add("d");

		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;
	}

	public static MsgSubsForm generateNode18() {

		String nodeName = "18";
		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		//topicChildList.put(nodeName, generateNodeSelf("18"));
		topicChildList.put("24", generateNode24());
		subs.add("a");
		routeNext.add("b");
		routeNext.add("c");
		routeNext.add("d");

		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;
	}

	public static MsgSubsForm generateNode17() {

		String nodeName = "17";
		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		//topicChildList.put(nodeName, generateNodeSelf("17"));
		topicChildList.put("23", generateNode23());
		subs.add("a");
		routeNext.add("b");
		routeNext.add("c");
		routeNext.add("d");
		subs.add("e");
		routeNext.add("f");
		routeNext.add("g");
		routeNext.add("h");

		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;
	}

	public static MsgSubsForm generateNode16() {

		String nodeName = "16";
		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		//topicChildList.put(nodeName, generateNodeSelf("16"));
		topicChildList.put("21", generateNode21());
		topicChildList.put("22", generateNode22());
		subs.add("a");
		routeNext.add("b");
		routeNext.add("c");
		routeNext.add("d");
		subs.add("e");
		routeNext.add("f");
		routeNext.add("g");
		routeNext.add("h");

		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;
	}

	public static MsgSubsForm generateNode15() {

		String nodeName = "15";
		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		//topicChildList.put(nodeName, generateNodeSelf("15"));
		topicChildList.put("20", generateNode20());

		subs.add("a");
		routeNext.add("b");
		routeNext.add("c");
		routeNext.add("d");
		subs.add("e");
		routeNext.add("f");
		routeNext.add("g");
		routeNext.add("h");

		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;
	}

	public static MsgSubsForm generateNode14() {

		String nodeName = "14";
		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		//topicChildList.put(nodeName, generateNodeSelf("14"));
		topicChildList.put("19", generateNode19());

		subs.add("a");
		routeNext.add("b");
		routeNext.add("c");
		routeNext.add("d");
		subs.add("e");
		routeNext.add("f");
		routeNext.add("g");
		routeNext.add("h");

		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;
	}

	public static MsgSubsForm generateNode13() {

		String nodeName = "13";
		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		//topicChildList.put(nodeName, generateNodeSelf("13"));
		topicChildList.put("18", generateNode18());

		subs.add("a");
		routeNext.add("b");
		routeNext.add("c");
		routeNext.add("d");
		subs.add("e");
		routeNext.add("f");
		routeNext.add("g");
		routeNext.add("h");

		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;
	}

	public static MsgSubsForm generateNode12() {

		String nodeName = "12";
		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		//topicChildList.put(nodeName, generateNodeSelf("12"));
		topicChildList.put("17", generateNode17());

		subs.add("a");
		routeNext.add("b");
		routeNext.add("c");
		routeNext.add("d");
		subs.add("e");
		routeNext.add("f");
		routeNext.add("g");
		routeNext.add("h");

		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;
	}

	public static MsgSubsForm generateNode11() {

		String nodeName = "11";
		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		//topicChildList.put(nodeName, generateNodeSelf("11"));
		topicChildList.put("16", generateNode16());

		subs.add("a");
		routeNext.add("b");
		routeNext.add("c");
		routeNext.add("d");
		subs.add("e");
		routeNext.add("f");
		routeNext.add("g");
		routeNext.add("h");

		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;
	}

	public static MsgSubsForm generateNode10() {

		String nodeName = "10";
		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		//topicChildList.put(nodeName, generateNodeSelf("10"));
		topicChildList.put("14", generateNode14());
		topicChildList.put("15", generateNode15());

		subs.add("a");
		routeNext.add("b");
		routeNext.add("c");
		routeNext.add("d");
		subs.add("e");
		routeNext.add("f");
		routeNext.add("g");
		routeNext.add("h");

		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;
	}

	public static MsgSubsForm generateNode9() {

		String nodeName = "9";
		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		//topicChildList.put(nodeName, generateNodeSelf("9"));
		topicChildList.put("13", generateNode13());

		subs.add("a");
		routeNext.add("b");
		routeNext.add("c");
		routeNext.add("d");
		subs.add("e");
		routeNext.add("f");
		routeNext.add("g");
		routeNext.add("h");

		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;
	}

	public static MsgSubsForm generateNode8() {

		String nodeName = "8";
		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		//topicChildList.put(nodeName, generateNodeSelf("8"));
		topicChildList.put("12", generateNode12());

		subs.add("a");
		routeNext.add("b");
		routeNext.add("c");
		routeNext.add("d");
		subs.add("e");
		routeNext.add("f");
		routeNext.add("g");
		routeNext.add("h");

		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;
	}

	public static MsgSubsForm generateNode7() {

		String nodeName = "7";
		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		//topicChildList.put(nodeName, generateNodeSelf("7"));
		topicChildList.put("11", generateNode11());

		subs.add("a");
		routeNext.add("b");
		routeNext.add("c");
		routeNext.add("d");
		subs.add("e");
		routeNext.add("f");
		routeNext.add("g");
		routeNext.add("h");

		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;
	}

	public static MsgSubsForm generateNode6() {

		String nodeName = "6";
		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		//topicChildList.put(nodeName, generateNodeSelf("6"));
		topicChildList.put("10", generateNode10());

		subs.add("a");
		routeNext.add("b");
		routeNext.add("c");
		routeNext.add("d");
		subs.add("e");
		routeNext.add("f");
		routeNext.add("g");
		routeNext.add("h");

		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;
	}

	public static MsgSubsForm generateNode5() {

		String nodeName = "5";
		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		//topicChildList.put(nodeName, generateNodeSelf("5"));
		topicChildList.put("8", generateNode8());
		topicChildList.put("9", generateNode9());

		subs.add("a");
		routeNext.add("b");
		routeNext.add("c");
		routeNext.add("d");
		subs.add("e");
		routeNext.add("f");
		routeNext.add("g");
		routeNext.add("h");

		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;
	}

	public static MsgSubsForm generateNode4() {

		String nodeName = "4";
		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		//topicChildList.put(nodeName, generateNodeSelf("4"));
		topicChildList.put("7", generateNode7());
		routeNext.add("d");

		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;
	}

	public static MsgSubsForm generateNode3() {

		String nodeName = "3";
		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		//topicChildList.put(nodeName, generateNodeSelf("3"));
		topicChildList.put("6", generateNode6());


		routeNext.add("c");


		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;
	}

	public static MsgSubsForm generateNode2() {

		String nodeName = "2";
		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		//topicChildList.put(nodeName, generateNodeSelf("2"));
		topicChildList.put("5", generateNode5());

		routeNext.add("b");

		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;
	}

	public static MsgSubsForm generateNode1() {

		String nodeName = "500";
		ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> routeNext = new ArrayList<String>();

		//topicChildList.put(nodeName, generateNodeSelf("1"));
		topicChildList.put("2", generateNode2());
		topicChildList.put("3", generateNode3());
		topicChildList.put("4", generateNode4());


		routeNext.add("a");

		MsgSubsForm n = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return n;
	}

	public MsgSubsForm iniRouteNode(String nodeName, ConcurrentHashMap<String, MsgSubsForm> topicChildList, ArrayList<String> subs, ArrayList<String> routeNext) {
		MsgSubsForm routeNode = new MsgSubsForm(nodeName, topicChildList, subs, routeNext);
		return routeNode;
	}


}
