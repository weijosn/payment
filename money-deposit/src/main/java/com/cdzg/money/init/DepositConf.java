package com.cdzg.money.init;

import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@PropertySource("classpath:/application.properties")
@Setter
@Getter
public class DepositConf {

}
