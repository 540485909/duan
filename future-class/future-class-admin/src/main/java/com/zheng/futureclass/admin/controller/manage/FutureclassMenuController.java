package com.zheng.futureclass.admin.controller.manage;

import com.baidu.unbiz.fluentvalidator.ComplexResult;
import com.baidu.unbiz.fluentvalidator.FluentValidator;
import com.baidu.unbiz.fluentvalidator.ResultCollectors;

import com.zheng.common.base.BaseController;
import com.zheng.common.validator.LengthValidator;
import com.zheng.futureclass.common.constant.CmsResult;
import com.zheng.futureclass.common.constant.CmsResultConstant;
import com.zheng.futureclass.dao.model.CmsMenu;
import com.zheng.futureclass.dao.model.CmsMenuExample;
import com.zheng.futureclass.rpc.api.FutureclassMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 菜单控制器
 * Created by shuzheng on 2017/3/18.
 */
@Controller
@Api(value = "菜单管理", description = "菜单管理")
@RequestMapping("/manage/menu")
public class FutureclassMenuController extends BaseController {

	private static final Logger LOGGER = LoggerFactory.getLogger(FutureclassMenuController.class);
	
	@Autowired
	private FutureclassMenuService futureclasssMenuService;

	@ApiOperation(value = "评论首页")
	@RequiresPermissions("cms:menu:read")
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index() {
		return "/manage/menu/index.jsp";
	}

	@ApiOperation(value = "评论列表")
	@RequiresPermissions("cms:menu:read")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public Object list(
			@RequestParam(required = false, defaultValue = "0", value = "offset") int offset,
			@RequestParam(required = false, defaultValue = "10", value = "limit") int limit,
			@RequestParam(required = false, value = "sort") String sort,
			@RequestParam(required = false, value = "order") String order) {
		CmsMenuExample cmsMenuExample = new CmsMenuExample();
		if (!StringUtils.isBlank(sort) && !StringUtils.isBlank(order)) {
			cmsMenuExample.setOrderByClause(sort + " " + order);
		}
		List<CmsMenu> rows = futureclasssMenuService.selectByExampleForOffsetPage(cmsMenuExample, offset, limit);
		long total = futureclasssMenuService.countByExample(cmsMenuExample);
		Map<String, Object> result = new HashMap<>(2);
		result.put("rows", rows);
		result.put("total", total);
		return result;
	}

	@ApiOperation(value = "新增菜单")
	@RequiresPermissions("cms:menu:create")
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String create() {
		return "/manage/menu/create.jsp";
	}

	@ApiOperation(value = "新增菜单")
	@RequiresPermissions("cms:menu:create")
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@ResponseBody
	public Object create(CmsMenu cmsMenu) {
		ComplexResult result = FluentValidator.checkAll()
				.on(cmsMenu.getName(), new LengthValidator(1, 20, "名称"))
				.doValidate()
				.result(ResultCollectors.toComplex());
		if (!result.isSuccess()) {
			return new CmsResult(CmsResultConstant.INVALID_LENGTH, result.getErrors());
		}
		long time = System.currentTimeMillis();
		cmsMenu.setOrders(time);
		int count = futureclasssMenuService.insertSelective(cmsMenu);
		return new CmsResult(CmsResultConstant.SUCCESS, count);
	}

	@ApiOperation(value = "删除菜单")
	@RequiresPermissions("cms:menu:delete")
	@RequestMapping(value = "/delete/{ids}",method = RequestMethod.GET)
	@ResponseBody
	public Object delete(@PathVariable("ids") String ids) {
		int count = futureclasssMenuService.deleteByPrimaryKeys(ids);
		return new CmsResult(CmsResultConstant.SUCCESS, count);
	}

	@ApiOperation(value = "修改菜单")
	@RequiresPermissions("cms:menu:update")
	@RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
	public String update(@PathVariable("id") int id, ModelMap modelMap) {
		CmsMenu menu = futureclasssMenuService.selectByPrimaryKey(id);
		modelMap.put("menu", menu);
		return "/manage/menu/update.jsp";
	}

