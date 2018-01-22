package com.zheng.futureclass.rpc.service.impl;

import com.zheng.common.annotation.BaseService;
import com.zheng.common.base.BaseServiceImpl;
import com.zheng.futureclass.dao.mapper.CmsMenuMapper;
import com.zheng.futureclass.dao.model.CmsMenu;
import com.zheng.futureclass.dao.model.CmsMenuExample;
import com.zheng.futureclass.rpc.api.FutureclassMenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@Transactional
@BaseService
public class FutureclassMenuServiceImpl extends BaseServiceImpl<CmsMenuMapper, CmsMenu, CmsMenuExample> implements FutureclassMenuService {

private static final Logger LOGGER = LoggerFactory.getLogger(FutureclassMenuServiceImpl.class);

    @Autowired
    CmsMenuMapper cmsMenuMapper;
}
