package com.familytoto.familytotoProject.board.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.familytoto.familytotoProject.board.domain.BoardVO;
import com.familytoto.familytotoProject.board.domain.SearchVO;
import com.familytoto.familytotoProject.board.service.BoardService;
import com.familytoto.familytotoProject.comment.domain.CommentVO;
import com.familytoto.familytotoProject.comment.service.CommentService;
import com.familytoto.familytotoProject.registerCust.domain.CustVO;
import com.google.gson.Gson;
import com.nhncorp.lucy.security.xss.XssFilter;

@Controller
@ControllerAdvice
public class BoardController {
	private int nReplyNo = 0;
	
	@Autowired
	BoardService boardService;
	
	@Autowired
	CommentService commentService;
	
	@RequestMapping("/boardList")
    public String boardList(Model model
    		, @RequestParam(required = false, defaultValue = "1") int page
			, @RequestParam(required = false, defaultValue = "1") int range
			, @RequestParam(required = false, defaultValue = "title") String searchType
			, @RequestParam(required = false) String keyword
    		) throws Exception {
		//전체 게시글 개수
		
		SearchVO search = new SearchVO();
		search.setSearchType(searchType);
		search.setKeyword(keyword);
		
		int listCnt = boardService.getBoardListCnt(search);

		search.pageInfo(page, range, listCnt);

	    // Pagination 객체생성		
		search.pageInfo(page, range, listCnt);

		model.addAttribute("pagination", search);
		model.addAttribute("boardList", boardService.getBoardList(search));
		
        return "board/boardList";
    }
	
	@RequestMapping(value = {"/registerBoard"} )
    public ModelAndView registerBoard(HttpSession session,
    		ModelAndView mv, HttpServletRequest request) {
		
		mv.setViewName("/board/registerBoard");
		mv.addObject("loginGubun", session.getAttribute("cust"));

        return mv;
    }
	
	@RequestMapping(value = {"/registerBoard/{boardNo}"} )
    public ModelAndView registerBoard(HttpSession session,
    		ModelAndView mv,
    		@PathVariable("boardNo") int nBoardNo) {
		
		if(nBoardNo != 0 ) {
			mv.addObject("replyNo", nBoardNo);
			nReplyNo = nBoardNo;
		}
		
		mv.setViewName("/board/registerBoard");
		mv.addObject("loginGubun", session.getAttribute("cust"));
		
        return mv;
    }
	
	@RequestMapping("/registerBoard/insert")
    public String insertBoard(@Valid @ModelAttribute BoardVO vo, HttpServletRequest request, HttpSession session) {
		vo.setRegIp(request.getRemoteAddr());
		vo.setBoardReplyNo(nReplyNo);
		
		if(session.getAttribute("cust") != null) {
			CustVO cVo = (CustVO) session.getAttribute("cust");
			vo.setRegCustNo(cVo.getCustNo());
			
			int nResult = boardService.insertCustBoard(vo);
			
			if(nResult==1) {
				return "redirect:/boardList";
			}
		}  
		
		return "-99";
    }
	
	@RequestMapping("/registerBoard/anno/insert")
    public String insertAnnoBoard(@Valid @ModelAttribute BoardVO vo, HttpServletRequest request, HttpSession session) {
		vo.setRegIp(request.getRemoteAddr());
		
		if(session.getAttribute("cust") == null) {
			int nResult = boardService.insertAnnoBoard(vo);
			
			if(nResult == 1) {
				return "redirect:/boardList";
			} else if(nResult == -99) {
				return "-99";
			} else if(nResult == -98) {
				return "-98";
			} else {
				return Integer.toString(nResult);
			}
		} else { // 회원이 익명 글쓰기
			return "-1";
		}
    }
	
	
	// 이 메서드 정리해야함
	@RequestMapping("/showEditor")
    public String showEditor() {
        return "board/editor/showEditor";
    }
	
	
	@RequestMapping("/showBoard/{boardNo}")
    public ModelAndView showBoard(HttpSession session, @PathVariable ("boardNo") String sBoardNo, ModelAndView mv) {
		BoardVO vo = new BoardVO();
		vo.setBoardNo(Integer.parseInt(sBoardNo));
		CustVO custVo = (CustVO) session.getAttribute("cust");
		int nCommentCnt = boardService.getCommentCnt(vo);
		int nCustNo = -1;
		
		vo.setBoardNo(Integer.parseInt(sBoardNo));
		vo = boardService.getShowBoard(vo);
		
		if(custVo != null) {
			nCustNo = custVo.getCustNo();
		} 
				
		List<CommentVO> listCommentVo = commentService.getListComment(vo);

		String sGubun = (String) session.getAttribute("social"); 
		
		if(sGubun != null) {
			if(sGubun.equals("KA")) {
				sGubun = "/img/social/icon/kakaoMiniIcon.jpg";
			} else if(sGubun.equals("FA")) {
				sGubun = "/img/social/icon/facebookMiniIcon.jpg";
			} else if(sGubun.equals("NA")) {
				sGubun = "/img/social/icon/naverMiniIcon.jpg";
			} 
		} else {
			sGubun = "/img/social/icon/onesportsMiniIcon.jpg";
		}

		mv.addObject("cust", nCustNo);
		mv.addObject("board", vo);
		mv.addObject("custComment", custVo);
		mv.addObject("comment", listCommentVo);
		mv.addObject("socialImg", sGubun);
		mv.addObject("commentCnt", nCommentCnt);

		mv.setViewName("board/showBoard");
		
		return mv;
    }	
	
