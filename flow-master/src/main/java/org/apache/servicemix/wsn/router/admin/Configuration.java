package org.apache.servicemix.wsn.router.admin;

import java.io.*;

public class Configuration {
	private AdminMgr Amgr;

	public Configuration(AdminMgr Amgr) {
		this.Amgr = Amgr;
	}

	public void configure() {
		// TODO Auto-generated method stub

		File file = new File("./Aconfigure.txt");

		BufferedReader reader = null;

		try {
			System.out.println(System.getProperty("user.dir"));
			reader = new BufferedReader(new FileReader(file));

			String l;
			String[] s;

			while ((l = reader.readLine()) != null) {

				s = l.split(":");

				s[0] = s[0].trim();

				if (s[0].equals("backupAdministrator")) {
					Amgr.backup = s[1].trim();
				} else if (s[0].equals("localAddr")) {
					Amgr.localAddr = s[1].trim();
				} else if (s[0].equals("uPort")) {
					Amgr.uPort = Integer.parseInt(s[1].trim());
				} else if (s[0].equals("tPort")) {
					Amgr.tPort = Integer.parseInt(s[1].trim());
				} else if (s[0].equals("Port")) {
					Amgr.port2 = Integer.parseInt(s[1].trim());
				} else if (s[0].equals("ldapAddr")) {
					Amgr.ldapAddr = s[1].trim();
				}
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
