package com.cdzg.money.exception;

import com.alibaba.dubbo.common.utils.ReflectUtils;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.rpc.*;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.cdzg.money.api.channel.BaseResponse;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @Description: 渠道服务异常信息过滤器
 * @Author : pc.huang
 * @Date : 2019-02-26 14:51
 */
@Slf4j
public class ChannelFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        try {
            Result result = invoker.invoke(invocation);
            if (result.hasException() && GenericService.class != invoker.getInterface()) {
                try {
                    Throwable exception = result.getException();
                    String className = exception.getClass().getName();
                    if (className.startsWith("com.cdzg.money.exception.ChannelException")) {
                        ChannelException businessException = (ChannelException) exception;
                        BaseResponse baseResponse = new BaseResponse();
                        baseResponse.setResultCode(businessException.getCode());
                        baseResponse.setResultMessage(businessException.getMsg());
                        return new RpcResult(baseResponse);
                    }
                    if (!(exception instanceof RuntimeException) && exception instanceof Exception) {
                        return result;
                    } else {
                        try {
                            Method method = invoker.getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
                            Class<?>[] exceptionClassses = method.getExceptionTypes();
                            Class[] arr$ = exceptionClassses;
                            int len$ = exceptionClassses.length;

                            for (int i$ = 0; i$ < len$; ++i$) {
                                Class<?> exceptionClass = arr$[i$];
                                if (exception.getClass().equals(exceptionClass)) {
                                    return result;
                                }
                            }
                        } catch (NoSuchMethodException var11) {
                            return result;
                        }

                        log.error("Got unchecked and undeclared exception which called by " + RpcContext.getContext().getRemoteHost() + ". service: " + invoker.getInterface().getName() + ", method: " + invocation.getMethodName() + ", exception: " + exception.getClass().getName() + ": " + exception.getMessage(), exception);
                        String serviceFile = ReflectUtils.getCodeBase(invoker.getInterface());
                        String exceptionFile = ReflectUtils.getCodeBase(exception.getClass());
                        if (serviceFile != null && exceptionFile != null && !serviceFile.equals(exceptionFile)) {
                            if (!className.startsWith("java.") && !className.startsWith("javax.")) {
                                return (Result) (exception instanceof RpcException ? result : new RpcResult(new RuntimeException(StringUtils.toString(exception))));
                            } else {
                                return result;
                            }
                        } else {
                            return result;
                        }
                    }
                } catch (Throwable var12) {
                    log.warn("Fail to ExceptionFilter when called by " + RpcContext.getContext().getRemoteHost() + ". service: " + invoker.getInterface().getName() + ", method: " + invocation.getMethodName() + ", exception: " + var12.getClass().getName() + ": " + var12.getMessage(), var12);
                    return result;
                }
            } else {
                return result;
            }
        } catch (RuntimeException var13) {
            log.error("Got unchecked and undeclared exception which called by " + RpcContext.getContext().getRemoteHost() + ". service: " + invoker.getInterface().getName() + ", method: " + invocation.getMethodName() + ", exception: " + var13.getClass().getName() + ": " + var13.getMessage(), var13);
            throw var13;
        }
    }
}
