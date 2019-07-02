package entity;

import java.io.Serializable;

/**
 * @ClassName:Result
 * @Author：Mr.lee
 * @DATE：2019/06/23
 * @TIME： 21:40
 * @Description: TODO
 */
public class Result implements Serializable {


    //用来响应给前端，成功与否的信息
    private boolean success;  //判断成功与否
    private String message;  //响应的信息

    public Result() {
    }

    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
