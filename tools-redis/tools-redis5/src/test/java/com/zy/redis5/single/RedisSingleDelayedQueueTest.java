package com.zy.redis5.single;

import com.alibaba.fastjson.JSON;
import com.zy.redis5.dto.DelayedOrderDTO;
import com.zy.redis5.enums.OrderEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RBoundedBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class RedisSingleDelayedQueueTest {

    @Autowired
    private RDelayedQueue<DelayedOrderDTO> rDelayedQueue;

    @Autowired
    private RBoundedBlockingQueue<DelayedOrderDTO> rBoundedBlockingQueue;

    private static final String DELAY_ORDER_ = "delay_order_";

    @Test
    public void fn01() {
        for (int i = 0; i < 10; i++) {
            DelayedOrderDTO delayedOrderDTO = DelayedOrderDTO.builder()
                    .orderId(String.valueOf(i))
                    .orderName(DELAY_ORDER_ + i)
                    .orderStatus(OrderEnum.one.getStatus())
                    .payAmount(String.valueOf(i * 100))
                    .createTime(LocalDateTime.now())
                    .build();
            produceDelayedOrder(delayedOrderDTO);
        }
        consumerDelayedOrder();
    }

    private void produceDelayedOrder(DelayedOrderDTO delayedOrderDTO) {
        // 一分钟以后将消息发送到指定队列,  相当于1分钟后取消订单
        rDelayedQueue.offer(delayedOrderDTO, 60L, TimeUnit.SECONDS);
    }

    private void consumerDelayedOrder() {
        while (true) {
            try {
                DelayedOrderDTO delayedOrderDTO = rBoundedBlockingQueue.take();
                // 执行查库的操作, 如果该订单是仍未支付, 则进行下述修改订单状态的操作
                // ... 这里省去查库的代码 ...
                // 执行修改订单状态的操作, 这里需要考虑线程安全问题
                delayedOrderDTO.setOrderStatus(OrderEnum.three.getStatus());
                delayedOrderDTO.setUpdateTime(LocalDateTime.now());
                System.out.println(String.format("订单: %s >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 已取消.", JSON.toJSONString(delayedOrderDTO)));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
