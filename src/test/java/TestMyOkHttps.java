import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.shopee.killer.ShopeeKillerApp;
import com.shopee.killer.config.ShopeeReqParamsConfig;
import com.shopee.killer.service.ItemService;
import com.shopee.killer.utils.HttpUtils;
import com.shopee.killer.utils.MyOkHttps;
import org.apache.http.client.HttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShopeeKillerApp.class)
public class TestMyOkHttps {

    @Autowired
    private ItemService itemService;

    @Test
    public void testMyOkHttpsClient() throws UnrecoverableKeyException, CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        MyOkHttps myOkHttps = MyOkHttps.getInstance();
        String url = "https://www.baidu.com";
        String res = myOkHttps.doGet(url, null, null);
        System.out.println(res);
    }

    @Test
    public void testGetReqHeaderParams() throws Exception {
//        String afAcEncDatRes = HttpUtils.sendGetRequest(ShopeeReqParamsConfig.AF_AC_ENC_DAT_URL);
//        afAcEncDatRes = afAcEncDatRes.replace("\"", "");
//        System.out.println(afAcEncDatRes);
//
//        String envInfoParams = "{\"env_info\": \"" + afAcEncDatRes + "\"}";
//        String afAcEncIdRes = HttpUtils.sendPostRequest(ShopeeReqParamsConfig.AF_AC_ENC_ID_URL, null, envInfoParams);
//        afAcEncIdRes = afAcEncIdRes.replace("\"", "");
//        System.out.println(afAcEncIdRes);
//
//        String afAcEncSzToken = HttpUtils.sendGetRequest(ShopeeReqParamsConfig.AF_AC_ENC_SZ_TOKEN_URL);
//        afAcEncSzToken = afAcEncSzToken.replace("\"", "");
//        System.out.println(afAcEncSzToken);

        String apmPidStr = "shopee/@shopee-rn/product-page/PRODUCT_PAGE";
        String encodeApmPidStr = URLEncoder.encode(apmPidStr, "UTF-8");
        String detail_url = "https://mall.shopee.sg/api/v4/pdp/get?" +
                "_pft=7&" +
                "apm_fs=true&" +
                "apm_p=7&" +
                "apm_pid=" + encodeApmPidStr + "&" +
                "apm_ts=" + System.currentTimeMillis() + "&" +
                "from_source=shop&" +
                "item_id=" + "21677339942" + "&" +
                "pdp_type=0&" +
                "shop_id=" + "503612196";
        String reqDefenseParams = "{\"detail_url\": \"" + detail_url + "\"}";
        String reqDefense = HttpUtils.sendPostRequest(ShopeeReqParamsConfig.GET_REQ_DEFENCE_URL, null, reqDefenseParams);
        System.out.println(reqDefense);
        reqDefense = reqDefense.replaceAll("\\p{C}", "");
//        reqDefense = new String(reqDefense.getBytes(), java.nio.charset.StandardCharsets.UTF_8);
        // 将字符串解析为JSONObject
        JSONObject reqDefenseJsonObj = JSON.parseObject(reqDefense);
        // 将JSONObject转换为Map
        Map<String, String> params = JSONObject.parseObject(reqDefenseJsonObj.toString(), new TypeReference<Map<String, String>>(){});
        for (Map.Entry<String, String> entry : params.entrySet()) {
            System.out.println("键：" + entry.getKey());
            System.out.println("值：" + entry.getValue());
        }
    }

    @Test
    public void testGetShopeeItemDetail() throws Exception {
        itemService.getOneItemDetail("24617292946", "960661812");
    }
}
