package com.familytoto.familytotoProject.basket.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.familytoto.familytotoProject.basket.dao.BasketDAO;
import com.familytoto.familytotoProject.basket.domain.BasketVO;
import com.familytoto.familytotoProject.productbuy.domain.ProductBuyVO;

@Service
public class BasketServiceImpl implements BasketService{
	@Autowired
	BasketDAO basketDao;
	
	@Override
	public int insertBasket(BasketVO vo) {
		if(basketDao.isCheckBeforeBasket(vo) == false) {
			return basketDao.insertBasket(vo);			
		} else {
			return -95;
		}
	}

	@Override
	public boolean checkProductAmount(ProductBuyVO vo) {
		return basketDao.checkProductAmount(vo);
	}

	@Override
	public List<BasketVO> listBasket(int familyCustNo) {
		return basketDao.listBasket(familyCustNo);
	}
}