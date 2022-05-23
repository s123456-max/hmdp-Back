package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public Result getOrderList() {
        List<ShopType> typeList = new ArrayList<>();
        for(String s : stringRedisTemplate.opsForList().range("shop:type",0,-1)){
            JSONObject jsonObject = JSONUtil.parseObj(s);
            ShopType bean = JSONUtil.toBean(jsonObject,ShopType.class);
            typeList.add(bean);
        }
        if(typeList.size()!=0){
            return Result.ok(typeList);
        }
        List<ShopType> shopTypeList = query().orderByAsc("sort").list();
        if(shopTypeList.size()==0){
            return Result.ok("暂无数据");
        }
        for(ShopType element : shopTypeList){
            stringRedisTemplate.opsForList().rightPush("shop:type", JSONUtil.toJsonStr(element));
        }
        return Result.ok(shopTypeList);
    }
}
