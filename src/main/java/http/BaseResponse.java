package http;


/**
 * 网络返回基类 支持泛型
 * Created by Tamic on 2016-06-06.
 */
public class BaseResponse<T> {

    private DBean d;

    public DBean getD() {
        return d;
    }

    public void setD(DBean d) {
        this.d = d;
    }

    private class DBean {

        private int code = 1;
        private int error_code;
        private String msg, error, message;
        private T Data;


        public T getData() {
            return Data;
        }

        public void setData(T data) {
            Data = data;
        }

        public int getError_code() {
            return error_code;
        }

        public void setError_code(int error_code) {
            this.error_code = error_code;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }


        public boolean isOk() {
            return code == 1;
        }


        @Override
        public String toString() {
            return "BaseResponse{" +
                    " msg='" + msg + '\'' +
                    ", error_code='" + error_code + '\'' +
                    ", Data=" + Data + '\'' +
                    ", Data=" + Data +
                    '}';
        }
    }
}
