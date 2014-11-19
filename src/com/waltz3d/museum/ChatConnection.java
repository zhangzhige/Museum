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

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ChatConnection {

	XL_Log log = new XL_Log(ChatConnection.class);
	
    private Handler mUpdateHandler;
    
    private ChatClient mChatClient;

    private Socket mSocket;
    private int mPort = -1;

    public ChatConnection(Handler handler) {
        mUpdateHandler = handler;
    }

    public void tearDown() {
        mChatClient.tearDown();
    }

    public void connectToServer(InetAddress address, int port) {
        mChatClient = new ChatClient(address, port);
    }

    public void sendMessage(String msg) {
    	log.debug("mChatClient"+mChatClient+",msg="+msg);
        if (mChatClient != null) {
            mChatClient.sendMessage(msg);
        }
    }
    
    public int getLocalPort() {
        return mPort;
    }
    
    public void setLocalPort(int port) {
        mPort = port;
    }
    

    public synchronized void updateMessages(String msg, boolean local) {
    	log.debug("Updating message: " + msg);

        if (local) {
            msg = "me: " + msg;
        } else {
            msg = "them: " + msg;
        }

        Bundle messageBundle = new Bundle();
        messageBundle.putString("msg", msg);

        Message message = new Message();
        message.setData(messageBundle);
        mUpdateHandler.sendMessage(message);

    }

    private synchronized void setSocket(Socket socket) {
    	log.debug("setSocket being called. socket="+socket);
        if (socket == null) {
        	log.debug("Setting a null socket.");
        }
        if (mSocket != null) {
            if (mSocket.isConnected()) {
                try {
                    mSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        mSocket = socket;
    }

    private Socket getSocket() {
        return mSocket;
    }

    private class ChatClient {

        private InetAddress mAddress;
        
        private int PORT;

        private Thread mSendThread;
        
        private Thread mRecThread;

        public ChatClient(InetAddress address, int port) {
            log.debug("Creating chatClient address=" +address+",port="+port);
            this.mAddress = address;
            this.PORT = port;

            mSendThread = new Thread(new SendingThread());
            mSendThread.start();
        }

        class SendingThread implements Runnable {

            BlockingQueue<String> mMessageQueue;
            private int QUEUE_CAPACITY = 10;

            public SendingThread() {
                mMessageQueue = new ArrayBlockingQueue<String>(QUEUE_CAPACITY);
            }

            @Override
            public void run() {
                try {
                    if (getSocket() == null) {
                        setSocket(new Socket(mAddress, PORT));
                        log.debug("Client-side socket initialized.");

                    } else {
                    	log.debug("Socket already initialized. skipping!");
                    }

                    mRecThread = new Thread(new ReceivingThread());
                    mRecThread.start();

                } catch (UnknownHostException e) {
                	e.printStackTrace();
                	log.debug("UnknownHostException ="+e);
                } catch (IOException e) {
                	e.printStackTrace();
                	log.debug("IOException="+e);
                }

                while (true) {
                    try {
                        String msg = mMessageQueue.take();
                        sendMessage(msg);
                    } catch (InterruptedException ie) {
                    	log.debug("InterruptedException="+ie);
                    }
                }
            }
        }

        class ReceivingThread implements Runnable {

            @Override
            public void run() {
                BufferedReader input;
                try {
                    input = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                    while (!Thread.currentThread().isInterrupted()) {
                        String messageStr = null;
                        messageStr = input.readLine();
                        if (messageStr != null) {
                        	log.debug("Read from the stream: " + messageStr);
                            updateMessages(messageStr, false);
                        } else {
                        	log.debug("receive The nulls! The nulls!");
                            break;
                        }
                    }
                    input.close();

                } catch (IOException e) {
                    e.printStackTrace();
                    log.debug("IOException="+e);
                }
            }
        }

        public void tearDown() {
            try {
                getSocket().close();
            } catch (IOException ioe) {
            	ioe.printStackTrace();
                log.debug("IOException="+ioe);
            }
        }

        public void sendMessage(String msg) {
            try {
                Socket socket = getSocket();
                log.debug("socket = "+socket);
                if (socket == null) {
                	log.debug("Socket is null, wtf?");
                } else if (socket.getOutputStream() == null) {
                	log.debug("Socket output stream is null, wtf?");
                }

                PrintWriter out = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(getSocket().getOutputStream())), true);
                out.println(msg);
                out.flush();
                updateMessages(msg, true);
            } catch (UnknownHostException e) {
            	e.printStackTrace();
            	log.debug(e.getMessage());
            	log.debug("Unknown Host"+e);
            } catch (IOException e) {
            	e.printStackTrace();
            	log.debug(e.getMessage());
            	log.debug("IOException"+e);
            } catch (Exception e) {
            	e.printStackTrace();
            	log.debug(e.getMessage());
            }
            log.debug("Client sent message: " + msg);
        }
    }
}
