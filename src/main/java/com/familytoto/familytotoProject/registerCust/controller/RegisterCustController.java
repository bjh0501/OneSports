package com.familytoto.familytotoProject.registerCust.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.familytoto.familytotoProject.registerCust.domain.CustVO;
import com.familytoto.familytotoProject.registerCust.domain.RegisterCustVO;
import com.familytoto.familytotoProject.registerCust.service.CaptchaService;
import com.familytoto.familytotoProject.registerCust.service.CustService;
import com.familytoto.familytotoProject.registerCust.service.RegisterCustService;

@Controller
public class RegisterCustController {
	@Autowired
	RegisterCustService registerCustService;
	
	@Autowired
	CustService custService;
	
	@Autowired
	CaptchaService captchaService;
	
	@RequestMapping("/registerCust")
    public String registerCust(HttpServletRequest request) {
        return "loginInfo/registerCust";
    }
	
	@RequestMapping("/registerCust/service")
	public String aa() {
		return "loginInfo/service/termsOfService";
	}
	
	@RequestMapping(value = "/registerCust/register", method = RequestMethod.POST)
	@ResponseBody
	public int insertRegister(@ModelAttribute RegisterCustVO rcVo, @ModelAttribute CustVO cVo, 
			HttpServletRequest request, HttpSession session) throws Exception {
		int nCaptchaResult = captchaService.isRight(session, request);
		
		rcVo.setRegIp(request.getRemoteAddr());

		Map<String, Object> custDupleId = custService.checkId(cVo);
		Map<String, Object> custDupleNickname = registerCustService.checkNickname(rcVo);
		
		int nResult=0;
		
		if(nCaptchaResult == 0) { // 틀린캡챠
			nResult = -99;
		} else if(custDupleId != null) { // 중복 아이디
			nResult = -98;
		} else if(custDupleNickname != null) { // 중복 닉네임
			nResult = -97;
		} else if(cVo.getCustPassword().length() < 4 || cVo.getCustPassword().length() > 20) {
			nResult = -96;
		} else {
			
			// 트랜잭션 걸어야함
			if(rcVo.getFamilyCustRecommend() != null && !rcVo.getFamilyCustRecommend().equals("")) {
				CustVO checkVo = new CustVO();
				checkVo.setCustId(rcVo.getFamilyCustRecommend());
				
				Map<String, Object> checkDupleRecommend = registerCustService.checkRecommend(checkVo);
				
				// 추천인의 아이디가 존재하지 않으면 1
				if(checkDupleRecommend != null) {
					RegisterCustVO vo = new RegisterCustVO(); 
					vo.setRegIp(request.getRemoteAddr());
					vo.setFamilyCustNo(Integer.parseInt(checkDupleRecommend.get("familyCustNo").toString()));
					registerCustService.insertRecommend(vo);
				} else {
					nResult = 1;
				}
			}
						
			registerCustService.insertRegisterCust(rcVo, request);
			cVo.setFamilyCustNo(rcVo.getFamilyCustNo());
			
			custService.insertCust(cVo, request);
			// 트랜잭션 걸어야함
		}
		
		return nResult;
	}
	
	@RequestMapping("/service/termsOfService")
	public String termsOfService() {
		return "/loginInfo/service/termsOfService";
	}
			
			
	
}