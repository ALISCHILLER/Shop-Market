package com.varanegar.vaslibrary.base;

import com.varanegar.framework.base.VaranegarApplication;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * Created by A.Torabi on 10/1/2018.
 */
@Aspect
public class SubsystemChecker {

    @Before("execution(@com.varanegar.vaslibrary.base.SubsystemType * *(..))")
    public void checkSystemType(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SubsystemType subsystemType = method.getAnnotation(SubsystemType.class);
        SubsystemTypeId typeId = subsystemType.id();
        if (typeId == SubsystemTypeId.Dist && !VaranegarApplication.is(VaranegarApplication.AppId.Dist) ||
                typeId == SubsystemTypeId.HotSales && !VaranegarApplication.is(VaranegarApplication.AppId.HotSales) ||
                typeId == SubsystemTypeId.PreSales && !VaranegarApplication.is(VaranegarApplication.AppId.PreSales) ||
                typeId == SubsystemTypeId.Supervisor && !VaranegarApplication.is(VaranegarApplication.AppId.Supervisor))
            throw new IllegalAccessError("Method invocation is not permitted. Subsystem type is wrong! method " + signature.toShortString() + " is for " + subsystemType.id().name());
    }

    @Before("execution(@com.varanegar.vaslibrary.base.SubsystemTypes * *(..))")
    public void checkSystemTypes(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SubsystemTypes subsystemType = method.getAnnotation(SubsystemTypes.class);
        SubsystemTypeId[] typeIds = subsystemType.ids();
        boolean isMatch = false;
        for (SubsystemTypeId typeId :
                typeIds) {
            if (typeId == SubsystemTypeId.Dist && VaranegarApplication.is(VaranegarApplication.AppId.Dist) ||
                    typeId == SubsystemTypeId.HotSales && VaranegarApplication.is(VaranegarApplication.AppId.HotSales) ||
                    typeId == SubsystemTypeId.PreSales && VaranegarApplication.is(VaranegarApplication.AppId.PreSales) ||
                    typeId == SubsystemTypeId.Supervisor && VaranegarApplication.is(VaranegarApplication.AppId.Supervisor))
                isMatch = true;
        }
        if (!isMatch)
            throw new IllegalAccessError("Method invocation is not permitted. Subsystem type is wrong! method " + signature.toShortString() + " is not for application with AppId = " + VaranegarApplication.getInstance().getAppId().toString());

    }


}
