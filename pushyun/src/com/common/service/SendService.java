package com.common.service;

import java.util.HashMap;

/**
 *
 * <br/>发送信息接口<br/>
 * @author
 * @see
 */
public interface SendService {
	/**
	 * 发送到消息队列
	 * @param queueName 队列名称
	 * @param obj 内容
	 * @return 返回整数，Constants.STATE_OPERATOR_SUCC表示成功 Constants.STATE_OPERATOR_LOST表示失败
	 */
	public int sendQueue(String queueName,Object obj);

}
