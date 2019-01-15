package org.tron.trongrid.transfers;

import com.alibaba.fastjson.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.tron.trongrid.QueryFactory;
import org.tron.trongrid.TransactionTriggerEntity;

@RestController
@Component
@PropertySource("classpath:tronscan.properties")
public class TransferController {
  @Autowired(required = false)
  MongoTemplate mongoTemplate;

  @RequestMapping(method = RequestMethod.GET, value = "/totaltransfers")
  public Long totaltransfers() {
    QueryFactory query = new QueryFactory();
    query.setTransferType();
    return new Long(mongoTemplate.count(query.getQuery(), TransactionTriggerEntity.class));
  }

  @RequestMapping(method = RequestMethod.GET, value = "/totaltransfers/{address}")
  public Long addressTotaltransfers(
      @PathVariable String address
  ) {
    QueryFactory query = new QueryFactory();
    query.findAllTransferByAddress(address);
    return mongoTemplate.count(query.getQuery(), TransactionTriggerEntity.class);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/transfers")
  public JSONObject getTransfers(
      /******************* Page Parameters ****************************************************/
      @RequestParam(value = "limit", required = false, defaultValue = "25") int limit,
      @RequestParam(value = "count", required = false, defaultValue = "true") boolean count,
      @RequestParam(value = "sort", required = false, defaultValue = "-timeStamp") String sort,
      @RequestParam(value = "start", required = false, defaultValue = "0") int start,
      /****************** Filter parameters *****************************************************/
      @RequestParam(value = "from", required = false, defaultValue = "") String from,
      @RequestParam(value = "to", required = false, defaultValue = "") String to,
      @RequestParam(value = "token", required = false, defaultValue = "") String token

  ) {
    QueryFactory query = new QueryFactory();
    query.setTransferType();
    if (from.length() != 0) {
      query.setTransactionFromAddr(from);
    }

    if (to.length() != 0) {
      query.setTransactionToAddr(to);
    }

    if (token.length() != 0) {
      query.setTransactionToken(token);
    }

    query.setPageniate(QueryFactory.setPagniateVariable(start, limit, sort));

    List<TransactionTriggerEntity> queryResult = mongoTemplate.find(query.getQuery(), TransactionTriggerEntity.class);
    Map map = new HashMap();
    map.put("data", queryResult);
    if (count == true) {
      map.put("result", queryResult.size());
    }

    return new JSONObject(map);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/transfers/{hash}")
  public JSONObject getTrnasferbyHash(
      @PathVariable String hash
  ) {
    QueryFactory query = new QueryFactory();
    query.setTransactionIdEqual(hash);
    List<TransactionTriggerEntity> queryResult = mongoTemplate.find(query.getQuery(),
        TransactionTriggerEntity.class);
    if (queryResult.size() == 0) {
      return null;
    }
    Map map = new HashMap();
    map.put("transaction", queryResult.get(0));
    return new JSONObject(map);
  }
}
