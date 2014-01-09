
package com.online.casino.aop;

public aspect SystemArchitecture {
    pointcut webLayerCall()
        : call(* com.online.casino.web..*.*(..));

    pointcut inWebLayer()
        : within(com.online.casino.web..*);

    pointcut inBootstrapLayer()
        : within(com.online.casino.bootstrap..*);

    pointcut serviceLayerCall()
        : call(* com.online.casino.service..*.*(..));

    pointcut inServiceLayer()
         : within(com.online.casino.service..*);

    pointcut inPhyshunLayer()
         : within(com.online.casino.physhun..*);

    pointcut repositoryLayerCall()
        : call(* com.online.casino.domain..*.*(..));

    pointcut staticRepositoryLayerCall()
        : call(public static * com.online.casino.domain.entity..*.*(..));

    pointcut inRepositoryLayer()
        : within(com.online.casino.domain.entity..*);

    pointcut inTestLayer()
        : within(com.online.casino.test..*);

    pointcut dataLayerCall()
        : JDBCPointcuts.jdbcCall()
          || JPAPointcuts.jpaCall()
          || SpringPointcuts.jdbcTemplateCall();

    pointcut entityOp()
        : execution(public static * (@javax.persistence.Entity *).*(..));

    pointcut repositoryOp()
        : execution(* (@org.springframework.stereotype.Repository *).*(..));

    pointcut controllerOp()
        : execution(* (@org.springframework.stereotype.Controller *).*(..));

    pointcut serviceOp()
        : execution(* (@org.springframework.stereotype.Service *).*(..));

    pointcut cometServiceOp()
        : execution(* (@org.cometd.annotation.Service *).*(..));

    pointcut inAnnotationType()
        : entityOp() || repositoryOp() || serviceOp() || controllerOp() || cometServiceOp();
}