	@ApiOperation(value = "修改菜单")
	@RequiresPermissions("cms:menu:update")
	@RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
	@ResponseBody
	public Object update(@PathVariable("id") int id, CmsMenu cmsMenu) {
		ComplexResult result = FluentValidator.checkAll()
				.on(cmsMenu.getName(), new LengthValidator(1, 20, "名称"))
				.doValidate()
				.result(ResultCollectors.toComplex());
		if (!result.isSuccess()) {
			return new CmsResult(CmsResultConstant.INVALID_LENGTH, result.getErrors());
		}
		cmsMenu.setMenuId(id);
		int count = futureclasssMenuService.updateByPrimaryKeySelective(cmsMenu);
		return new CmsResult(CmsResultConstant.SUCCESS, count);
	}

	@ApiOperation(value = "上移菜单")
	@RequiresPermissions("cms:menu:up")
	@RequestMapping(value = "/up/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Object up(@PathVariable("id") int id) {
		CmsMenu cmsMenu = futureclasssMenuService.selectByPrimaryKey(id);
		if (null == cmsMenu) {
			return new CmsResult(CmsResultConstant.INVALID_PARAMETER, "无效参数！");
		}
		CmsMenuExample cmsMenuExample = new CmsMenuExample();
		CmsMenuExample.Criteria criteria = cmsMenuExample.createCriteria();
		if (null == cmsMenu.getPid()) {
			criteria.andPidIsNull();
		} else {
			criteria.andPidEqualTo(cmsMenu.getPid());
		}
		criteria.andOrdersLessThan(cmsMenu.getOrders());
		cmsMenuExample.setOrderByClause("orders desc");
		CmsMenu upCmsMenu = futureclasssMenuService.selectFirstByExample(cmsMenuExample);
		if (null == upCmsMenu) {
			return new CmsResult(CmsResultConstant.FAILED, "不能上移了！");
		}
		long tempOrders = upCmsMenu.getOrders();
		upCmsMenu.setOrders(cmsMenu.getOrders());
		cmsMenu.setOrders(tempOrders);
		futureclasssMenuService.updateByPrimaryKeySelective(cmsMenu);
		futureclasssMenuService.updateByPrimaryKeySelective(upCmsMenu);
		return new CmsResult(CmsResultConstant.SUCCESS, 1);
	}

	@ApiOperation(value = "下移菜单")
	@RequiresPermissions("cms:menu:down")
	@RequestMapping(value = "/down/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Object down(@PathVariable("id") int id) {
		CmsMenu cmsMenu = futureclasssMenuService.selectByPrimaryKey(id);
		if (null == cmsMenu) {
			return new CmsResult(CmsResultConstant.INVALID_PARAMETER, "无效参数！");
		}
		CmsMenuExample cmsMenuExample = new CmsMenuExample();
		CmsMenuExample.Criteria criteria = cmsMenuExample.createCriteria();
		if (null == cmsMenu.getPid()) {
			criteria.andPidIsNull();
		} else {
			criteria.andPidEqualTo(cmsMenu.getPid());
		}
		criteria.andOrdersGreaterThan(cmsMenu.getOrders());
		cmsMenuExample.setOrderByClause("orders asc");
		CmsMenu upCmsMenu = futureclasssMenuService.selectFirstByExample(cmsMenuExample);
		if (null == upCmsMenu) {
			return new CmsResult(CmsResultConstant.FAILED, "不能下移了！");
		}
		long tempOrders = upCmsMenu.getOrders();
		upCmsMenu.setOrders(cmsMenu.getOrders());
		cmsMenu.setOrders(tempOrders);
		futureclasssMenuService.updateByPrimaryKeySelective(cmsMenu);
		futureclasssMenuService.updateByPrimaryKeySelective(upCmsMenu);
		return new CmsResult(CmsResultConstant.SUCCESS, 1);
	}

}