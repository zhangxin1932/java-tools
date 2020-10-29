package com.zy.commons.starter;

import com.zy.commons.did.business.service.impl.DefaultNumberSegmentAllocate;
import com.zy.commons.did.core.DidGenerator;
import com.zy.commons.did.core.impl.DefaultDidGenerator;
import com.zy.commons.did.business.service.impl.DefaultSnowflakeWorkerIdAllocate;
import com.zy.commons.did.core.impl.NumberSegmentDidGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(DidGenerator.class)
@ConditionalOnResource(resources = {"config.properties"})
public class DidGeneratorAutoConfiguration {

    /**
     * 只有指定 did 的生成模式, 才会加载这个 bean --> DefaultDidGenerator, 进而将其装入 Spring 容器
     * @return
     */
    @ConditionalOnExpression("'${did.pattern}'.equals('1')")
    @Bean
    public DidGenerator didGenerator() {
        DefaultDidGenerator defaultDidGenerator = new DefaultDidGenerator(new DefaultSnowflakeWorkerIdAllocate());
        // 时间戳位，如果不配置，代码中默认是31位
        defaultDidGenerator.setTimeBits(31);
        // 机器位，如果不配置，代码中默认是20位
        defaultDidGenerator.setWorkerBits(20);
        // 序列位，如果不配置，代码中默认是12位
        defaultDidGenerator.setSequenceBits(12);
        // 机房标识，合法值：0~15，如果不配置(即-1), 默认不启用机房位, 此时: dataCenterIdBits = 0
        defaultDidGenerator.setDataCenterId(-1);
        // 数据库类型, 如果不配置, 默认是 mysql
        defaultDidGenerator.setDbType("mysql");
        // 机器码表名，用于每次应用程序启动时, 获取 mysql 生成的自增 id 作为 workerId 的表
        defaultDidGenerator.setTableName("worker_node");
        // did补零标识，如果不配置代码中默认左补零, true 表示左位补 0 至 19 位, false 表示不补 0, 仅限返回 String 类型的方法
        defaultDidGenerator.setLeftPaddingWithZero(true);
        // 时间戳计算基准时间，如果不配置代码中默认是 "2020-02-13 12:51:25"
        defaultDidGenerator.setEpochStr("2020-02-13 12:51:25");
        return defaultDidGenerator;
    }

    /**
     * 只有指定 did 的生成模式, 才会加载这个 bean --> DefaultDidGenerator, 进而将其装入 Spring 容器
     * @return
     */
    @ConditionalOnExpression("'${did.pattern}'.equals('2')")
    @Bean
    public DidGenerator numberSegmentDidGenerator() {
        NumberSegmentDidGenerator didGenerator = new NumberSegmentDidGenerator(new DefaultNumberSegmentAllocate());
        didGenerator.setDbType("mysql");
        didGenerator.setTableName("number_segment");
        didGenerator.setNeedRetry(true);
        didGenerator.setSegmentRetryMillis(5000L);
        didGenerator.setQuantityPerSegment(1000);
        didGenerator.setSegmentMaxValue(999_999_999_999L);
        return didGenerator;
    }

}
