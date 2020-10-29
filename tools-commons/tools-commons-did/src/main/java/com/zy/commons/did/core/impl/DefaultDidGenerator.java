package com.zy.commons.did.core.impl;

import com.zy.commons.did.common.Constants;
import com.zy.commons.did.core.DidGenerator;
import com.zy.commons.did.core.SnowflakeAllocator;
import com.zy.commons.did.enums.DidPattern;
import com.zy.commons.did.exception.DidException;
import com.zy.commons.did.utils.PaddingAtomicLong;
import com.zy.commons.did.business.service.WorkerIdAllocate;
import com.zy.commons.lang.utils.TimeUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * 可以用 Spring 的配置类, 使此类加载到 Spring 容器中即可使用
 *
 * step1: 创建 sql 表 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
 *
 * SET FOREIGN_KEY_CHECKS=0;
 * -- ----------------------------
 * -- Table structure for worker_node
 * -- ----------------------------
 * DROP TABLE IF EXISTS `worker_node`;
 * CREATE TABLE `worker_node` (
 *   `id` bigint(20) NOT NULL AUTO_INCREMENT,
 *   `host_name` varchar(255) NOT NULL,
 *   `port` varchar(255) NOT NULL,
 *   `type` varchar(255) NOT NULL,
 *   `launch_date` date NOT NULL,
 *   PRIMARY KEY (`id`)
 * ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
 *
 *
 * step2: 将 DefaultDidGenerator 注入到 Spring 容器
 *   >>>>>> DefaultDidGenerator 可以通过 setDbType() 方法, 修改数据库类型, 通过 setTableName() 方法, 修改表名称
 *
 * step3: 在 classpath 下, 新建一个 config.properties 文件, config.properties 至少要包含以下几项:
 *  did.db.driverClassName=com.mysql.jdbc.Driver
 *  did.db.url=jdbc:mysql://localhost:3306/did?serverTimezone=UTC&characterEncoding=utf8
 *  did.db.username=root
 *  did.db.password=3NMtX+Mh67L/tq0UWqwsYQ==
 *  did.db.key=8TNiqeJQYAZbhRWugW5iig==
 *  did.pattern=1 # 这个表示使用 默认的 {@link DidPattern}
 */
@Slf4j
public class DefaultDidGenerator implements DidGenerator {
    private static final String FORMAT = "{did: %s, timestamp: %s, dataCenterId: %d, workerId: %d, sequence: %d}.";
    private static final long maxDataCenterId = 15L;

    // 时间戳位，如果不配置，代码中默认是31位
    private int timeBits = 31;
    // 机房标识[0-15]，默认为0不启用, 当 dataCenterId 属性被设置时, 启用
    private int dataCenterIdBits = 0;
    @Setter
    private long dataCenterId;
    // 机器位，如果不配置，代码中默认是20位
    private int workerBits = 20;
    // 序列位，如果不配置，代码中默认是12位
    private int sequenceBits = 12;
    // 数据库类型, 如果不配置, 默认是 mysql
    @Setter
    private String dbType = "mysql";
    // 机器码表名，用于每次应用程序启动时, 获取 mysql 生成的自增 id 作为 workerId 的表
    @Setter
    private String tableName = "worker_node";
    // did补零标识，如果不配置代码中默认左补零, true 表示左位补 0 至 19 位, false 表示不补 0
    @Setter
    private boolean leftPaddingWithZero = true;
    // 时间戳计算基准时间，如果不配置代码中默认是 "2020-02-13 12:51:25"
    private String epochStr = "2020-02-13 12:51:25";
    private long epochSeconds;
    private SnowflakeAllocator snowflakeAllocator;
    private long workerId;
    private long sequence;
    private PaddingAtomicLong lastSecond;
    private WorkerIdAllocate workerIdAllocate;

    public DefaultDidGenerator(WorkerIdAllocate workerIdAllocate) {
        this.epochSeconds = TimeUnit.MILLISECONDS.toSeconds(TimeUtils.parseEpochStr2Mills(this.epochStr, TimeUtils.TIME_FORMAT_01));
        this.dataCenterId = -1L;
        this.sequence = -1L;
        this.workerIdAllocate = workerIdAllocate;
    }

    @PostConstruct
    public void init() {
        // 如果设置了 dataCenterIdBits, 则 dataCenterIdBits 占 4 bit; timeBits 减去 1 bit, workerBits 减去 3 bit
        if (this.dataCenterId >= 0L) {
            --this.timeBits;
            this.workerBits -= 3;
            this.dataCenterIdBits = 4;
            if (this.dataCenterId > maxDataCenterId) {
                throw new DidException(String.format("DataCenterId:{%s} is larger than {%d}.", this.dataCenterId, maxDataCenterId));
            }
        }
        this.snowflakeAllocator = new SnowflakeAllocator(this.timeBits, this.dataCenterIdBits, this.workerBits, this.sequenceBits);
        this.workerId = this.workerIdAllocate.allocateWorkerId(this.workerBits, this.tableName, this.dbType);
        if (this.workerId > this.snowflakeAllocator.getMaxWorkerId()) {
            log.warn(String.format("workerId:{%s} is larger than the maxWorkerId:{%s}.", this.workerId, this.snowflakeAllocator.getMaxWorkerId()));
            this.workerId &= this.snowflakeAllocator.getMaxWorkerId();
        }
        this.lastSecond = new PaddingAtomicLong(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        log.info(String.format("initialized bits(1, %s, %s, %s) for workerId: %s.", this.timeBits, this.workerBits, this.sequenceBits, this.workerId));
    }

    @Override
    public String getDid() throws DidException {
        String did = String.valueOf(this.nextId());
        if (!this.leftPaddingWithZero) {
            return did;
        }
        // 填充至 19 位
        return StringUtils.leftPad(did, Constants.SIZE, Constants.ZERO);
    }

    @Override
    public String parseDid(String did) {
        long lDid = Long.parseLong(did);
        long headBits = (long)this.snowflakeAllocator.getHeadBits();
        long timestampBits = (long)this.snowflakeAllocator.getTimestampBits();
        long dataCenterIdBits = (long)this.snowflakeAllocator.getDataCenterIdBits();
        long workerIdBits = (long)this.snowflakeAllocator.getWorkerIdBits();
        long sequenceBits = (long)this.snowflakeAllocator.getSequenceBits();
        long sequence = lDid << (int)(SnowflakeAllocator.TOTAL_BITS - sequenceBits) >>> (int)(SnowflakeAllocator.TOTAL_BITS - sequenceBits);
        long workerId = lDid << (int)(dataCenterIdBits + timestampBits + headBits) >>> (int)(SnowflakeAllocator.TOTAL_BITS - workerIdBits);
        long deltaSeconds = lDid >>> (int)(dataCenterIdBits + workerIdBits + sequenceBits);
        String thatTimeStr = TimeUtils.parseMillsTimestamp2Str(TimeUnit.SECONDS.toMillis(this.epochSeconds + deltaSeconds), TimeUtils.TIME_FORMAT_01);
        long dataCenter = -1;
        if (dataCenterIdBits > 0L) {
            dataCenter = lDid << (int)(timestampBits + headBits) >>> (int)(SnowflakeAllocator.TOTAL_BITS - dataCenterIdBits);
        }
        return String.format(FORMAT, did, thatTimeStr, dataCenter, workerId, sequence);
    }

    private synchronized long nextId() {
        this.sequence = this.sequence + 1L & this.snowflakeAllocator.getMaxSequence();
        if (this.sequence == 0L) {
            long currentSecond = this.lastSecond.incrementAndGet();
            if (currentSecond - this.epochSeconds > this.snowflakeAllocator.getMaxDeltaSeconds()) {
                throw new DidException(String.format("failed to generate did, currentSecond is %s.", currentSecond));
            }
        }
        return this.snowflakeAllocator.allocate(this.lastSecond.get() - this.epochSeconds, this.dataCenterId, this.workerId, this.sequence);
    }

    public void setTimeBits(int timeBits) {
        if (timeBits > 0) {
            this.timeBits = timeBits;
        }
    }

    public void setWorkerBits(int workerBits) {
        if (workerBits > 0) {
            this.workerBits = workerBits;
        }
    }

    public void setSequenceBits(int sequenceBits) {
        if (sequenceBits > 0) {
            this.sequenceBits = sequenceBits;
        }
    }

    public void setDataCenterIdBits(int dataCenterIdBits) {
        if (dataCenterIdBits > 0) {
            this.dataCenterIdBits = dataCenterIdBits;
        }
    }

    public void setEpochStr(String epochStr) {
        if (StringUtils.isNotBlank(epochStr)) {
            this.epochStr = epochStr;
            this.epochSeconds = TimeUnit.MILLISECONDS.toSeconds(TimeUtils.parseEpochStr2Mills(epochStr, TimeUtils.TIME_FORMAT_01));
        }
    }
}
