package com.common.jms;

import com.common.config.Config;
import com.common.config.ConfigLoader;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

import javax.jms.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

public class JmsPool {
    private static final Logger logger = Logger.getLogger(JmsPool.class.getName());
    /** the sole JMSConnection instance */
    static private JmsPool instance; // 唯一实例

    /** default pool name */
    static private String name = ConfigLoader.getInstance()
            .getProps(Config.system_config).getProperty(Config.jms_pool_name);

    /** set a hashtable to save all the pools */
    private Hashtable pools = new Hashtable();

    /** set logger */

    /** default database connection wait timeout */
    private int timeout = Integer.parseInt(ConfigLoader.getInstance()
            .getProps(Config.system_config).getProperty(Config.jms_pool_timeout));

    private String user = ActiveMQConnection.DEFAULT_USER;;
    private String password = ActiveMQConnection.DEFAULT_PASSWORD;
    private int maxPoolSize = Integer.parseInt(ConfigLoader.getInstance()
            .getProps(Config.system_config).getProperty(Config.jms_pool_maxPoolSize));
    private int minPoolSize = Integer.parseInt(ConfigLoader.getInstance()
            .getProps(Config.system_config).getProperty(Config.jms_pool_minPoolSize));
    private String url = ConfigLoader.getInstance().getProps(Config.system_config).getProperty(Config.jms_pool_url);

    /**
     * initalize jms connection pool
     */
    public synchronized void init() throws Exception {
        initialPool();
    }

    /**
     * initial jms connection pool
     */
    private void initialPool() throws Exception {
        QueueConnectionPool pool = (QueueConnectionPool) pools.get(name);
        if (pool == null) {
            this.createPools(name);
        }
    }

    /**
     * 建立数据库连接池
     *
     * @param poolName
     * 连接池名
     */
    private synchronized void createPools(String poolName) throws Exception {
        if (poolName == null || poolName.trim().length() == 0) {
            throw new IllegalArgumentException(
                    "pool name must not be empty or null!");
        }
        poolName = poolName.trim();
        if (pools.get(poolName) != null) {
            return;
        }

        QueueConnectionPool pool = new QueueConnectionPool(poolName, maxPoolSize,
                timeout, url, user, password);

        for (int i = 0; i < minPoolSize; i++) {
            QueueConnection conn = pool.newConnection();
            pool.freeConnections.add(conn);
        }

        pools.put(poolName, pool);
    }

    /**
     * 返回唯一实例.如果是第一次调用此方法,则创建实例
     *
     * @return DBSource 唯一实例
     */
    static synchronized public JmsPool getInstance() {
        if (instance == null) {
            try {
                instance = new JmsPool();
            } catch (Exception e) {

                logger.error("初始化消息队列出现严重问题", e);
            }
        }
        return instance;
    }

    /**
     * 建构函数私有以防止其它对象创建本类实例
     */
    private JmsPool() throws Exception {
        initialize();
    }

    private void initialize() throws Exception {

        this.init();
    }

    /**
     * 将连接对象返回给由名字指定的连接池
     *
     * @param con
     *            连接对象
     */
    public void freeConnection(QueueConnection con) {
        freeConnection(name, con);
    }

    /**
     * 将连接对象返回给由名字指定的连接池
     *
     * @param name
     *            在属性文件中定义的连接池名字
     * @param con
     *            连接对象
     */
    public void freeConnection(String name, QueueConnection con) {
        QueueConnectionPool pool = (QueueConnectionPool) pools.get(name);
        if (pool != null) {
            pool.freeConnection(con);
        }
    }

    /**
     * 获得可用的(空闲的)连接.如果没有可用连接,且已有连接数小于最大连接数 限制,则创建并返回新连接
     *
     * @return Connection 可用连接或null
     */
    public QueueConnection getConnection() throws Exception {
        return this.getConnection(name, 0);
    }

    /**
     * 获得一个可用的(空闲的)连接.如果没有可用连接,且已有连接数小于最大连接数 限制,则创建并返回新连接
     *
     * @param name
     *            连接池名字
     * @return Connection 可用连接或null
     */
    public QueueConnection getConnection(String name) throws Exception {
        return this.getConnection(name, 0);
    }

