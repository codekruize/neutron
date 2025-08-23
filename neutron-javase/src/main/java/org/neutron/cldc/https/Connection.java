package org.neutron.cldc.https;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.microedition.io.HttpsConnection;
import javax.microedition.io.SecurityInfo;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import org.neutron.cldc.CertificateImpl;
import org.neutron.cldc.SecurityInfoImpl;
import org.neutron.log.Logger;

public class Connection extends org.neutron.cldc.http.Connection implements HttpsConnection {

	private SSLContext sslContext;

	private SecurityInfo securityInfo;

	public Connection() {
	    try {
			sslContext = SSLContext.getInstance("TLS");
		} catch (NoSuchAlgorithmException ex) {
			Logger.error(ex);
		}

		securityInfo = null;
	}

	public SecurityInfo getSecurityInfo() throws IOException {
		if (securityInfo == null) {
		    if (cn == null) {
				throw new IOException();
			}
			if (!connected) {
				cn.connect();
				connected = true;
			}
			HttpsURLConnection https = (HttpsURLConnection) cn;

			Certificate[] certs = https.getServerCertificates();
			if (certs.length == 0) {
				throw new IOException();
			}
			securityInfo = new SecurityInfoImpl(
					https.getCipherSuite(),
					sslContext.getProtocol(),
					new CertificateImpl((X509Certificate) certs[0]));
		}

		return securityInfo;
	}

	public String getProtocol() {
		return "https";
	}
public int getPort() {
		if (cn == null) {
			return -1;
		}
		int port = cn.getURL().getPort();
		if (port == -1) {
			return 443;
		}
		return port;
	}

}
