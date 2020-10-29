package com.zy.commons.did.business.service;

public interface WorkerIdAllocate {
    long allocateWorkerId(int workerIdBits, String tableName, String dbType);
}
