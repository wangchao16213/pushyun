package com.common.service.impl;

import com.common.comm.Constants;
import com.common.jms.JmsPool;
import com.common.service.SendService;
import org.apache.log4j.Logger;

import javax.jms.*;
import java.io.Serializable;
import java.util.HashMap;

public class SendServiceImpl implements SendService {

    private final Logger logger = Logger.getLogger(SendServiceImpl.class.getName());

    @Override
    public int sendQueue(String queueName,Object obj) {
        int state = Constants.STATE_OPERATOR_LOST;
        Session session=null;
        QueueConnection qc =null;
        Destination destination = null;
        MessageProducer producer = null;
        ObjectMessage objMessage = null;
        try {
            qc = JmsPool.getInstance().getConnection();
            session = qc.createSession(false, Session.AUTO_ACKNOWLEDGE);
            destination = session.createQueue(queueName);
            producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            objMessage = session.createObjectMessage();
            objMessage.setObject((Serializable)obj);
            producer.send(objMessage);
        } catch (Exception e) {
            logger.error("", e);
            return state;
        } finally {
            if (producer != null) {
                try {
                    producer.close();
                    producer = null;
                } catch (Exception e1) {
                }
            }
            if (session != null) {
                try {
                    session.close();
                    session = null;
                } catch (Exception e2) {
                }
            }
            if (qc != null) {
                try {
                    JmsPool.getInstance().freeConnection(qc);
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }
        return Constants.STATE_OPERATOR_SUCC;
    }
}
