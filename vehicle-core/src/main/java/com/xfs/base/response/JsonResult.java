package com.xfs.base.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JsonResult {
    //内部状态码、提示消息、具体数据
    private Integer code;
    private String message;
    private Object data;

    /** 1.针对于有具体数据返回的控制器方法 */
    public JsonResult(StatusCode statusCode, Object data) {
        this.code = statusCode.getCode();
        this.message = statusCode.getMessage();
        this.data = data;
    }


    /** 2.针对于没有具体数据返回的控制器方法 */
    public JsonResult(StatusCode statusCode) {
        this.code = statusCode.getCode();
        this.message = statusCode.getMessage();
    }
    /* 静态方法可以被类名直接调用，方便我们提供一致的返回结构，且无需在其它控制器或异常处理器中重复定义 */
    /** 3.静态方法1:在操作成功的场景下,针对于有具体数据返回的控制器方法 */
    public static JsonResult ok(Object data){
        return new JsonResult(StatusCode.OPERATION_SUCCESS, data);
    }
    /** 4.静态方法2:在操作成功的场景下,针对于没有具体数据返回的控制器方法 */
    public static JsonResult ok(){
        return ok(null);
    }
    /** 5.静态方法3:在操作失败的场景下,由全局异常处理器中使用的方法 */
    public static JsonResult fail(Object data){
        return new JsonResult(StatusCode.OPERATION_FAILED, data);
    }
}
