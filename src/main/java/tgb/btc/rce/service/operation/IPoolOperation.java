package tgb.btc.rce.service.operation;

import tgb.btc.rce.vo.PoolOperation;

public interface IPoolOperation {

    void process(PoolOperation poolOperation);

    String getOperationName();
}
