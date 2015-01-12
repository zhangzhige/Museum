/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.waltz3d.museum;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import android.os.Handler;

public class ChatConnection {

	XL_Log log = new XL_Log(ChatConnection.class);

	private int mPort = -1;

	private InetAddress mAddress;

	public ChatConnection(Handler handler) {
	}

	public void connectToServer(InetAddress address, int port) {
		this.mAddress = address;
		this.mPort = port;
	}

	public void sendMessage(final String msg) {
		new Thread(new Runnable() {

			@Override
			public void run() {

//				try {
//					Socket client = new Socket(mAddress, mPort);
//					Writer writer = new OutputStreamWriter(client.getOutputStream());
//					writer.write(msg);
//					writer.flush();
//					log.debug("Client sent message: " + mAddress.getHostAddress() + ",mPort=" + mPort + ",isConnect=" + client.isConnected());
//					writer.close();
//					client.close();
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}

				DatagramSocket ds = null;
				try {
					ds = new DatagramSocket();
					ds.connect(mAddress, mPort);
					DatagramPacket dp = new DatagramPacket(msg.getBytes(), msg.length(), mAddress, mPort);
					log.debug("Client sent message: " + mAddress.getHostAddress() + ",mPort=" + mPort + ",isConnect=" + ds.isConnected());
					ds.send(dp);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					ds.close();
				}

			}
		}).start();

	}
}
