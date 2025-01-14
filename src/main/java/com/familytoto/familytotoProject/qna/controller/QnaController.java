package com.familytoto.familytotoProject.qna.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.familytoto.familytotoProject.qna.domain.QnaVO;
import com.familytoto.familytotoProject.qna.service.QnaService;
import com.familytoto.familytotoProject.registerCust.domain.CustVO;

@Controller
public class QnaController {
	@Autowired
	QnaService qnaService;
	
	@RequestMapping("/qna")
    public String qna(HttpSession session, Model model, QnaVO vo) {
		CustVO cVo = (CustVO) session.getAttribute("cust");
		
		model.addAttribute("qnaList", qnaService.listQna(vo));
		
		
		if(cVo != null) {
			model.addAttribute("cust", cVo);
			model.addAttribute("annoGubun", "N");
		} else {
			model.addAttribute("annoGubun", "Y");
		}
		
        return "/board/qna";
    }
	
	@RequestMapping("/qna/register")
	@ResponseBody
    public int qnaRegister(@Valid @ModelAttribute QnaVO vo, HttpSession session, HttpServletRequest request) {
		CustVO cVo = (CustVO)session.getAttribute("cust");

		if(cVo == null) {
			return qnaService.insertAnnoQna(vo,request);
		} else {
			return qnaService.insertCustQna(vo, session, request);
		}
    }
	
	@RequestMapping("/qna/answer")
	@ResponseBody
    public int qnaAnswer(@ModelAttribute QnaVO vo,
    		HttpSession session,
    		HttpServletRequest request) {
		CustVO cVo = (CustVO)session.getAttribute("cust");

		if(cVo == null) {
			return -99;
		} else if(cVo.getCustOperatorGubun().equals("N")) {
			return -98;
		}
		
		if(vo.getQnaAdminContents().trim().equals("")) {
			return -97;
		}
		
		vo.setQnaAdminNo(cVo.getCustNo());
		vo.setQnaAdminIp(request.getRemoteAddr());
		
		return qnaService.updateAnswerQna(vo);
    }
}
