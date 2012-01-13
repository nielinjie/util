package nielinjie.util.io;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class LocalAddress {
	public static InetAddress getFirstNonLoopbackAddress(boolean preferIpv4,
			boolean preferIPv6) throws SocketException {
		Enumeration en = NetworkInterface.getNetworkInterfaces();
		while (en.hasMoreElements()) {
			NetworkInterface i = (NetworkInterface) en.nextElement();
			for (Enumeration en2 = i.getInetAddresses(); en2.hasMoreElements();) {
				InetAddress addr = (InetAddress) en2.nextElement();
				if (!addr.isLoopbackAddress()) {
					if (addr instanceof Inet4Address) {
						if (preferIPv6) {
							continue;
						}
						return addr;
					}
					if (addr instanceof Inet6Address) {
						if (preferIpv4) {
							continue;
						}
						return addr;
					}
				}
			}
		}
		return null;
	}

	public static InetAddress getBoardcastAddress(InetAddress here) {
		byte[] hereb = here.getAddress();
		hereb[3] = (byte) 255;
		try {
			return InetAddress.getByAddress(hereb);
		} catch (UnknownHostException e) {
			//never go here.
			e.printStackTrace();
			return null;
		}
	}
}
