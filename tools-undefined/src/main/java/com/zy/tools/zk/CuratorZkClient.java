package com.zy.tools.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.zy.tools.zk.CommonConstants.TIMEOUT_KEY;
@Slf4j
public class CuratorZkClient {

    private final Set<StateListener> stateListeners = new CopyOnWriteArraySet<StateListener>();
    private Map<String, TreeCache> treeCacheMap = new ConcurrentHashMap<>();
    private CuratorFramework client;

    public CuratorZkClient(URL url) {
        try {
            int timeout = url.getParameter(TIMEOUT_KEY, 5000);
            CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
                    .connectString(url.getHostAndPort())
                    .retryPolicy(new RetryNTimes(1, 1000))
                    .connectionTimeoutMs(timeout);

            String authority = url.getAuthority();
            if (StringUtils.isNotBlank(authority)) {
                builder = builder.authorization("digest", authority.getBytes());
            }

            client = builder.build();

            client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
                @Override
                public void stateChanged(CuratorFramework client, ConnectionState state) {
                    if (state == ConnectionState.LOST) {
                        CuratorZkClient.this.stateChanged(StateListener.DISCONNECTED);
                    } else if (state == ConnectionState.CONNECTED) {
                        CuratorZkClient.this.stateChanged(StateListener.CONNECTED);
                    } else if (state == ConnectionState.RECONNECTED) {
                        CuratorZkClient.this.stateChanged(StateListener.RECONNECTED);
                    }
                }
            });

            client.start();
        } catch (Exception e) {
            log.error("failed to start zk.", e);
        }
    }

    public Set<StateListener> getSessionListeners() {
        return stateListeners;
    }

    protected void stateChanged(int state) {
        for (StateListener sessionListener : getSessionListeners()) {
            sessionListener.stateChanged(state);
        }
    }

    /**
     * 创建持久性节点
     * @param path
     */
    public void createPersistent(String path) {
        if (checkExists(path)) {
            return;
        }
        try {
            client.create().forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建持久性节点
     * @param path
     * @param data
     */
    public void createPersistent(String path, String data) {
        if (checkExists(path)) {
            return;
        }
        try {
            byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
            client.create().forPath(path, bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建临时性节点
     * @param path
     */
    public void createEphemeral(String path) {
        if (checkExists(path)) {
            return;
        }
        try {
            client.create().withMode(CreateMode.EPHEMERAL).forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建临时性节点
     * @param path
     * @param data
     */
    public void createEphemeral(String path, String data) {
        if (checkExists(path)) {
            return;
        }
        try {
            byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
            client.create().withMode(CreateMode.EPHEMERAL).forPath(path, bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查节点是否存在
     * @param path
     * @return
     */
    public boolean checkExists(String path) {
        try {
            if (Objects.nonNull(client.checkExists().forPath(path))) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断某节点是否是 持久性节点
     * @param path
     * @return
     */
    public boolean isPersistentNode(String path) {
        try {
            Stat stat = client.checkExists().forPath(path);
            if (Objects.isNull(stat)) {
                return false;
            }
            if (stat.getEphemeralOwner() == 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除某个节点
     * @param path
     */
    public void delete(String path) {
        try {
            if (checkExists(path)) {
                client.delete().forPath(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取某节点下的所有子节点
     * @param path
     * @return
     */
    public List<String> getChildren(String path) {
        try {
            if (checkExists(path)) {
                return client.getChildren().forPath(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断是否连接
     * @return
     */
    public boolean isConnected() {
        return client.getZookeeperClient().isConnected();
    }

    /**
     * 获取某节点对应的 value
     * @param path
     * @return
     */
    public String getContent(String path) {
        try {
            if (checkExists(path)) {
                byte[] bytes = client.getData().forPath(path);
                return (Objects.isNull(bytes) || bytes.length == 0) ? null : new String(bytes, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 关闭连接
     */
    public void close() {
        client.close();
    }

}
