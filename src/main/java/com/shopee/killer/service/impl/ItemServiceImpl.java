package com.shopee.killer.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.shopee.killer.config.ShopeeReqParamsConfig;
import com.shopee.killer.service.ItemService;
import com.shopee.killer.utils.HttpUtils;
import com.shopee.killer.utils.MyOkHttps;
import com.shopee.killer.utils.Result;
import com.shopee.killer.utils.ShopeeReverseUtils;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * shopee 商品相关信息获取实现类
 */
@Service
public class ItemServiceImpl implements ItemService {
    private static AtomicInteger sequence = new AtomicInteger();

    @Override
    public Result getOneItemDetail(String itemId, String shopId) throws Exception {
        MyOkHttps okHttpClient = MyOkHttps.getInstance();

        String apmPidStr = "shopee/@shopee-rn/product-page/PRODUCT_PAGE";
        String encodeApmPidStr = URLEncoder.encode(apmPidStr, "UTF-8");
        String url = "https://mall.shopee.sg/api/v4/pdp/get?" +
                "_pft=7&" +
                "apm_fs=true&" +
                "apm_p=7&" +
                "apm_pid=" + encodeApmPidStr + "&" +
                "apm_ts=" + System.currentTimeMillis() + "&" +
                "from_source=dd&" +
                "item_id=" + itemId + "&" +
                "pdp_type=0&" +
                "shop_id=" + shopId;
        System.out.println("请求链接：" + url);
        String paramsSplit = url.split("\\?")[1];

        // 获取 af-ac-enc-dat
        String afAcEncDatRes = HttpUtils.sendGetRequest(ShopeeReqParamsConfig.AF_AC_ENC_DAT_URL);
        afAcEncDatRes = afAcEncDatRes.replace("\"", "");
        // 获取 af-ac-enc-id
        String envInfoParams = "{\"env_info\": \"" + afAcEncDatRes + "\"}";
        String afAcEncIdRes = HttpUtils.sendPostRequest(ShopeeReqParamsConfig.AF_AC_ENC_ID_URL, null, envInfoParams);
        afAcEncIdRes = afAcEncIdRes.replace("\"", "");
        // 获取 af-ac-enc-sz-token
        String afAcEncSzToken = HttpUtils.sendGetRequest(ShopeeReqParamsConfig.AF_AC_ENC_SZ_TOKEN_URL);
        afAcEncSzToken = afAcEncSzToken.replace("\"", "");

        // 获取 req_defense 相关的参数，三个键值对是动态的，一个键是 x-sap-ri
        String reqDefenseParams = "{\"detail_url\": \"" + url + "\"}";
        String reqDefense = HttpUtils.sendPostRequest(ShopeeReqParamsConfig.GET_REQ_DEFENCE_URL, null, reqDefenseParams);
        reqDefense = reqDefense.replaceAll("\\p{C}", "");
        JSONObject reqDefenseJsonObj = JSON.parseObject(reqDefense);

        // 请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("referer", "https://mall.shopee.sg/");
        headers.put("x-api-source", "rn");
        headers.put("x-shopee-language", "en");
        headers.put("af-ac-cli-id", "34b3055fd7b9089bae7b8f6ac2f26ca6");  // 应该是和用户相关的信息，不同用户值不一样
        headers.put("if-none-match-", ShopeeReverseUtils.reverseIfNonMatch(paramsSplit));
        headers.put("shopee_http_dns_mode", "1");
//        headers.put("X-Shopee-Client-Timezone", "Asia/Shanghai");
        headers.put("x-shopee-client-timezone", "Asia/Singapore");
        headers.put("client-request-id", ShopeeReverseUtils.genCliRequestID(sequence));
        headers.put("Host", "mall.shopee.sg");
//        headers.put("Connection", "Keep-Alive");
//        headers.put("Accept-Encoding", "gzip");
//        headers.put("Cookie", "SPC_DID=D4tWDP6W1VVX0slfIFQcAAduA4bd64OOIpiha3LS97s=; SPC_F=de750ac18e8fe52f_unknown; SPC_SI=9T8vZgAAAABQWWp2eGhzS539mAoAAAAAcTBmMHJmUlI=; SPC_AFTID=06438199-622e-43a9-aa47-08a544283a26; SPC_CLIENTID=D4tWDP6W1VVX0slfjrucxigaciwpzphu; shopee_app_version=31618; SPC_F=de750ac18e8fe52f_unknown; REC_T_ID=9ccb1b45-26f4-11ef-890e-caae7171df49; userid=1215332210; shopee_token=iy0hljKopEeRfgn88/mApJWAXLqdRXnG7iEnHphvP8vZt8G5GyPJ9xfg/k1YMwxD; username=shengchillie917; shopee_token=iy0hljKopEeRfgn88/mApJWAXLqdRXnG7iEnHphvP8vZt8G5GyPJ9xfg/k1YMwxD; SPC_U=1215332210; SPC_R_T_ID=y/+e17m12fmgr6gzsqWjhJYtMCYvpBc2DPIucRI7VKP/3OvKTL2ywspinj9kBMsxcW/om1c157Ni1NyQVAsYDWYfQUOrgYFrJzUhfXZImCho9DXrpUE8CZQ4669cbCwKw9sJC+qDxJFkgTOrdXvET7f95eblvWbvBnwJd/xitSs=; SPC_R_T_IV=S09rNDRSd0lkdnJRc3BFaA==; SPC_T_ID=y/+e17m12fmgr6gzsqWjhJYtMCYvpBc2DPIucRI7VKP/3OvKTL2ywspinj9kBMsxcW/om1c157Ni1NyQVAsYDWYfQUOrgYFrJzUhfXZImCho9DXrpUE8CZQ4669cbCwKw9sJC+qDxJFkgTOrdXvET7f95eblvWbvBnwJd/xitSs=; SPC_T_IV=S09rNDRSd0lkdnJRc3BFaA==; _gcl_au=1.1.1379744621.1718002235; _ga=GA1.2.1922909839.1718002235; _ga_9GWFVH1DVL=GS1.1.1718002235.1.1.1718002239.56.0.0; language=en; language=en; SPC_B_SI=Jx9oZgAAAABMN2k5aFdWQyrJIwAAAAAAQzdJbUExNWQ=; _fbp=fb.1.1718163167625.728143273955531754; UA=Shopee%20Android%20Beeshop%20locale%2Fen%20version%3D1133%20appver%3D31618; shopee_rn_version=1718351279; SPC_RNBV=6019002; SPC_ST=.UEI2ZVlxOUZ3V2kwRjRuUwkxJWgyV3zQoVUFOJrIzL09sFHkvVGMkz58BaEyxYVveQL+xAvPj7VzbuLb/2xNjX9ShQMkZqX9ZFnVV9DIFUbTH7KQI/cw2DpWh1KsVDccaNey2Tp8PLTPVSdlumeT1mA3lgtn+1NUltsL/+SZqAYL5RyP5ZjWubGaIVjAgV41aRnG4QgZHXkReDzk8Xke8aCDfrsi2p9NHb9ve6elFzA=; SPC_EC=.UEI2ZVlxOUZ3V2kwRjRuUwkxJWgyV3zQoVUFOJrIzL09sFHkvVGMkz58BaEyxYVveQL+xAvPj7VzbuLb/2xNjX9ShQMkZqX9ZFnVV9DIFUbTH7KQI/cw2DpWh1KsVDccaNey2Tp8PLTPVSdlumeT1mA3lgtn+1NUltsL/+SZqAYL5RyP5ZjWubGaIVjAgV41aRnG4QgZHXkReDzk8Xke8aCDfrsi2p9NHb9ve6elFzA=; csrftoken=KAAmAOkkgrThQqj2sdsCaTOf1A5WY3iz");
//        headers.put("Cookie", "SPC_EC=.eHAzV3BIa1hDN05NUmtURmWcf24OWN1uLictEX/UXBbUkTRgMNeVTnu5EekEkWnm4ELd4CbuVwwfW+48kXqIFdEdaNuQE4/dfQyH2LKjNSk0vTioVpe2byjasEJOauamFDlsrAU9ZmPZA3C4njMIMPbMGc5LTZV7hgI93gAWkGHgkGabU9WpkYqL8JM12Eaq5nJ3fe3qF5IcKCfenv7XWBVEKewyzlb/VNvIfx/U5DM=");
        headers.put("cookie", "SPC_DID=c7KYndXOCtMfljpFHR+DMjNqEQzOMGX4aiIUYXK7o6U=;SPC_F=de750ac18e8fe52f_unknown;SPC_SEC_SI=v1-T1VQdjdNOHFRZUNUb2toSV1r808/vzjU6Ep/fdU9x4E+95UEUs4wZRGBkk4kwKGiodLEwPEzDt2jVjZm4W8XOuZGbMbl9mzN1gCmT1JynG8=;REC_T_ID=279b9389-2e05-11ef-a009-66b3170ee9dd;SPC_SI=KR9oZgAAAABoUUlkQUdBVxxRAgIAAAAAeDRDanczZmE=;UA=Shopee%20Android%20Beeshop%20locale%2Fen%20version%3D1133%20appver%3D31618;SPC_AFTID=06438199-622e-43a9-aa47-08a544283a26;SPC_CLIENTID=c7KYndXOCtMfljpFwqwssltyzdbppaxa;language=en;shopee_app_version=31618;shopee_rn_version=1718703834;csrftoken=Ey4d23HTApX8eJ27ZQTl53ZzZdKcfsSo;language=en;SPC_RNBV=6019005;SPC_F=de750ac18e8fe52f_unknown;userid=1215332210;shopee_token=iy0hljKopEeRfgn88/mApDV7bmzKtoA35XjxrOpzoPTg3dFHV/dB2yWOHNu04WwH;username=shengchillie917;shopee_token=iy0hljKopEeRfgn88/mApDV7bmzKtoA35XjxrOpzoPTg3dFHV/dB2yWOHNu04WwH;SPC_U=1215332210;SPC_R_T_ID=gqQKzFGbIYoP7KtDzDGftUKiHPUyxhgJp/4sK56oUCYGNUsycYJpF4WYmIPwr4UPUI22t4VYTDqqX41oARhLxSS77XEbOhhVyKxZsDfK9PThkUVSXSeH2Ik6gIZPd5w4mNEoBJKZa2xGrd34z4G08/0GskLjZgC++1hCgd+HV1A=;SPC_R_T_IV=eHNGbnFpZjRoQU9YRWMwOA==;SPC_T_ID=gqQKzFGbIYoP7KtDzDGftUKiHPUyxhgJp/4sK56oUCYGNUsycYJpF4WYmIPwr4UPUI22t4VYTDqqX41oARhLxSS77XEbOhhVyKxZsDfK9PThkUVSXSeH2Ik6gIZPd5w4mNEoBJKZa2xGrd34z4G08/0GskLjZgC++1hCgd+HV1A=;SPC_T_IV=eHNGbnFpZjRoQU9YRWMwOA==;SPC_B_SI=KR9oZgAAAABoUUlkQUdBV5xSAgIAAAAAZWN1dFJNM2c=;SPC_ST=.SmtlNVZoa2pJcFFFNEpQag5g00zC7b5IL+2lWpBRblawiYMn15ff/mTX1hEwkB952wj7p/ZCXNxTFFLnWIJSR9QPwy/1y+9bjK2P+FgsUsLMwkdZnH48fzakZKu62aejKpVzkmBMhHrAuuKCdetoL+OxpTO/mEs0UnnNooQSvlTyN4lDedgGUgBmDTlFeV+r4tjq1E4gbU45QcVSkV6MMz3dA7WCqw6QxTX0OiDYx1c=;SPC_EC=.SmtlNVZoa2pJcFFFNEpQag5g00zC7b5IL+2lWpBRblawiYMn15ff/mTX1hEwkB952wj7p/ZCXNxTFFLnWIJSR9QPwy/1y+9bjK2P+FgsUsLMwkdZnH48fzakZKu62aejKpVzkmBMhHrAuuKCdetoL+OxpTO/mEs0UnnNooQSvlTyN4lDedgGUgBmDTlFeV+r4tjq1E4gbU45QcVSkV6MMz3dA7WCqw6QxTX0OiDYx1c=;");
        headers.put("user-agent", "Android app Shopee appver=31618 app_type=1 Cronet/102.0.5005.61");
        headers.put("af-ac-enc-dat", afAcEncDatRes);
        headers.put("af-ac-enc-id", afAcEncIdRes);
        headers.put("af-ac-enc-sz-token", afAcEncSzToken);
        Map<String, String> params = JSONObject.parseObject(reqDefenseJsonObj.toString(), new TypeReference<Map<String, String>>() {
        });
        headers.putAll(params);
        headers.put("accept-encoding", "gzip, deflate, br");

        System.out.println(headers);

        String res = okHttpClient.doGet(url, null, headers);
        System.out.println(res);
        return null;
    }
}
