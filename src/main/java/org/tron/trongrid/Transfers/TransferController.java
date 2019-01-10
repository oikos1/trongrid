package org.tron.trongrid.Transfers;

import com.alibaba.fastjson.JSONObject;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.tron.trongrid.ContractEvenTriggerEntity;
import org.tron.trongrid.QueryFactory;
import org.tron.trongrid.TransactionTriggerEntity;

@RestController
@Component
@PropertySource("classpath:tronscan.properties")
public class TransferController {

    @Value("${url.transfers}")
    private String url;

    @Autowired(required = false)
    MongoTemplate mongoTemplate;

    @RequestMapping(method = RequestMethod.GET, value = "/totaltransfers")
    public Long totaltransfers() {
        QueryFactory query = new QueryFactory();
        query.likeEventSignature("Transfer");
        List<ContractEvenTriggerEntity> tmp = mongoTemplate.find(query.getQuery(), ContractEvenTriggerEntity.class);
        return null;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/transfers")
    public JSONArray getTransfers(
/******************* Page Parameters ****************************************************/
            @RequestParam(value="limit", required=false, defaultValue = "40" ) int limit,
            @RequestParam(value="count", required=false, defaultValue = "true" ) boolean count,
            @RequestParam(value="sort", required=false, defaultValue = "-timestamp") String sort,
            @RequestParam(value="start", required=false, defaultValue = "0") Long start,
            @RequestParam(value="total", required=false, defaultValue = "0") Long total,
/****************** Filter parameters *****************************************************/
            @RequestParam(value="address", required=false, defaultValue = "") String address,
            @RequestParam(value="from", required=false, defaultValue = "") String from,
            @RequestParam(value="to", required=false, defaultValue = "") String to,
            @RequestParam(value="token", required=false, defaultValue = "") String token

    ){

        String url = String.format("%s?limit=%d&sort=%s&count=%b&start=%d&total=%d",
                this.url,limit,sort,count,start,total);
        if (address != null && address.length() > 0)
            url = String.format("%s&address=%s", url, address);
        if (from != null && from.length() > 0)
            url = String.format("%s&from=%s", url, from);
        if (to != null && to.length() > 0)
            url = String.format("%s&to=%s", url, to);
        if(token != null && token.length() > 0)
            url = String.format("%s&token=%s", url, token);

        JSONObject result = this.getResponse(url);
        return result.getJSONArray("data");
    }

    @RequestMapping(method = RequestMethod.GET, value = "/transfers/{hash}")
    public JSONObject getTrnasferbyHash(
            @PathVariable String hash
    ){
        String url = String.format("%s/%s",this.url,hash);
        JSONObject result = this.getResponse(url);
        return result;
    }

    private JSONObject getResponse(String url){
        System.out.println(url);
        RestTemplate restTemplate = new RestTemplate();
        return JSON.parseObject(restTemplate.getForObject(url, String.class));
    }


}
