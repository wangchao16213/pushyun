package com.common.jms;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.bean.BaseRecord;
import com.bean.BusinessChannel;
import com.bean.BusinessRule;
import com.bean.BusinessRuleDetail;
import com.bean.DataStatistics;
import com.bean.LogRuleStatistics;
import com.bean.LogSnifferStatistics;
import com.common.config.SpringConfig;
import com.common.tools.DateUtil;
import com.common.type.BusinessChannelOnlinestate;
import com.common.type.DataStatisticsState;
import com.common.type.LogRuleStatisticsState;
import com.common.type.LogSnifferStatisticsState;
import com.module.data.service.DataStatisticsService;
import com.spacesat.util.json.JSONArray;
import com.spacesat.util.json.JSONObject;

import javax.jms.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JmsListener implements Runnable, MessageListener,
        ExceptionListener {

    private final Logger logger = Logger.getLogger(JmsListener.class.getName());

    private Session session;

    private Destination destination;

    private boolean listening = false;

    private boolean transacted = false;

    private int ackMode = Session.AUTO_ACKNOWLEDGE;

    private long receiveTimeOut = 0;

    private long sleepTime = 100;

    private String queueName = "";

    public void run() {
        MessageConsumer consumer = null;
        Connection connection = null;
        try {
            listening = true;
            connection = JmsPool.getInstance().getConnection();
            connection.setExceptionListener(this);
            connection.start();
            session = connection.createSession(transacted, ackMode);
            destination = session.createQueue(queueName);
            consumer = session.createConsumer(destination);
            if (receiveTimeOut == 0) {
                consumer.setMessageListener(this);
            } else {
                consumeMessagesAndClose(connection, session, consumer,
                        receiveTimeOut);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isListening() {
        return listening;
    }

    public void setListening(boolean listening) {
        this.listening = listening;
    }
    /**
     * 由jms调用，
     * 根据消息里面的cmdtype类型进行处理
     */
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage txtMsg = (TextMessage) message;
                String msg = txtMsg.getText();
                try{
                	JSONObject dataObject=new JSONObject(msg);
                	if(!dataObject.has("data")){
                		return;
                	}
                	DataStatisticsService dataStatisticsService=(DataStatisticsService) SpringConfig.getInstance().getService(DataStatisticsService.class);
                	dataStatisticsService.saveDataStatistics(dataObject);
                }catch (Exception e) {
					e.printStackTrace();
				}
            } else if (message instanceof ObjectMessage) {
                ObjectMessage obMsg = (ObjectMessage) message;
                Object o = (Object) obMsg.getObject();
                if(o instanceof String){
                	try{
                		if(StringUtils.isBlank(String.valueOf(o))){
                			return;
                		}
                    	JSONObject dataObject=new JSONObject(String.valueOf(o));
                    	if(!dataObject.has("data")){
                    		return;
                    	}
                    	DataStatisticsService dataStatisticsService=(DataStatisticsService) SpringConfig.getInstance().getService(DataStatisticsService.class);
                    	dataStatisticsService.saveDataStatistics(dataObject);
                    }catch (Exception e) {
    					e.printStackTrace();
    				}
                }
            } else {
                logger.error("Unknown information stored in message queue!");
            }
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }

    }

    public void onException(JMSException e) {
        logger.error("", e);
        listening = false;
    }

    protected void consumeMessagesAndClose(Connection connection,
                                           Session session, MessageConsumer consumer, long timeout)
            throws JMSException, IOException {
        logger.info("We will consume messages while they continue to be delivered within: " + timeout + " ms, and then we will shutdown");
        Message message;
        while ((message = consumer.receive(timeout)) != null) {
            onMessage(message);
        }
        System.out.println("Closing connection");
        consumer.close();
        session.close();
        connection.close();
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }




}
