package logic.async_await;


public interface OnAsyncDoneRsObjListener {

    <T> void onDone(T obj);
}