    /**
     * 获得一个可用连接.若没有可用连接,且已有连接数小于最大连接数限制, 则创建并返回新连接.否则,在指定的时间内等待其它线程释放连接.
     *
     * @param name
     *            连接池名字
     * @param timeout
     *            以毫秒计的等待时间
     * @return Connection 可用连接或null
     */
    public QueueConnection getConnection(String name, long timeout)
            throws Exception {
        QueueConnectionPool pool = null;

        pool = (QueueConnectionPool) pools.get(name);

        if (pool == null)
            throw new Exception("can not create a new pool for the name:"
                    + name);

        if (timeout == 0) {
            return pool.getConnection();
        } else {
            return pool.getConnection(timeout);
        }
    }

    /**
     * 释放所有的连接池资源
     */
    public synchronized void release() {

        Enumeration allPools = pools.elements();
        while (allPools.hasMoreElements()) {
            QueueConnectionPool pool = (QueueConnectionPool) allPools
                    .nextElement();
            pool.release();
        }

    }

    /**
     * 此内部类定义了一个连接池.它能够根据要求创建新连接,直到预定的最 大连接数为止.在返回连接给客户程序之前,它能够验证连接的有效性.
     */
    class QueueConnectionPool {
        /**
         * 已取出的连接数
         */
        private int checkedOut = 0;

        /** save database connection object */
        private Vector freeConnections = new Vector();
        /** max pool size */
        private int maxConn;
        /** db pool name */
        private String name;
        /** connection wait timeout */
        private long timeout = 0;

        private String url;
        //		private int jmsPort;
        private String user;
        private String password;

        /** the time when starting to get a connection */
        private long startTime = 0;

        private long endTime = 0;

        private int totalNum = 0;

        /**
         * 创建新的连接池
         *
         * @param name
         *            连接池名字
         * @param url
         *            数据库的JDBC URL
         * @param user
         *            数据库帐号,或 null
         * @param password
         *            密码,或 null
         * @param maxConn
         *            此连接池允许建立的最大连接数
         */
        public QueueConnectionPool(String name, int maxConn, long timeout,
                                   String url,String user, String password) {
            this.name = name;
            this.maxConn = maxConn;
            this.timeout = timeout;

            this.url = url;
            this.user = user;
            this.password = password;
        }

        /**
         * 将不再使用的连接返回给连接池
         *
         * @param con
         *            客户程序释放的连接
         */
        public synchronized void freeConnection(QueueConnection con) {
            checkedOut--;
            if (checkedOut < 0) {

                checkedOut = 0;
            }
            if (con == null) {

            } else {
                // 将指定连接加入到向量末尾
                if (freeConnections.size() < this.maxConn) {
                    freeConnections.addElement(con);
                } else {

                }
            }
            notifyAll();
        }

        /**
         * 从连接池获得一个可用连接.如没有空闲的连接且当前连接数小于最大连接 数限制,则创建新连接.如原来登记为可用的连接不再有效,则从向量删除之,
         * 然后递归调用自己以尝试新的可用连接.
         *
         * @return Connection
         */
        public synchronized QueueConnection getConnection() throws Exception {
            return this.getConnection(timeout);
        }

        /**
         * 从连接池获得一个可用连接.如没有空闲的连接且当前连接数小于最大连接 数限制,则创建新连接.如原来登记为可用的连接不再有效,则从向量删除之,
         * 然后递归调用自己以尝试新的可用连接.
         *
         * @return Connection
         */
        public synchronized QueueConnection getConnection(long timeout)
                throws Exception {
            QueueConnection con = null;
            if (freeConnections.size() > 0) {
                // 获取向量中第一个可用连接
                con = (QueueConnection) freeConnections.firstElement();
                freeConnections.removeElementAt(0);
                while (!this.validateConn(con))
                    return getConnection(timeout);
            } else if (maxConn == 0 || checkedOut < maxConn) {
                con = newConnection();
            } else {

                if (timeout == -1) {
                    try {
                        wait();
                    } catch (Exception e) {
                        logger.error("初始化消息队列出现严重问题", e);
                    }
                    return getConnection(timeout);
                } else {
                    try {
                        wait(timeout);
                    } catch (Exception e) {
                        ;
                    }
                    this.endTime = System.currentTimeMillis();
                    long waitTime = this.endTime - this.startTime;
                    if (waitTime < timeout) {
                        return getConnection(timeout);
                    } else {

                        con = getConnectionNoWait();
                    }
                }
            }
            if (con != null) {
                checkedOut++;
            } else {
                throw new Exception(name + " pool中没有空余的数据库连接!");
            }
            return con;
        }

