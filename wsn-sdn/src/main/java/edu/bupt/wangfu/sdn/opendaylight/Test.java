package edu.bupt.wangfu.sdn.opendaylight;

import java.util.ArrayList;
import java.util.List;

import static edu.bupt.wangfu.sdn.opendaylight.IssueFlow.topicCode2multiV6Addr;
import static edu.bupt.wangfu.sdn.opendaylight.IssueFlow.topicName2topicCode;


/**
 * Created by lenovo on 2016-5-22.
 */
public class Test {
	public static void main(String[] args) {
		List<List<String>> topicList = new ArrayList<>();

		List<String> levelList_a = new ArrayList<>();
		levelList_a.add("all");

		List<String> levelList_b = new ArrayList<>();
		levelList_b.add("a");
		levelList_b.add("b");
		levelList_b.add("c");

		List<String> levelList_c = new ArrayList<>();
		levelList_c.add("d");
		levelList_c.add("e");
		levelList_c.add("f");

		List<String> levelList_d = new ArrayList<>();
		levelList_d.add("g");
		levelList_d.add("h");
		levelList_d.add("i");


		topicList.add(levelList_a);
		topicList.add(levelList_b);
		topicList.add(levelList_c);
		topicList.add(levelList_d);

		String res = topicName2topicCode("all:a:e:h", topicList);
		System.out.println(res);
		System.out.println(topicCode2multiV6Addr(res, 3));
	}
}