	@RequestMapping("/deleteBoard/{boardNo}")
	public String deleteBoard(HttpSession session, @PathVariable ("boardNo") String sNo, HttpServletRequest request) {
		if(boardService.updateDeleteBoard(sNo, session, request) == 1) {
			return "redirect:/boardList";			
		} else {
			return null;
		}
    }
	
	@RequestMapping("/deleteAnnoBoard/{boardNo}")
	@ResponseBody
    public int deleteAnnoBoard(@PathVariable ("boardNo") String sNo, HttpServletRequest request, @ModelAttribute BoardVO vo) {
		int nResult = boardService.updateDeleteAnnoBoard(sNo, request,vo);
		
		if(nResult == 1) {
			return 0;			
		} else if(nResult == -99) {
			return -99;
		} else {
			return -98;
		}
    }
	
	@RequestMapping("/updateBoard/check")
	@ResponseBody
    public String updateCheckBoard(BoardVO bVo, Model model) {
		CustVO cVo = new CustVO();
		String sOriginalPass = bVo.getBoardAnnoPw();
		
		bVo = boardService.getUpdateBoard(bVo);
		cVo.setCustPassword(sOriginalPass);
		
		if(cVo.isDecodePassword(cVo, bVo.getBoardAnnoPw())) {
			model.addAttribute("board", bVo);
			
	        return "0";
		} else { // 비번틀린경우
			
			return "-98";
		}		
    }
	
	@RequestMapping("/updateBoard/{boardNo}")
	public String showUpdateBoard(@Valid BoardVO bVo, Model model) {
		// 비밀번호 체크 && 리퍼러 체크하기> 실패하면 원래있던 보드로 이동 
		// 성공하면 수정창이동
		BoardVO resultBoardVo = boardService.getUpdateBoard(bVo);
		
		
		model.addAttribute("board", resultBoardVo);
		return "/board/updateBoard";
    }
	
	@RequestMapping("/updateBoard/{boardNo}/update")
	@ResponseBody
	public int updateBoard(@PathVariable ("boardNo") String sBoardNo,
			@Valid @ModelAttribute BoardVO bVo, Model model,
			HttpServletRequest request, HttpSession session) {
		bVo.setChgIp(request.getRemoteAddr());

		return boardService.updateBoard(bVo, session);
    }
	
	@RequestMapping("/board/uploadFile")
	@ResponseBody
    private String boardInsertProc(MultipartHttpServletRequest mtfRequest, HttpServletRequest request) throws Exception{
		List<MultipartFile> fileList = mtfRequest.getFiles("file");
		ArrayList<Map<String, Object>> list = new ArrayList<>(); 
		
		String path = "A:/workspace/OneSports/toto/src/main/resources/static/img/board/temp/";
		File folder = new File(path);
		
		for (MultipartFile mf : fileList) {
			long fileSize = mf.getSize(); // 파일 사이즈
			String originFileName = mf.getOriginalFilename(); // 원본 파일 명			
			
			if(fileSize <= 1024*1024) {
//				System.out.println("originFileName : " + originFileName);
//				System.out.println("fileSize : " + fileSize);

				if (!folder.exists()) {
					try {
						folder.mkdir(); // 폴더 생성합니다.
					} catch (Exception e) {
						e.getStackTrace();
					}
				}
			
				String safeFile = path + System.currentTimeMillis()+"_" + originFileName;
				String sPhysicalPath = request.getSession().getServletContext()
						.getContext("/img/board/temp/").getRealPath("");
						
				Map<String, Object> map = new HashMap<String, Object>();
				
				map.put("imgUrl","/img/board/temp/" + System.currentTimeMillis()+"_" 
				+ originFileName + "?t=" + System.currentTimeMillis());
				map.put("originalFileName", originFileName);
				map.put("fileSize", fileSize);
				list.add(map);
				
				try {
					mf.transferTo(new File(safeFile));
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		Gson gson = new Gson();
		String sPath = gson.toJson(list);
		
		return sPath;
    }
}
