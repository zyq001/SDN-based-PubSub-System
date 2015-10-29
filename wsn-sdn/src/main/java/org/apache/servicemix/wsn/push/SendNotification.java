package org.apache.servicemix.wsn.push;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpPost;
import org.apache.servicemix.application.WSNTopicObject;
import org.apache.servicemix.application.WsnProcessImpl;
import org.apache.servicemix.wsn.jms.JmsSubscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendNotification implements RouterSend {
	public static List<ListItem> localtable;
	private static Log log = LogFactory.getLog(SendNotification.class);
	//������ʱ�洢���������NotificationBuilder����
	private static Map<String, NotificationBuilder> map = new HashMap<String, NotificationBuilder>();
	private boolean successfulFlag = true;

	public boolean getSuccessfulFlag() {
		return successfulFlag;
	}

	public void setSuccessfulFlag(boolean _successfulFlag) {
		successfulFlag = _successfulFlag;
		log.error("#####SendNotification: set the successfulFlag " + _successfulFlag);
	}

	/*
	 * ����������
	 */
	synchronized public void update(String message) throws Exception {
		System.out.println("update--------------------" + message);
		try {
			WsnProcessImpl.readTopicTree("ou=all_test,dc=wsn,dc=com");
			WsnProcessImpl.printTopicTree();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)new added by wp
	 * @see org.apache.servicemix.wsn.push.RouterSend#send(java.lang.String)
	 * ԭ����·��ģ��������Ϣ��ģ��һ��������Ϣ������Ϣ�Ӹ�NMR�����ڴ�·��ģ��������Ϣ
	 * Ҳֱ�ӵ���PushClient������Ϣֱ�ӵݽ���ws
	 */
	synchronized public void send(String message) throws Exception {
		System.out.println("-------------------------------");

		String notification = null;
		//������� added by shmily at 2013/04/08
		String fragment = message.split("<Fragment>")[1].split("</Fragment>")[0];
		System.out.println("[Fragment:]" + fragment);
		String[] splitFragment = fragment.split("-");

		StringBuilder s = new StringBuilder("[splitFragment:]");
		for (int i = 0; i < splitFragment.length; i++) {
			s.append(splitFragment[i]);
			s.append(" ");
		}

		//������Ϣδ������Ƭ���߸���Ϣ����ݽ��ϰ�����ֱ�ӵݽ�
		if (Integer.parseInt(splitFragment[0]) == 0 || Integer.parseInt(splitFragment[2]) == 1) {
			//ȥ�������Ϣ
			notification = message.split("<wsnt:Package>")[0] + message.split("</wsnt:Package>")[1];
		} else {
			//�����÷ְ���id��ȷ����������һ����Ϣ
			String hashCode = message.split("<Identification>")[1].split("</Identification>")[0];
			System.out.println("hashcode: " + hashCode);
			//�ж�������Ϣ�Ƿ������������Ļ��������������
			System.out.println("true or false? " + map.containsKey(hashCode));
			if (!map.containsKey(hashCode)) {
				NotificationBuilder nb = new NotificationBuilder();
				map.put(hashCode, nb);
			}
			//��ȡ�������
			NotificationBuilder tempNb = map.get(hashCode);
			//����Ϣ�ݽ����������
			tempNb.setTempMessage(message);
			//����ְ�
			tempNb.breakMessage();
			//������Ϣ
			tempNb.parse();
			//�ж��Ƿ��ȡ��һ����Ϣ�����зְ����������
			if (tempNb.isReadyToBuild()) {
				notification = tempNb.build();
				map.remove(hashCode);
			}
		}

		if (notification != null) {
			System.out.println("notification: " + notification);
			int start = notification.indexOf("TopicExpression/Simple\">") + 24;
			int end = notification.indexOf("</wsnt:Topic>");
			String topicName = notification.substring(start, end);
			//		if(topicName.contains("EventType=1")){
			//			String tempTopicName = topicName.substring(0, topicName.indexOf("EventType=1"));
			//			notification = notification.replaceAll(topicName, tempTopicName);
			//		}
			//		System.out.println("**************************our topicName  " + topicName);
			String subAddr = null;

			/**================================================================
			 * new added at 2013/12/1
			 */
			String[] topicPath = topicName.split(":");
			for (int m = 0; m < topicPath.length; m++) {
				System.out.print("Topic is: " + topicPath[m] + ":");
			}
			System.out.println();
			WSNTopicObject current = WsnProcessImpl.topicTree;
			int flag = 0;
			for (int i = 0; i < topicPath.length - 1; i++) {
				if (current.getTopicentry().getTopicName().equals(topicPath[i])) {
					for (int counter = 0; counter < current.getChildrens().size(); counter++) {
						if (current.getChildrens().get(counter).getTopicentry().getTopicName().equals(topicPath[i + 1])) {
							current = current.getChildrens().get(counter);
							flag++;
							System.out.println("match: " + current.getChildrens());
						}
					}
				} else {
					log.error("subscribe faild! there is not this topic in the topic tree!");
				}
			}
			if (flag == topicPath.length - 1) {
				System.out.println("ooooooooooooooooookkkkkkkkkkkkkkkkkkkkkkkkk!!!!!!!!!!!!!!!!!!!!");
				ArrayList<String> sendedaddr = new ArrayList<String>();
				while (current != null) {
					for (int i = 0; i < current.getSubscribeAddress().size(); i++) {
						if (sendedaddr.contains(current.getSubscribeAddress().get(i)))
							continue;
						sendedaddr.add(current.getSubscribeAddress().get(i));
						JmsSubscription.diliverToWebservice.doPush(current.getSubscribeAddress().get(i),
								notification,
								JmsSubscription.asyClient,
								new HttpPost(current.getSubscribeAddress().get(i)), this);
					}
					current = current.getParent();
				}
			} else {
				System.out.println("notify faild! there is not this topic in the topic tree!");
			}
			//=================================================================
		}
	}
}