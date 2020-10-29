package com.zy.tools.zk;

public class ZkTest {

    public static void main(String[] args) {
        URL url = new URL(null, null, null, "192.168.0.104", 2181, null, null);
        CuratorZkClient client = new CuratorZkClient(url);
        client.createPersistent("/test01", "hello-world");
    }
}
