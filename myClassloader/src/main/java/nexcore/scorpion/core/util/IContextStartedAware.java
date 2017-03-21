/*
 * Copyright (c) 2012 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with SK
 * C&C.
 */
package nexcore.scorpion.core.util;

import org.springframework.core.Ordered;

/**
 * 업무 그룹명 : nexcore.gemini.commons.lazyinit
 * 서브 업무명 : IContextStartedAware.java
 * 작성자 : alcava00
 * 작성일 : 2012. 6. 21.
 * 설 명 : CamelContext가 완전히 기동완료 되었는지를 Notify 받기위해 implements 해야되는 Interface
 *       Spring의 lazy-init 과는 다른 개념임
 * 
 * @see nexcore.gemini.commons.lazyinit.ContextStartedAwareSupport
 */
public interface IContextStartedAware extends Ordered {
    /**
     * context가 Started 되었을때 처리할 로직
     */
    public void contextStarted();
}