        /**
         * 如有可用连接，则返回，如没有，则返回空值
         *
         * @return
         * @throws
         */
        private synchronized QueueConnection getConnectionNoWait()
                throws Exception {
            QueueConnection con = null;
            if (freeConnections.size() > 0) {
                // 获取向量中第一个可用连接
                con = (QueueConnection) freeConnections.firstElement();
                freeConnections.removeElementAt(0);
                while (!this.validateConn(con))
                    getConnectionNoWait();
            } else if (maxConn == 0 || checkedOut < maxConn) {
                con = newConnection();
            }

            return con;
        }

        /**
         * 验证接连是否有效
         *
         * @param con
         * @return
         * @throws Exception
         */
        private boolean validateConn(QueueConnection con) throws Exception {
            javax.jms.Session ss = null;
            try {
                ss = con.createSession(false,
                        javax.jms.Session.AUTO_ACKNOWLEDGE);

                ss.close();
            } catch (Exception e) {
                logger.error("初始化消息队列出现严重问题", e);
                return false;
            } finally {
                if (ss != null)
                    ss.close();
            }
            return true;
        }

        /**
         * 关闭所有连接
         */
        public synchronized void release() {
            Enumeration allConnections = freeConnections.elements();
            while (allConnections.hasMoreElements()) {
                QueueConnection con = (QueueConnection) allConnections
                        .nextElement();
                try {
                    con.close();
                } catch (Exception e) {
                    logger.error("初始化消息队列出现严重问题", e);
                }
            }
            freeConnections.removeAllElements();
        }

        /**
         * 创建新的连接
         *
         * @return Connection
         */
        private QueueConnection newConnection() {
            QueueConnection con = null;

            try {
//				String url = ActiveMQConnection.DEFAULT_BROKER_URL;
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                        user, password, url);
                con = connectionFactory.createQueueConnection();
                con.start();

            } catch (Exception e) {
                logger.error("初始化消息队列出现严重问题", e);

            }
            ++totalNum;
            return con;
        }
    }

    public static void main(String[] args) {
        try {

            Iterator it = System.getProperties().keySet().iterator();
            while (it.hasNext()) {
                String h = (String) it.next();
                if (h.startsWith("javapt.")) {
                    System.out.println(h);
                    System.out.println(System.getProperty(h));
                }
            }
            System.out.println("start.........");

            QueueConnection c = JmsPool.getInstance().getConnection();
            Session session = c.createSession(false, Session.AUTO_ACKNOWLEDGE);
            String subject = "TOOL.TEST";
            Destination destination = session.createQueue(subject);
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            TextMessage message = session.createTextMessage("1233455");
            // message.setStringProperty("1", "1");
            producer.send(message);
            JmsPool.getInstance().freeConnection(c);
            JmsPool.getInstance().release();

            it = System.getProperties().keySet().iterator();
            while (it.hasNext()) {
                String h = (String) it.next();
                if (h.startsWith("javapt.")) {
                    System.out.println(h);
                    System.out.println(System.getProperty(h));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void sendLoop(Session session, MessageProducer producer)
            throws Exception {
        //
        //
        // TextMessage message = session.createTextMessage("fdsfdsfs");
        //
        //
        //
        // producer.send(message);
        // if (transacted) {
        // session.commit();
        // }
        //
        // Thread.sleep(sleepTime);

    }
}
