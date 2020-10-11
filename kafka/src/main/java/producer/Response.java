package producer;

import java.util.HashMap;
import java.util.Map;

/**
 * producer消息返回结果类
 */
public class Response {
    private boolean success=false;
    private Map<String,String> resultMap=new HashMap<>();

    public Response() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void putResult(String name,String value){
        this.resultMap.put(name,value);
    }

    public String getResult(String name){
        return this.resultMap.getOrDefault(name, null);
    }
}
