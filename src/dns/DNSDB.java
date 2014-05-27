package dns;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import dns.bplustree.KeyValuePair;

public class DNSDB {

	private BPlusTreeIntToString60 hostNames;
	private BPlusTreeString60toInt ipAddresses;

	public DNSDB() {
		hostNames = new BPlusTreeIntToString60("hostnames.b+tree");
		ipAddresses = new BPlusTreeString60toInt("ips.b+tree");
	}

	/**
	 * Loads all the host-IP pairs into the B+ trees.
	 * 
	 * @param fileName
	 */
	public void load(File file) {
		if (!file.exists()) {
			System.out.println(file + " not found");
			return;
		}
		BufferedReader data;
		System.out.println("Loading....");
		try {
			data = new BufferedReader(new FileReader(file));
			while (true) {
				String line = data.readLine();
				if (line == null) {
					break;
				}
				String[] pair = line.split("\t");
				String host = pair[0];
				int IP = stringToIP(pair[1]);
				hostNames.put(IP, host);
				ipAddresses.put(host, IP);
			}
		} catch (IOException e) {
			System.out.println("Fail: " + e);
		}
		System.out.println("Loading Done");
	}
	
	public void flush() throws IOException{
	  hostNames.flush();
	  ipAddresses.flush();
	}

	/**
	 * Finds an IP address given the host name.
	 * 
	 * @param hostName
	 * @return integer representation of an IP address, null if not found.
	 */
	public Integer findIP(String hostName) {
		return ipAddresses.find(hostName);
	}

	/**
	 * Finds the host name given the IP address.
	 * 
	 * @param ip
	 * @return null if not found
	 */
	public String findHostName(int ip) {
		return hostNames.find(ip);
	}

	/**
	 * Tests whether the given IP-name pair is valid.
	 * 
	 * @param ip
	 *            integer representation of an IP address
	 * @param hostName
	 * @return true if valid, false otherwise
	 */
	public boolean testPair(int ip, String hostName) {
		String host = findHostName(ip);
		if (host == null) {
			Integer foundIP = findIP(hostName);
			if (foundIP == null)
				return false;
			return ip == foundIP;
		} else {
			return host.equals(hostName);
		}
	}

	/**
	 * Tests whether the given name-IP pair is valid.
	 * 
	 * @param hostName
	 * @param ip
	 *            integer representation of an IP address
	 * @return true if valid, false otherwise
	 */
	public boolean testPair(String hostName, int ip) {
		Integer foundIP = findIP(hostName);
		if (foundIP == null) {
			String foundName = findHostName(ip);
			if (foundName == null)
				return false;
			return hostName.equals(foundName);
		} else {
			return ip == foundIP;
		}
	}

	/**
	 * Adds an host-IP pair to the database (ie, to both B+ trees)
	 * 
	 * @param hostName
	 * @param ip
	 * @return whether successfully added to the database
	 */
	public boolean add(String hostName, int ip) {
		return ipAddresses.put(hostName, ip) && hostNames.put(ip, hostName);
	}

	/**
	 * Prints (to System.out) all the pairs in the HostNames index.
	 */
	public void iterateAll() {
		for(KeyValuePair<Integer, String> pair : hostNames.entryList()){
			System.out.println(IPToString(pair.getKey()) + " --> " + pair.getValue());
		}
	}

	// FOR MARKING! PLEASE DO NOT MODIFY.
	/**
	 * Look up all the values in the file. If the value is not present, then it
	 * will print that value to System.out.
	 */
	public void testAllPairs(File file) {
		try {
			int missingCount = 0;
			Scanner scan = new Scanner(file);
			while (scan.hasNextLine()) {
				String[] line = scan.nextLine().split("\t");
				if (line.length != 2)
					continue;
				int ip = stringToIP(line[1].trim());
				String host = line[0].trim();
				if (!testPair(ip, host)) {
					System.out.println("Missing: " + IPToString(ip) + " -> "
							+ host);
					missingCount ++;
				}
				if (!testPair(host, ip)) {
					System.out.println("Missing: " + host + " -> "
							+ IPToString(ip));
					missingCount ++;
				}
			}
			System.out.println("Missing " + missingCount + "entries");
			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	// Utilities

	public static Integer stringToIP(String text) {
		String[] bytes = text.trim().split("\\.");

		if (bytes.length != 4)
			return null;

		try {
			int ip = 0;
			for (int i = 0; i < 4; i++) {
				int b = Integer.parseInt(bytes[i].trim());
				ip |= b << (24 - 8 * i);
			}

			return ip;
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static String IPToString(int ip) {
		StringBuilder sb = new StringBuilder();
		for (int i = 3; i >= 0; i--) {
			sb.append((ip & (0xFF << (i * 8))) >> (i * 8));
			if (i > 0)
				sb.append('.');
		}
		return sb.toString();
	}
}
