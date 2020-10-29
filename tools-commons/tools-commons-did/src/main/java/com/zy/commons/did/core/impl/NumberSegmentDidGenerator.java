package com.zy.commons.did.core.impl;

import com.zy.commons.did.business.service.NumberSegmentAllocate;
import com.zy.commons.did.core.DidGenerator;
import com.zy.commons.did.enums.DidPattern;
import com.zy.commons.did.exception.DidException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * 可以用 Spring 的配置类, 使此类加载到 Spring 容器中即可使用
 *
 * step1: 创建 sql 表 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
 * SET FOREIGN_KEY_CHECKS=0;
 *
 * -- ----------------------------
 * -- Table structure for number_segment
 * -- ----------------------------
 * DROP TABLE IF EXISTS `number_segment`;
 * CREATE TABLE `number_segment` (
 *   `id` bigint(20) NOT NULL AUTO_INCREMENT,
 *   `host_name` varchar(255) DEFAULT NULL,
 *   `port` varchar(255) DEFAULT NULL,
 *   `type` varchar(255) DEFAULT NULL,
 *   PRIMARY KEY (`id`)
 * ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
 *
 * step2: 将 NumberSegmentDidGenerator 注入到 Spring 容器
 *   >>>>>> NumberSegmentDidGenerator 可以通过 setDbType() 方法, 修改数据库类型, 通过 setTableName() 方法, 修改表名称
 *
 * step3: 在 classpath 下, 新建一个 config.properties 文件, config.properties 至少要包含以下几项:
 *  did.db.driverClassName=com.mysql.jdbc.Driver
 *  did.db.url=jdbc:mysql://localhost:3306/did?serverTimezone=UTC&characterEncoding=utf8
 *  did.db.username=root
 *  did.db.password=3NMtX+Mh67L/tq0UWqwsYQ==
 *  did.db.key=8TNiqeJQYAZbhRWugW5iig==
 *  did.pattern=2 # 这个表示使用 默认的 {@link DidPattern}
 */
@Slf4j
public class NumberSegmentDidGenerator implements DidGenerator {

    private static final long LOG_EXP_INTERNAL = 500L;
    private static final long EXP_SLEEP_INTERNAL = 10L;
    private long currentId = 0L;
    private long currentMaxId = 0L;
    private NumberSegmentAllocate numberSegmentAllocate;
    @Setter
    private long segmentMaxValue = 999_999_999_999L;
    @Setter
    private int quantityPerSegment = 1000;
    @Setter
    private boolean needRetry = true;
    @Setter
    private long segmentRetryMillis = 5000L;
    @Setter
    private String dbType = "mysql";
    @Setter
    private String tableName = "number_segment";

    public NumberSegmentDidGenerator(NumberSegmentAllocate numberSegmentAllocate) {
        this.numberSegmentAllocate = numberSegmentAllocate;
    }

    @PostConstruct
    public void init() {
        if (this.quantityPerSegment < 10 || Math.log10((double) this.quantityPerSegment) % 1.0D != 0.0D) {
            log.warn("quantityPerSegment [{}] < 10 或不是 10 的整数次幂, 将其置为了默认值: 1000", this.quantityPerSegment);
            this.quantityPerSegment = 1000;
        }
        if (this.segmentRetryMillis > 5000L) {
            log.warn("segmentRetryMillis [{}] > 5000L, 将其置为了默认值: 5000L", this.segmentRetryMillis);
            this.segmentRetryMillis = 5000L;
        }
    }

    @Override
    public String getDid() throws DidException {
        return Long.toString(nextId());
    }

    @Override
    public String parseDid(String did) {
        long lDid = Long.parseLong(did);
        long segment = lDid / (long) this.quantityPerSegment;
        long sequence = lDid % (long) this.quantityPerSegment;
        return String.format("{did: %s, segment: %d, sequence: %d}.", lDid, segment, sequence);
    }

    private long nextId() {
        if (this.currentId == this.currentMaxId) {
            this.getNextNumberSegmentAndReset();
        }
        ++ this.currentId;
        return this.currentId;
    }

    private void getNextNumberSegmentAndReset() {
        long beginTime = System.currentTimeMillis();
        long lastLogExpTime = 0L;
        boolean maxExp = false;

        while (true) {
            try {
                long segment = this.getNextNumberSegment();
                if (segment > this.segmentMaxValue) {
                    maxExp = true;
                    throw new DidException("号段模式获取 ID 已超过最大范围: " + this.segmentMaxValue);
                }
                this.currentId = (segment - 1L) * (long) this.quantityPerSegment;
                this.currentMaxId = segment * (long)this.quantityPerSegment;
                return;
            } catch (Exception e) {
                if (!this.needRetry) {
                    throw new DidException("号段模式获取 ID 异常.", e);
                }
                lastLogExpTime = this.retryGet(beginTime, lastLogExpTime, maxExp, e);
            }
        }
    }

    private long retryGet(long beginTime, long lastLogExpTime, boolean maxExp, Exception e) {
        try {
            TimeUnit.MILLISECONDS.sleep(10L);
        } catch (InterruptedException ex) {
            // e.printStackTrace();
        }
        long now = System.currentTimeMillis();
        if (now - lastLogExpTime >= 500L) {
            log.error("号段模式获取 ID 异常.", e);
            lastLogExpTime = now;
        }
        if (now - beginTime <= this.segmentRetryMillis && !maxExp) {
            return lastLogExpTime;
        }
        String error;
        if (maxExp) {
            error = "号段超过最大范围[" + this.segmentMaxValue + "]";
        } else {
            error = "号段模式请求 ID 超过最大重试时间[" + this.segmentRetryMillis + "ms]";
        }
        throw new DidException(error, e);
    }

    private long getNextNumberSegment() {
        return this.numberSegmentAllocate.numberSegmentAllocate(this.tableName, this.dbType);
    }
}
