package tgb.btc.rce.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tgb.btc.library.vo.web.PoolDeal;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PoolOperation {

    private String operation;

    private List<PoolDeal> poolDeals;

    private String data;
}
